import { useEffect, useState } from 'react';
import { useBank } from '../context/BankContext';
import { transferMoney } from '../api/transactionApi';
import { verifyReceiver } from '../api/accountApi';
import { Alert, Button, Field, PageHead, SelectField, Stamp, TextField } from '../components/ui';
import { Icon } from '../components/Icon';
import { Link } from '../router';
import { groupAccount, money, newIdempotencyKey, shortDate, shortTime, titleCase, maskAccount } from '../utils/format';

export function TransferPage() {
  const { accounts, active } = useBank();
  const [from, setFrom] = useState(active?.accountNumber || accounts[0]?.accountNumber);
  const [to, setTo] = useState('');
  const [amount, setAmount] = useState('');
  const [note, setNote] = useState('');
  const [pin, setPin] = useState('');
  const [errors, setErrors] = useState({});
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState(null);
  // One key per transfer attempt. Retrying after a timeout re-sends the same key, so it can't pay twice.
  const [key, setKey] = useState(newIdempotencyKey);
  const [verify, setVerify] = useState({ state: 'idle' });

  useEffect(() => {
    if (!/^\d{12}$/.test(to)) { setVerify({ state: 'idle' }); return; }
    let live = true;
    setVerify({ state: 'checking' });
    verifyReceiver(to).then((r) => live && setVerify({ state: r.status })).catch((e) => live && setVerify({ state: 'ERROR', message: e.message }));
    return () => { live = false; };
  }, [to]);

  const validate = () => {
    const e = {};
    if (!/^\d{12}$/.test(to)) e.to = 'Account number has 12 digits.';
    else if (to === from) e.to = 'Choose a different account to send to.';
    else if (verify.state === 'ERROR') e.to = verify.message;
    else if (verify.state === 'SUSPENDED' || verify.state === 'CLOSED') e.to = 'This account cannot receive money right now.';
    if (!(Number(amount) >= 1)) e.amount = 'Enter at least ₹1.';
    if (!/^\d{6}$/.test(pin)) e.pin = 'Enter your 6-digit PIN.';
    return e;
  };

  const submit = async (ev) => {
    ev.preventDefault();
    const e = validate(); setErrors(e);
    if (Object.keys(e).length) return;
    setBusy(true); setError('');
    try {
      const res = await transferMoney({ senderAccountNumber: from, receiverAccountNumber: to, amount: Number(amount), transactionPin: pin, description: note.trim() || undefined, idempotencyKey: key });
      setResult(res);
    } catch (err) {
      setError(err.status ? err.message : 'We could not reach the bank. Nothing was sent twice: tap Send again to retry safely.');
    } finally { setBusy(false); }
  };

  const reset = () => { setResult(null); setTo(''); setAmount(''); setNote(''); setPin(''); setErrors({}); setError(''); setKey(newIdempotencyKey()); };

  if (result) return <Receipt result={result} onAgain={reset} />;

  return (
    <>
      <PageHead title="Send money" sub="Transfer from your account to any Siddu Bank account. You need the receiver's 12-digit account number." />
      <div className="panel transfer"><div className="panel__body">
        <form className="form" onSubmit={submit} noValidate>
          {error && <Alert tone="bad">{error}</Alert>}
          <SelectField label="From" value={from} onChange={(e) => setFrom(e.target.value)}>
            {accounts.map((a) => <option key={a.accountNumber} value={a.accountNumber}>{titleCase(a.type)} {maskAccount(a.accountNumber)}</option>)}
          </SelectField>
          <div className="field">
            <TextField label="To account number" inputMode="numeric" value={to} className="acct" placeholder="000000000000"
              onChange={(e) => setTo(e.target.value.replace(/\D/g, '').slice(0, 12))} error={errors.to} />
            <div className="verify" aria-live="polite">
              {verify.state === 'checking' && <span className="muted">Checking account…</span>}
              {verify.state === 'ACTIVE' && <span style={{ color: 'var(--credit)', display: 'inline-flex', gap: 6, alignItems: 'center' }}><Icon name="check" size={16} /> Account found and active</span>}
              {verify.state === 'SUSPENDED' && <span style={{ color: 'var(--debit)' }}>This account is suspended</span>}
              {verify.state === 'ERROR' && !errors.to && <span style={{ color: 'var(--debit)' }}>{verify.message}</span>}
            </div>
          </div>
          <Field label="Amount" error={errors.amount}>
            <div className="adorn">
              <span className="adorn__pre">₹</span>
              <input className="input input--amount" inputMode="decimal" value={amount} placeholder="0.00" aria-label="Amount in rupees" aria-invalid={!!errors.amount}
                onChange={(e) => setAmount(e.target.value.replace(/[^\d.]/g, '').replace(/(\..*)\./g, '$1'))} />
            </div>
          </Field>
          <TextField label="Note (optional)" value={note} maxLength={255} onChange={(e) => setNote(e.target.value)} hint="Shown on both passbooks." />
          <TextField label="PIN" type="password" inputMode="numeric" autoComplete="off" className="input--pin" placeholder="••••••" value={pin}
            onChange={(e) => setPin(e.target.value.replace(/\D/g, '').slice(0, 6))} error={errors.pin} style={{ maxWidth: 220 }} />
          <div className="row">
            <Button type="submit" busy={busy}>{busy ? 'Sending…' : amount ? `Send ${money(amount)}` : 'Send money'}</Button>
            <span className="muted" style={{ fontSize: 13.5 }}>Retrying reuses this transfer's reference, so you can't be charged twice.</span>
          </div>
        </form>
      </div></div>
    </>
  );
}

function Receipt({ result: r, onAgain }) {
  const ok = r.status === 'SUCCESS';
  const wait = r.status === 'PENDING';
  return (
    <>
      <PageHead title={ok ? 'Money sent' : wait ? 'Transfer pending' : 'Transfer failed'} />
      <div className="panel transfer"><div className="panel__body receipt">
        <div className="receipt__head">
          <div className="receipt__amt">{money(r.amount)}</div>
          <Stamp big status={r.status} label={ok ? 'Paid' : wait ? 'Pending' : 'Failed'} />
        </div>
        {wait && <Alert tone="wait">The bank hasn't confirmed this transfer yet. Don't send it again. Check your passbook in a few minutes, the status will update there.</Alert>}
        {r.status === 'FAILED' && <Alert tone="bad">{r.failureReason || 'The transfer did not go through. No money left your account.'}</Alert>}
        <dl>
          <dt>From</dt><dd>{r.debitedFrom.accountHolderName}<br /><span className="acct">{groupAccount(r.debitedFrom.accountNumber)}</span></dd>
          <dt>To</dt><dd>{r.creditedTo.accountHolderName}<br /><span className="acct">{groupAccount(r.creditedTo.accountNumber)}</span></dd>
          <dt>Reference</dt><dd className="acct">{r.transactionId}</dd>
          <dt>Time</dt><dd>{shortDate(r.completedAt)}, {shortTime(r.completedAt)}</dd>
        </dl>
        <div className="row">
          <Link to="/passbook" className="btn">View passbook</Link>
          <button className="btn btn--ghost" onClick={onAgain}>{ok ? 'Send another' : 'Start a new transfer'}</button>
        </div>
      </div></div>
    </>
  );
}
