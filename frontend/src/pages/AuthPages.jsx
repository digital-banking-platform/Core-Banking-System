import { useState } from 'react';
import { AuthLayout } from '../components/Layout';
import { Alert, Button, TextField } from '../components/ui';
import { Icon } from '../components/Icon';
import { Link, navigate } from '../router';
import { useAuth } from '../context/AuthContext';

const EMAIL = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const RULES = [
  ['6–20 characters', (p) => p.length >= 6 && p.length <= 20],
  ['A lowercase letter', (p) => /[a-z]/.test(p)],
  ['An uppercase letter', (p) => /[A-Z]/.test(p)],
  ['A number', (p) => /\d/.test(p)],
  ['A special character (@ $ ! % * ? &)', (p) => /[@$!%*?&]/.test(p)],
];

function PasswordField({ label = 'Password', value, onChange, error, autoComplete }) {
  const [shown, setShown] = useState(false);
  return (
    <div className="field">
      <label htmlFor="pw">{label}</label>
      <div className="adorn">
        <input id="pw" className="input" type={shown ? 'text' : 'password'} value={value} onChange={onChange}
          autoComplete={autoComplete} aria-invalid={!!error} style={{ paddingRight: 48 }} />
        <button type="button" className="adorn__post btn btn--ghost btn--sm" style={{ border: 0, minHeight: 32 }}
          onClick={() => setShown(!shown)} aria-label={shown ? 'Hide password' : 'Show password'}>
          <Icon name={shown ? 'eyeoff' : 'eye'} size={16} />
        </button>
      </div>
      {error && <span className="field__error" role="alert">{error}</span>}
    </div>
  );
}

export function LoginPage() {
  const { signIn } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const [errors, setErrors] = useState({});

  const submit = async (e) => {
    e.preventDefault();
    const next = {};
    if (!EMAIL.test(email)) next.email = 'Enter a valid email address.';
    if (!password) next.password = 'Enter your password.';
    setErrors(next);
    if (Object.keys(next).length) return;
    setBusy(true); setError('');
    try { await signIn(email.trim(), password); navigate('/'); }
    catch (err) { setError(err.message); } finally { setBusy(false); }
  };

  return (
    <AuthLayout title="Sign in" sub="Welcome back. Use the email you registered with.">
      <form className="form" onSubmit={submit} noValidate>
        {error && <Alert tone="bad">{error}</Alert>}
        <TextField label="Email" type="email" autoComplete="username" value={email} onChange={(e) => setEmail(e.target.value)} error={errors.email} />
        <PasswordField value={password} onChange={(e) => setPassword(e.target.value)} error={errors.password} autoComplete="current-password" />
        <Button type="submit" busy={busy} block>{busy ? 'Signing in…' : 'Sign in'}</Button>
      </form>
      <p className="auth__alt">New to Siddu Bank? <Link to="/register">Create an account</Link></p>
    </AuthLayout>
  );
}

export function RegisterPage() {
  const { signUp } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const allOk = RULES.every(([, test]) => test(password));

  const submit = async (e) => {
    e.preventDefault();
    if (!EMAIL.test(email)) { setError('Enter a valid email address.'); return; }
    if (!allOk) { setError('Your password does not meet every rule below yet.'); return; }
    setBusy(true); setError('');
    try { await signUp(email.trim(), password); navigate('/'); }
    catch (err) { setError(err.message); } finally { setBusy(false); }
  };

  return (
    <AuthLayout title="Create your login" sub="Next you'll add your KYC details and open an account.">
      <form className="form" onSubmit={submit} noValidate>
        {error && <Alert tone="bad">{error}</Alert>}
        <TextField label="Email" type="email" autoComplete="username" value={email} onChange={(e) => setEmail(e.target.value)} />
        <PasswordField value={password} onChange={(e) => setPassword(e.target.value)} autoComplete="new-password" />
        <ul className="checklist" aria-label="Password rules">
          {RULES.map(([label, test]) => (
            <li key={label} data-ok={test(password)}><Icon name={test(password) ? 'check' : 'x'} size={14} /> {label}</li>
          ))}
        </ul>
        <Button type="submit" busy={busy} block>{busy ? 'Creating…' : 'Create login'}</Button>
      </form>
      <p className="auth__alt">Already registered? <Link to="/login">Sign in</Link></p>
    </AuthLayout>
  );
}
