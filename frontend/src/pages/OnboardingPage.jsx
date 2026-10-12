import { useEffect, useState } from 'react';
import { Alert, Button, Field, PageHead, SelectField, TextField } from '../components/ui';
import { useBank } from '../context/BankContext';
import { useToast } from '../context/ToastContext';
import * as accountApi from '../api/accountApi';
import { navigate } from '../router';

const blankProfile = { accountHolderName: '', dateOfBirth: '', gender: 'FEMALE', aadhaarNumber: '', phoneNumber: '', addressLine: '', city: '', state: '', pincode: '' };

function validate(f) {
  const e = {};
  if (f.accountHolderName.trim().length < 3 || f.accountHolderName.trim().length > 50) e.accountHolderName = 'Use 3–50 characters.';
  if (!f.dateOfBirth) e.dateOfBirth = 'Enter your date of birth.';
  if (!/^\d{12}$/.test(f.aadhaarNumber)) e.aadhaarNumber = 'Aadhaar number has 12 digits.';
  if (!/^\d{10}$/.test(f.phoneNumber)) e.phoneNumber = 'Phone number has 10 digits.';
  if (f.addressLine.trim().length < 10 || f.addressLine.trim().length > 100) e.addressLine = 'Use 10–100 characters.';
  if (f.city.trim().length < 2) e.city = 'Enter your city.';
  if (f.state.trim().length < 2) e.state = 'Enter your state.';
  if (!/^\d{6}$/.test(f.pincode)) e.pincode = 'Pincode has 6 digits.';
  return e;
}

function Steps({ step }) {
  const state = (n) => (n < step ? 'done' : n === step ? 'current' : 'todo');
  return (
    <div className="steps" aria-label="Setup progress">
      <div className="step" data-state={state(1)}><span className="step__n">{step > 1 ? '✓' : 1}</span> Your details</div>
      <span className="step__bar" />
      <div className="step" data-state={state(2)}><span className="step__n">2</span> Open an account</div>
    </div>
  );
}

export function OnboardingPage() {
  const { profile, accounts, reload } = useBank();
  const toast = useToast();
  const step = profile ? 2 : 1;

  useEffect(() => { if (profile && accounts.length) navigate('/'); }, [profile, accounts]);

  return (
    <>
      <PageHead title={step === 1 ? 'Tell us about yourself' : 'Open your first account'}
        sub={step === 1 ? 'We need these details for KYC before you can transact. A bank officer reviews them after you submit.' : 'Pick an account type and a branch, then set the 6-digit PIN you will use for balance checks and transfers.'} />
      <div className="onboarding-flow">
        <Steps step={step} />
        <div className="panel onboarding-flow__panel"><div className="panel__body">
        {step === 1 ? <ProfileStep onDone={async () => { toast('Details saved. KYC is now pending review.'); await reload(); }} />
          : <AccountCreationForm onDone={async () => { toast('Account opened.'); await reload(); navigate('/'); }} />}
        </div></div>
      </div>
    </>
  );
}

function ProfileStep({ onDone }) {
  const [f, setF] = useState(blankProfile);
  const [errors, setErrors] = useState({});
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value });
  const digits = (k, max) => (e) => setF({ ...f, [k]: e.target.value.replace(/\D/g, '').slice(0, max) });

  const submit = async (e) => {
    e.preventDefault();
    const v = validate(f); setErrors(v);
    if (Object.keys(v).length) return;
    setBusy(true); setError('');
    try { await accountApi.createProfile({ ...f, accountHolderName: f.accountHolderName.trim(), addressLine: f.addressLine.trim() }); await onDone(); }
    catch (err) { setError(err.message); } finally { setBusy(false); }
  };

  return (
    <form className="form" onSubmit={submit} noValidate>
      {error && <Alert tone="bad">{error}</Alert>}
      <TextField label="Full name" hint="As it appears on your Aadhaar card." value={f.accountHolderName} onChange={set('accountHolderName')} error={errors.accountHolderName} autoComplete="name" />
      <div className="form__row">
        <TextField label="Date of birth" type="date" value={f.dateOfBirth} onChange={set('dateOfBirth')} error={errors.dateOfBirth} />
        <Field label="Gender">
          <div className="seg" role="radiogroup" aria-label="Gender">
            {[['FEMALE', 'Female'], ['MALE', 'Male'], ['OTHER', 'Other']].map(([v, l]) => (
              <label key={v}><input type="radio" name="gender" value={v} checked={f.gender === v} onChange={set('gender')} />{l}</label>
            ))}
          </div>
        </Field>
      </div>
      <div className="form__row">
        <TextField label="Aadhaar number" inputMode="numeric" value={f.aadhaarNumber} onChange={digits('aadhaarNumber', 12)} error={errors.aadhaarNumber} className="acct" />
        <TextField label="Mobile number" inputMode="numeric" value={f.phoneNumber} onChange={digits('phoneNumber', 10)} error={errors.phoneNumber} autoComplete="tel-national" className="acct" />
      </div>
      <TextField label="Address" value={f.addressLine} onChange={set('addressLine')} error={errors.addressLine} autoComplete="street-address" />
      <div className="form__row">
        <TextField label="City" value={f.city} onChange={set('city')} error={errors.city} autoComplete="address-level2" />
        <TextField label="State" value={f.state} onChange={set('state')} error={errors.state} autoComplete="address-level1" />
      </div>
      <TextField label="Pincode" inputMode="numeric" value={f.pincode} onChange={digits('pincode', 6)} error={errors.pincode} className="acct" style={{ maxWidth: 200 }} />
      <div><Button type="submit" busy={busy}>{busy ? 'Saving…' : 'Save and continue'}</Button></div>
    </form>
  );
}

export function AccountCreationForm({ onDone }) {
  const [branches, setBranches] = useState(null);
  const [type, setType] = useState('SAVINGS');
  const [ifsc, setIfsc] = useState('');
  const [pin, setPin] = useState('');
  const [pinConfirm, setPinConfirm] = useState('');
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    accountApi.getBranches(0, 50).then((p) => { setBranches(p.content); setIfsc(p.content[0]?.ifscCode || ''); }).catch((e) => setError(e.message));
  }, []);

  const submit = async (e) => {
    e.preventDefault();
    if (!ifsc) { setError('Choose a branch.'); return; }
    if (!/^\d{6}$/.test(pin)) { setError('PIN must be exactly 6 digits.'); return; }
    if (pin !== pinConfirm) { setError('The two PINs do not match.'); return; }
    setBusy(true); setError('');
    try { await accountApi.createAccount({ accountType: type, ifsc, pin, pinConfirm }); await onDone(); }
    catch (err) { setError(err.message); } finally { setBusy(false); }
  };

  const digits = (setter) => (e) => setter(e.target.value.replace(/\D/g, '').slice(0, 6));

  return (
    <form className="form account-setup" onSubmit={submit} noValidate>
      {error && <Alert tone="bad">{error}</Alert>}
      <Field label="Account type">
        <div className="choice-grid account-setup__types" role="radiogroup" aria-label="Account type">
          <label className="choice"><input type="radio" name="type" checked={type === 'SAVINGS'} onChange={() => setType('SAVINGS')} /><b>Savings</b><span>For everyday money and personal use.</span></label>
          <label className="choice"><input type="radio" name="type" checked={type === 'CURRENT'} onChange={() => setType('CURRENT')} /><b>Current</b><span>For business payments and frequent transfers.</span></label>
        </div>
      </Field>
      <SelectField label="Branch" value={ifsc} onChange={(e) => setIfsc(e.target.value)} disabled={!branches}>
        {!branches && <option>Loading branches…</option>}
        {branches?.map((b) => <option key={b.ifscCode} value={b.ifscCode}>{b.branchName}, {b.city} ({b.ifscCode})</option>)}
      </SelectField>
      <div className="form__row">
        <TextField label="Set a PIN" type="password" inputMode="numeric" autoComplete="new-password" value={pin} onChange={digits(setPin)} className="input--pin" placeholder="••••••" hint="6 digits. You'll enter it to see your balance and send money." />
        <TextField label="Confirm PIN" type="password" inputMode="numeric" autoComplete="new-password" value={pinConfirm} onChange={digits(setPinConfirm)} className="input--pin" placeholder="••••••" />
      </div>
      <div className="account-setup__submit"><Button type="submit" busy={busy}>{busy ? 'Opening…' : 'Open account'}</Button></div>
    </form>
  );
}
