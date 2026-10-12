import { useEffect, useState } from 'react';
import { useBank } from '../context/BankContext';
import { getHistory } from '../api/transactionApi';
import { PassbookCover } from '../components/PassbookCover';
import { Ledger } from '../components/Ledger';
import { Alert, Empty, PageHead, Skeleton, Stamp } from '../components/ui';
import { Link } from '../router';
import { firstName, greeting } from '../utils/format';

export function DashboardPage() {
  const { profile, active } = useBank();
  const [entries, setEntries] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!active) return;
    let cancelled = false;
    setEntries(null); setError('');
    getHistory(active.accountNumber, 0, 5)
      .then((p) => {
        if (!cancelled) setEntries(p.content);
      })
      .catch((e) => {
        if (!cancelled) setError(e.message);
      });
    return () => { cancelled = true; };
  }, [active?.accountNumber]);

  return (
    <>
      <PageHead title={`${greeting()}, ${firstName(profile.accountHolderName)}.`} sub="Your accounts and latest passbook entries.">
        <Link to="/transfer" className="btn">Send money</Link>
      </PageHead>

      {profile.kycStatus === 'PENDING' && <div style={{ marginBottom: 20 }}><Alert tone="wait">Your KYC is waiting for a bank officer to review it. You can look around now; transfers may be limited until it is verified.</Alert></div>}
      {profile.kycStatus === 'REJECTED' && <div style={{ marginBottom: 20 }}><Alert tone="bad">Your KYC was rejected. Check your details on the <Link to="/profile" style={{ fontWeight: 650 }}>profile page</Link> and contact your branch.</Alert></div>}

      <div className="stack">
        {active && <PassbookCover account={active} kycStatus={profile.kycStatus} isPrimary />}

        <section>
          <div className="row row--between" style={{ marginBottom: 12 }}>
            <h2>Latest entries</h2>
            <Link to="/passbook" style={{ fontWeight: 600 }}>Open full passbook</Link>
          </div>
          {error ? <Alert tone="bad">{error}</Alert>
            : entries === null ? <div className="panel panel__body" style={{ display: 'grid', gap: 14 }}><Skeleton h={22} /><Skeleton h={22} /><Skeleton h={22} /></div>
            : entries.length === 0 ? <div className="panel"><Empty title="No entries yet" action={<Link to="/transfer" className="btn btn--sm">Send your first transfer</Link>}>Transfers in and out of this account will be written here.</Empty></div>
            : <Ledger entries={entries} accountNumber={active.accountNumber} />}
        </section>
      </div>
    </>
  );
}
