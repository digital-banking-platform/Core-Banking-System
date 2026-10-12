import { useEffect, useState } from 'react';
import { checkBalance } from '../api/accountApi';
import { useBank } from '../context/BankContext';
import { Alert, Button, PageHead, TextField } from '../components/ui';
import { Link, navigate } from '../router';
import { groupAccount, money, titleCase, maskAccount } from '../utils/format';

const balanceKey = (accountNumber) => `banking-ui-balance:${accountNumber}`;

function getAccount(accounts, accountNumber) {
  return accounts.find((account) => String(account.accountNumber) === String(accountNumber));
}

export function BalancePinPage({ accountNumber }) {
  const { accounts } = useBank();
  const account = getAccount(accounts, accountNumber);
  const [pin, setPin] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  const submit = async (event) => {
    event.preventDefault();
    if (!/^\d{6}$/.test(pin)) {
      setError('Enter your 6-digit PIN.');
      return;
    }
    setBusy(true);
    setError('');
    try {
      const result = await checkBalance(account.accountNumber, pin);
      const balance = Number(result?.balance);
      if (!Number.isFinite(balance)) throw new Error('The bank did not return a valid balance.');
      sessionStorage.setItem(balanceKey(account.accountNumber), JSON.stringify({
        balance,
        expiresAt: Date.now() + 20000,
      }));
      navigate(`/balance-result/${encodeURIComponent(account.accountNumber)}`);
    } catch (err) {
      setError(err.message || 'We could not verify your PIN. Please try again.');
    } finally {
      setBusy(false);
    }
  };

  if (!account) {
    return <Alert tone="bad">That account is not available in your profile.</Alert>;
  }

  return (
    <>
      <PageHead title="Check balance" sub={`${titleCase(account.type)} account · ${maskAccount(account.accountNumber)}`} />
      <div className="panel balance-card">
        <div className="panel__body">
          <form className="form" onSubmit={submit} noValidate>
            <p className="muted">Enter your 6-digit PIN to securely view this account's available balance.</p>
            {error && <Alert tone="bad">{error}</Alert>}
            <TextField
              label="Account PIN"
              type="password"
              inputMode="numeric"
              autoComplete="off"
              autoFocus
              className="input--pin"
              placeholder="••••••"
              value={pin}
              maxLength={6}
              onChange={(event) => setPin(event.target.value.replace(/\D/g, '').slice(0, 6))}
              hint={`Account ending in ${String(account.accountNumber).slice(-4)}`}
            />
            <div className="row">
              <Button type="submit" busy={busy}>{busy ? 'Verifying PIN…' : 'View balance'}</Button>
              <Link to="/" className="btn btn--ghost">Cancel</Link>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}

function readBalance(accountNumber) {
  try {
    const saved = sessionStorage.getItem(balanceKey(accountNumber));
    if (!saved) return null;
    const result = JSON.parse(saved);
    if (!Number.isFinite(Number(result?.expiresAt)) || Date.now() >= Number(result.expiresAt)) return null;
    if (!Number.isFinite(Number(result?.balance))) return null;
    return result;
  } catch { return null; }
}

export function BalanceResultPage({ accountNumber }) {
  const { accounts } = useBank();
  const account = getAccount(accounts, accountNumber);
  const [result, setResult] = useState(() => account ? readBalance(account.accountNumber) : null);
  const [expired, setExpired] = useState(false);

  useEffect(() => {
    if (!result || !account) return undefined;
    const remaining = Math.max(0, Number(result.expiresAt) - Date.now());
    const timer = setTimeout(() => {
      sessionStorage.removeItem(balanceKey(account.accountNumber));
      setResult(null);
      setExpired(true);
    }, remaining);
    return () => clearTimeout(timer);
  }, [result, account]);

  if (!account) return <Alert tone="bad">That account is not available in your profile.</Alert>;

  return (
    <>
      <PageHead title="Balance status" sub={`${titleCase(account.type)} account · ${maskAccount(account.accountNumber)}`} />
      <section className="panel balance-result">
        {result ? (
          <div className="balance-result__body">
            <span className="balance-result__icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none"><path d="m5 12 4 4L19 6" /></svg>
            </span>
            <h2>Balance verified</h2>
            <span className="balance-result__label">Available balance</span>
            <strong className="balance-result__amount">{money(result.balance)}</strong>
            <span className="balance-result__account">{groupAccount(account.accountNumber)}</span>
            <p className="muted">For your security, this balance will be hidden after 20 seconds.</p>
            <Link to="/" className="btn">Done</Link>
          </div>
        ) : (
          <div className="panel__body">
            <Alert tone={expired ? 'info' : 'wait'}>
              {expired ? 'Your balance was hidden for your security.' : 'No verified balance is available in this session. Enter your PIN to check again.'}
            </Alert>
            <div className="row" style={{ marginTop: 18 }}>
              <Link to={`/balance/${encodeURIComponent(account.accountNumber)}`} className="btn">Check again</Link>
              <Link to="/" className="btn btn--ghost">Back to accounts</Link>
            </div>
          </div>
        )}
      </section>
    </>
  );
}
