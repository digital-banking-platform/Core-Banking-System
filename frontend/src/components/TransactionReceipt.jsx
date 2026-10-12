import { groupAccount, money, shortDate, shortTime } from '../utils/format';

export function TransactionReceipt({ transaction }) {
  const status = {
    SUCCESS: { title: 'Transaction successful', icon: 'success' },
    FAILED: { title: 'Transaction failed', icon: 'failed' },
    PENDING: { title: 'Transaction pending', icon: 'pending' },
  }[transaction.status];

  return (
    <section className="panel transaction-card">
      <div className={`transaction-card__receipt transaction-card__receipt--${status?.icon || 'unknown'}`}>
        <div className="transaction-card__status">
          <span className="transaction-card__status-icon" aria-hidden="true">
            {status?.icon === 'success' ? (
              <svg viewBox="0 0 24 24" fill="none"><path d="m5 12 4 4L19 6" /></svg>
            ) : status?.icon === 'failed' ? (
              <svg viewBox="0 0 24 24" fill="none"><path d="m7 7 10 10M17 7 7 17" /></svg>
            ) : <span>!</span>}
          </span>
          <h2>{status?.title || `Transaction ${transaction.status.toLowerCase()}`}</h2>
        </div>
        {transaction.status === 'FAILED' && transaction.failureReason && (
          <p className="transaction-card__reason">{transaction.failureReason}</p>
        )}
        <strong className="transaction-card__amount">{money(transaction.amount)}</strong>
        <span className="transaction-card__time">{shortDate(transaction.completedAt)} · {shortTime(transaction.completedAt)}</span>
      </div>
      <dl className="transaction-card__facts">
        <div className="transaction-card__fact transaction-card__fact--reference">
          <dt>Reference number</dt>
          <dd className="acct">{transaction.transactionId}</dd>
        </div>
        <div className="transaction-card__fact">
          <dt>Sender</dt>
          <dd>{transaction.debitedFrom.accountHolderName}<span className="acct">{groupAccount(transaction.debitedFrom.accountNumber)}</span></dd>
        </div>
        <div className="transaction-card__fact">
          <dt>Receiver</dt>
          <dd>{transaction.creditedTo.accountHolderName}<span className="acct">{groupAccount(transaction.creditedTo.accountNumber)}</span></dd>
        </div>
        {transaction.description && (
          <div className="transaction-card__fact transaction-card__fact--reference">
            <dt>Description</dt>
            <dd>{transaction.description}</dd>
          </div>
        )}
      </dl>
    </section>
  );
}
