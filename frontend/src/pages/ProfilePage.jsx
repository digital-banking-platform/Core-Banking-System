import { useState } from 'react';
import { useBank } from '../context/BankContext';
import { useToast } from '../context/ToastContext';
import * as accountApi from '../api/accountApi';
import { Alert, Button, PageHead, Stamp, TextField } from '../components/ui';
import { shortDate, groupAccount, titleCase } from '../utils/format';

const KYC_COPY = {
  VERIFIED: 'A bank officer has verified your details.',
  PENDING: 'A bank officer is reviewing your details. This usually takes a working day.',
  REJECTED: 'Your details were rejected. Contact your branch to resubmit.',
};

export function ProfilePage() {
  const { profile, accounts, reload } = useBank();
  const toast = useToast();
  const [editing, setEditing] = useState(null); // 'name' | 'address' | null

  return (
    <>
      <PageHead title="Profile" sub="Your KYC details and the accounts linked to them." />
      <div className="grid-2">
        <div className="stack">
          <section className="panel">
            <div className="panel__head"><h2>Personal details</h2>{editing !== 'name' && <button className="link-btn" onClick={() => setEditing('name')}>Edit name</button>}</div>
            <div className="panel__body">
              {editing === 'name' ? <NameForm initial={profile.accountHolderName} onClose={() => setEditing(null)} onSaved={async () => { toast('Name updated.'); setEditing(null); await reload(); }} />
                : <dl className="kv">
                  <dt>Full name</dt><dd>{profile.accountHolderName}</dd>
                  <dt>Date of birth</dt><dd>{shortDate(profile.dateOfBirth)}</dd>
                  <dt>Mobile</dt><dd className="acct">{profile.phoneNumber}</dd>
                </dl>}
            </div>
          </section>

          <section className="panel">
            <div className="panel__head"><h2>Address</h2>{editing !== 'address' && <button className="link-btn" onClick={() => setEditing('address')}>Edit address</button>}</div>
            <div className="panel__body">
              {editing === 'address' ? <AddressForm profile={profile} onClose={() => setEditing(null)} onSaved={async () => { toast('Address updated.'); setEditing(null); await reload(); }} />
                : <dl className="kv">
                  <dt>Street</dt><dd>{profile.address}</dd>
                  <dt>City</dt><dd>{profile.city}</dd>
                  <dt>State</dt><dd>{profile.state}</dd>
                  <dt>Pincode</dt><dd className="acct">{profile.pincode}</dd>
                </dl>}
            </div>
          </section>
        </div>

        <div className="stack">
          <section className="panel">
            <div className="panel__head"><h2>KYC status</h2><Stamp status={profile.kycStatus} /></div>
            <div className="panel__body"><p className="muted">{KYC_COPY[profile.kycStatus]}</p></div>
          </section>
          <section className="panel">
            <div className="panel__head"><h2>Linked accounts</h2></div>
            <div className="panel__body" style={{ display: 'grid', gap: 14 }}>
              {accounts.map((a) => (
                <div key={a.accountNumber}>
                  <div style={{ fontWeight: 650 }}>{titleCase(a.type)}</div>
                  <div className="acct">{groupAccount(a.accountNumber)}</div>
                  <div className="muted" style={{ fontSize: 14 }}>{a.branch} · <span className="acct">{a.ifsc}</span></div>
                </div>
              ))}
            </div>
          </section>
        </div>
      </div>
    </>
  );
}

function NameForm({ initial, onClose, onSaved }) {
  const [name, setName] = useState(initial);
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const submit = async (e) => {
    e.preventDefault();
    if (name.trim().length < 3) { setError('Use at least 3 characters.'); return; }
    setBusy(true); setError('');
    try { await accountApi.updateName(name.trim()); await onSaved(); } catch (err) { setError(err.message); } finally { setBusy(false); }
  };
  return (
    <form className="form" onSubmit={submit} noValidate>
      <TextField label="Full name" value={name} onChange={(e) => setName(e.target.value)} error={error} autoFocus />
      <div className="row"><Button type="submit" busy={busy}>Save name</Button><Button type="button" variant="ghost" onClick={onClose}>Cancel</Button></div>
    </form>
  );
}

function AddressForm({ profile, onClose, onSaved }) {
  const [f, setF] = useState({ address: profile.address, city: profile.city, state: profile.state, pincode: profile.pincode });
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const submit = async (e) => {
    e.preventDefault();
    if (f.address.trim().length < 10) { setError('Address needs at least 10 characters.'); return; }
    if (!/^\d{6}$/.test(f.pincode)) { setError('Pincode has 6 digits.'); return; }
    setBusy(true); setError('');
    try { await accountApi.updateAddress({ ...f, address: f.address.trim() }); await onSaved(); } catch (err) { setError(err.message); } finally { setBusy(false); }
  };
  return (
    <form className="form" onSubmit={submit} noValidate>
      {error && <Alert tone="bad">{error}</Alert>}
      <TextField label="Street address" value={f.address} onChange={set('address')} />
      <div className="form__row">
        <TextField label="City" value={f.city} onChange={set('city')} />
        <TextField label="State" value={f.state} onChange={set('state')} />
      </div>
      <TextField label="Pincode" inputMode="numeric" maxLength={6} className="acct" value={f.pincode} onChange={(e) => setF({ ...f, pincode: e.target.value.replace(/\D/g, '') })} style={{ maxWidth: 200 }} />
      <div className="row"><Button type="submit" busy={busy}>Save address</Button><Button type="button" variant="ghost" onClick={onClose}>Cancel</Button></div>
    </form>
  );
}
