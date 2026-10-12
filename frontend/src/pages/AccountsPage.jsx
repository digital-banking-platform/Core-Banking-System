import { useBank } from '../context/BankContext';
import { Empty, PageHead } from '../components/ui';
import { PassbookCover } from '../components/PassbookCover';
import { Link } from '../router';

export function AccountsPage() {
  const { accounts, active, profile, setPrimary } = useBank();

  return (
    <>
      <PageHead title="Accounts" sub="Choose which account to feature on your overview.">
        <Link to="/accounts/create" className="btn">Create account</Link>
      </PageHead>
      {accounts.length === 0 ? (
        <div className="panel"><Empty title="No linked accounts">Your linked accounts will appear here.</Empty></div>
      ) : (
        <div className="accounts-grid">
          {accounts.map((account) => (
            <PassbookCover
              key={account.accountNumber}
              account={account}
              kycStatus={profile?.kycStatus}
              isPrimary={account.accountNumber === active?.accountNumber}
              onSetPrimary={setPrimary}
            />
          ))}
        </div>
      )}
    </>
  );
}
