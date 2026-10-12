import { useBank } from '../context/BankContext';
import { useToast } from '../context/ToastContext';
import { PageHead } from '../components/ui';
import { Link, navigate } from '../router';
import { AccountCreationForm } from './OnboardingPage';

export function CreateAccountPage() {
  const { reload } = useBank();
  const toast = useToast();

  const onDone = async () => {
    toast('Account opened.');
    await reload();
    navigate('/accounts');
  };

  return (
    <>
      <PageHead title="Create account" sub="Choose an account type and branch, then set the 6-digit PIN for your new account.">
        <Link to="/accounts" className="btn btn--ghost">Back to accounts</Link>
      </PageHead>
      <div className="onboarding-flow">
        <div className="panel onboarding-flow__panel">
          <div className="panel__body">
            <AccountCreationForm onDone={onDone} />
          </div>
        </div>
      </div>
    </>
  );
}
