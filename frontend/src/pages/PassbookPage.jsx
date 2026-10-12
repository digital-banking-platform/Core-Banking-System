import { useEffect, useState } from 'react';
import { useBank } from '../context/BankContext';
import { getHistory } from '../api/transactionApi';
import { Ledger } from '../components/Ledger';
import { Alert, Empty, PageHead, Pager, Skeleton } from '../components/ui';
import { Link } from '../router';
import { titleCase, maskAccount } from '../utils/format';

export function PassbookPage() {
  const { active } = useBank();
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [error, setError] = useState('');

  useEffect(() => { setPage(0); }, [active?.accountNumber]);
  useEffect(() => {
    if (!active) return;
    let cancelled = false;
    setData(null); setError('');
    getHistory(active.accountNumber, page, 10)
      .then((result) => {
        if (!cancelled) setData(result);
      })
      .catch((e) => {
        if (!cancelled) setError(e.message);
      });
    return () => { cancelled = true; };
  }, [active?.accountNumber, page]);

  return (
    <>
      <PageHead
        title="Passbook"
        sub={active ? `History for your primary ${titleCase(active.type).toLowerCase()} account ${maskAccount(active.accountNumber)}, newest first.` : 'Every transfer in and out of your primary account, newest first.'}
      />
      {error ? <Alert tone="bad">{error}</Alert>
        : !data ? <div className="panel panel__body" style={{ display: 'grid', gap: 16 }}>{[0, 1, 2, 3, 4, 5].map((i) => <Skeleton key={i} h={26} />)}</div>
        : data.content.length === 0 ? <div className="panel"><Empty title="Nothing written yet" action={<Link to="/transfer" className="btn btn--sm">Send money</Link>}>Entries appear here as soon as money moves.</Empty></div>
        : <><Ledger entries={data.content} accountNumber={active.accountNumber} /><Pager page={data.number} totalPages={data.totalPages} onChange={setPage} /></>}
    </>
  );
}
