import { useState } from 'react';
import { Link } from '../router';
import { Alert, PageHead } from '../components/ui';
import { TransactionReceipt } from '../components/TransactionReceipt';

function readTransaction(transactionId) {
  try {
    const saved = sessionStorage.getItem(`banking-ui-transaction:${transactionId}`);
    return { transaction: saved ? JSON.parse(saved) : null, error: '' };
  } catch {
    return { transaction: null, error: 'Transaction details could not be loaded in this browser session.' };
  }
}

export function TransactionDetailsPage({ transactionId }) {
  const [stored] = useState(() => readTransaction(transactionId));
  const { transaction, error } = stored;

  return (
    <>
      <PageHead title="Transaction details" sub="Review the status and parties for this transaction.">
        <Link to="/passbook" className="btn btn--ghost">Back to passbook</Link>
      </PageHead>
      {error ? <Alert tone="bad">{error}</Alert>
        : !transaction ? <div className="panel panel__body"><Alert tone="info">These transaction details are no longer available in this browser session. Return to the passbook and select the transaction again.</Alert></div>
          : <TransactionReceipt transaction={transaction} />}
    </>
  );
}
