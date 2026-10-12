import { useCallback, useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useToast } from '../context/ToastContext';
import * as authApi from '../api/authApi';
import * as accountApi from '../api/accountApi';
import { Alert, Button, Empty, Field, PageHead, Pager, SelectField, Skeleton, Stamp, TextField } from '../components/ui';
import { Icon } from '../components/Icon';
import { groupAccount, shortDate, titleCase } from '../utils/format';

const TABS = [['customers', 'Customers'], ['kyc', 'KYC review'], ['accounts', 'Accounts'], ['branches', 'Branches']];
const STATUSES = ['ACTIVE', 'DEACTIVATE', 'BLOCKED', 'DORMANT'];
const ROLES = ['USER', 'SUPPORT', 'MANAGER', 'ADMIN'];

/* Fetch a Spring Page<T> and keep the page number */
function usePaged(fetcher, deps) {
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');
  const load = useCallback(() => {
    setError('');
    return fetcher(page).then(setData).catch((e) => setError(e.message));
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [page, ...deps]);
  useEffect(() => { setData(null); load(); }, [load]);
  useEffect(() => { setPage(0); }, deps); // eslint-disable-line react-hooks/exhaustive-deps
  return { page, setPage, data, error, reload: load };
}

export function AdminPage() {
  const [tab, setTab] = useState('customers');
  return (
    <>
      <PageHead title="Bank desk" sub="Manage customers, review KYC and keep branches up to date." />
      <div className="tabs" role="tablist">
        {TABS.map(([id, label]) => <button key={id} role="tab" className="tab" aria-selected={tab === id} onClick={() => setTab(id)}>{label}</button>)}
      </div>
      {tab === 'customers' && <Customers />}
      {tab === 'kyc' && <KycReview />}
      {tab === 'accounts' && <Accounts />}
      {tab === 'branches' && <Branches />}
    </>
  );
}

function Customers() {
  const { isAdmin } = useAuth();
  const toast = useToast();
  const { page, setPage, data, error, reload } = usePaged((p) => authApi.listUsers(p, 8), []);

  const act = async (fn, msg) => { try { await fn(); toast(msg); await reload(); } catch (e) { toast(e.message, 'bad'); } };

  if (error) return <Alert tone="bad">{error}</Alert>;
  if (!data) return <div className="panel panel__body" style={{ display: 'grid', gap: 14 }}><Skeleton h={24} /><Skeleton h={24} /><Skeleton h={24} /></div>;
  return (
    <div className="panel"><div className="panel__body">
      <div className="table-wrap">
        <table className="dtable customer-table">
          <thead><tr><th>Email</th><th>Roles</th><th>Status</th>{isAdmin && <th>Add role</th>}</tr></thead>
          <tbody>
            {data.content.map((u) => (
              <tr key={u.email}>
                <td className="customer-table__email">{u.email}</td>
                <td>{u.roles.map((r) => (
                  <span key={r} className="stamp stamp--idle" style={{ marginRight: 6 }}>
                    {r.toLowerCase()}
                    {isAdmin && u.roles.length > 1 && <button className="link-btn" style={{ textDecoration: 'none' }} aria-label={`Remove ${r} role from ${u.email}`} onClick={() => act(() => authApi.revokeRole(u.email, r), `${titleCase(r)} role removed.`)}>×</button>}
                  </span>
                ))}</td>
                <td>
                  <select className="select" style={{ minHeight: 36, width: 150 }} value={u.status} aria-label={`Status for ${u.email}`}
                    onChange={(e) => act(() => authApi.updateUserStatus(u.email, e.target.value), `${u.email} is now ${e.target.value.toLowerCase()}.`)}>
                    {STATUSES.map((s) => <option key={s} value={s}>{titleCase(s)}</option>)}
                  </select>
                </td>
                {isAdmin && <td>
                  <select className="select" style={{ minHeight: 36, width: 130 }} value="" aria-label={`Add role to ${u.email}`}
                    onChange={(e) => e.target.value && act(() => authApi.assignRole(u.email, e.target.value), `${titleCase(e.target.value)} role added.`)}>
                    <option value="">Choose…</option>
                    {ROLES.filter((r) => !u.roles.includes(r)).map((r) => <option key={r} value={r}>{titleCase(r)}</option>)}
                  </select>
                </td>}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <Pager page={page} totalPages={data.totalPages} onChange={setPage} />
    </div></div>
  );
}

function KycReview() {
  const { isAdmin } = useAuth();
  const toast = useToast();
  const [status, setStatus] = useState('PENDING');
  const { page, setPage, data, error, reload } = usePaged((p) => accountApi.listProfilesByKyc(status, p, 4), [status]);
  const [aadhaar, setAadhaar] = useState('');
  const [found, setFound] = useState(null);
  const [searchError, setSearchError] = useState('');
  const [busy, setBusy] = useState(false);

  const search = async (e) => {
    e.preventDefault();
    if (!/^\d{12}$/.test(aadhaar)) { setSearchError('Aadhaar number has 12 digits.'); setFound(null); return; }
    setBusy(true); setSearchError('');
    try { setFound(await accountApi.findProfileByAadhaar(aadhaar)); } catch (err) { setFound(null); setSearchError(err.message); } finally { setBusy(false); }
  };

  const decide = async (profile, verdict) => {
    try {
      await (verdict === 'verify' ? accountApi.verifyKyc(profile.profileId) : accountApi.rejectKyc(profile.profileId));
      const nextStatus = verdict === 'verify' ? 'VERIFIED' : 'REJECTED';
      toast(`${profile.accountHolderName}: KYC ${verdict === 'verify' ? 'verified' : 'rejected'}.`);
      setFound((current) => current?.profileId === profile.profileId ? { ...current, kycStatus: nextStatus } : current);
      await reload();
    } catch (err) {
      toast(err.message, 'bad');
    }
  };

  return (
    <div className="stack">
      <form className="panel" onSubmit={search} noValidate><div className="panel__body">
        <div className="search-row kyc-search-row">
          <Field label="Find a customer by Aadhaar number" error={searchError}>
            <div className="kyc-search-control">
              <input
                className="input acct"
                inputMode="numeric"
                value={aadhaar}
                maxLength={12}
                aria-label="Find a customer by Aadhaar number"
                aria-invalid={!!searchError}
                onChange={(e) => setAadhaar(e.target.value.replace(/\D/g, '').slice(0, 12))}
              />
              <div className="kyc-search-action">
                <Button type="submit" variant="ghost" size="sm" busy={busy} aria-label="Find customer" title="Find customer">
                  {!busy && <Icon name="search" size={18} />}
                </Button>
              </div>
            </div>
          </Field>
        </div>
        {found && <div style={{ marginTop: 8 }}><KycCard p={found} canDecide={isAdmin} onDecide={decide} /></div>}
      </div></form>

      <section>
        <div className="row row--between" style={{ marginBottom: 12 }}>
          <h2>KYC queue</h2>
          <div className="seg kyc-filters" role="radiogroup" aria-label="Filter by KYC status">
            {['PENDING', 'VERIFIED', 'REJECTED'].map((s) => <label key={s}><input type="radio" name="kycf" checked={status === s} onChange={() => setStatus(s)} style={{ padding: 0 }} />{titleCase(s)}</label>)}
          </div>
        </div>
        {error ? <Alert tone="bad">{error}</Alert>
          : !data ? <div className="panel panel__body"><Skeleton h={90} /></div>
          : data.content.length === 0 ? <div className="panel"><Empty title="Nothing here">No {status.toLowerCase()} profiles right now.</Empty></div>
          : <div className="stack" style={{ gap: 14 }}>{data.content.map((p) => <div className="panel" key={p.profileId || p.phoneNumber}><div className="panel__body"><KycCard p={p} /></div></div>)}
            <Pager page={page} totalPages={data.totalPages} onChange={setPage} /></div>}
      </section>
    </div>
  );
}

function KycCard({ p, canDecide = false, onDecide }) {
  return (
    <div className="kyc-card">
      <div className="kyc-card__head">
        <div className="kyc-card__identity"><h3>{p.accountHolderName}</h3><div className="muted">Born {shortDate(p.dateOfBirth)} · <span className="acct">{p.phoneNumber}</span></div></div>
        <Stamp status={p.kycStatus} />
      </div>
      <div className="kyc-card__address muted">{p.address}, {p.city}, {p.state} <span className="acct">{p.pincode}</span></div>
      {canDecide && p.kycStatus === 'PENDING' && (
        <div className="kyc-card__actions">
          <Button size="sm" onClick={() => onDecide(p, 'verify')}>Verify</Button>
          <Button size="sm" variant="danger" onClick={() => onDecide(p, 'reject')}>Reject</Button>
        </div>
      )}
    </div>
  );
}

function Accounts() {
  const [type, setType] = useState('SAVINGS');
  const { page, setPage, data, error } = usePaged((p) => accountApi.listAccounts(type, p, 6), [type]);
  return (
    <div className="panel"><div className="panel__head accounts__head">
      <h2>All accounts</h2>
      <div className="seg account-filters" role="radiogroup" aria-label="Account type">
        {['SAVINGS', 'CURRENT'].map((t) => <label key={t}><input type="radio" name="atype" checked={type === t} onChange={() => setType(t)} />{titleCase(t)}</label>)}
      </div>
    </div><div className="panel__body">
      {error ? <Alert tone="bad">{error}</Alert> : !data ? <Skeleton h={90} />
        : data.content.length === 0 ? <Empty title="No accounts">No {type.toLowerCase()} accounts have been opened yet.</Empty>
        : <div className="table-wrap"><table className="dtable">
          <thead><tr><th>Holder</th><th>Account number</th><th>Branch</th><th>Status</th></tr></thead>
          <tbody>{data.content.map((a) => (
            <tr key={a.accountNumber}>
              <td style={{ fontWeight: 600 }}>{a.accountHolderName}<div className="muted" style={{ fontWeight: 400, fontSize: 13 }}>KYC {a.kycStatus?.toLowerCase()}</div></td>
              <td className="acct">{groupAccount(a.accountNumber)}</td>
              <td>{a.branchName}<div className="muted acct" style={{ fontSize: 13 }}>{a.ifscCode}</div></td>
              <td><Stamp status={a.accountStatus} /></td>
            </tr>))}</tbody>
        </table></div>}
      <Pager page={page} totalPages={data?.totalPages} onChange={setPage} />
    </div></div>
  );
}

function Branches() {
  const { isAdmin } = useAuth();
  const toast = useToast();
  const [list, setList] = useState(null);
  const [f, setF] = useState({ branchName: '', branchAddress: '', city: '', state: '', pincode: '' });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const load = () => accountApi.getBranches(0, 50).then((p) => setList(p.content)).catch((e) => setError(e.message));
  useEffect(() => { load(); }, []);
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    const short = Object.entries(f).find(([k, v]) => k !== 'pincode' && v.trim().length < 10);
    if (short) { setError('Branch name, address, city and state each need at least 10 characters.'); return; }
    if (!/^\d{6}$/.test(f.pincode)) { setError('Pincode has 6 digits.'); return; }
    setBusy(true); setError('');
    try { await accountApi.createBranch(f); toast('Branch created.'); setF({ branchName: '', branchAddress: '', city: '', state: '', pincode: '' }); await load(); }
    catch (err) { setError(err.message); } finally { setBusy(false); }
  };

  return (
    <div className="grid-2">
      <div className="panel"><div className="panel__head"><h2>Branches</h2></div><div className="panel__body">
        {!list ? <Skeleton h={80} /> : (
          <div className="table-wrap"><table className="dtable">
            <thead><tr><th>Branch</th><th>IFSC</th><th>City</th></tr></thead>
            <tbody>{list.map((b) => <tr key={b.ifscCode}><td style={{ fontWeight: 600 }}>{b.branchName}<div className="muted" style={{ fontWeight: 400, fontSize: 13 }}>{b.addressLine}</div></td><td className="acct">{b.ifscCode}</td><td>{b.city}</td></tr>)}</tbody>
          </table></div>
        )}
      </div></div>
      {isAdmin ? (
        <form className="panel" onSubmit={submit} noValidate><div className="panel__head"><h2>Add a branch</h2></div><div className="panel__body form">
          {error && <Alert tone="bad">{error}</Alert>}
          <TextField label="Branch name" value={f.branchName} onChange={set('branchName')} />
          <TextField label="Address" value={f.branchAddress} onChange={set('branchAddress')} />
          <TextField label="City" value={f.city} onChange={set('city')} />
          <TextField label="State" value={f.state} onChange={set('state')} />
          <TextField label="Pincode" inputMode="numeric" maxLength={6} className="acct" value={f.pincode} onChange={(e) => setF({ ...f, pincode: e.target.value.replace(/\D/g, '') })} />
          <div><Button type="submit" busy={busy}>Create branch</Button></div>
        </div></form>
      ) : <Alert tone="info">Only admins can add branches.</Alert>}
    </div>
  );
}
