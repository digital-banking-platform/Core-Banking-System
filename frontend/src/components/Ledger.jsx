import { money, shortDate, shortTime } from '../utils/format';
import { Link } from '../router';

/* Show each transfer from the selected account owner's point of view. */
export function Ledger({ entries, accountNumber }) {
  return (
    <div className="ledger" role="list" aria-label="Passbook entries">
      {entries.map((t) => {
        const selectedAccount = String(accountNumber);
        const isSender = String(t.debitedFrom.accountNumber) === selectedAccount;
        const isReceiver = String(t.creditedTo.accountNumber) === selectedAccount;
        const out = isSender || !isReceiver;
        const other = out ? t.creditedTo : t.debitedFrom;
        return (
          <article className="ledger__row" role="listitem" key={t.transactionId} data-status={t.status} data-direction={out ? 'debit' : 'credit'}>
            <Link
              to={`/transaction/${encodeURIComponent(t.transactionId)}`}
              className="ledger__summary"
              aria-label={`View transaction: ${out ? 'paid to' : 'received from'} ${other.accountHolderName}, ${money(t.amount)}`}
              onClick={() => sessionStorage.setItem(`banking-ui-transaction:${t.transactionId}`, JSON.stringify(t))}
            >
                <span className="ledger__icon" aria-hidden="true">
                  <svg viewBox="0 0 24 24" fill="none">
                    {out
                      ? <path d="M7 17 17 7M8 7h9v9" />
                      : <path d="m17 7-10 10m0-9v9h9" />}
                  </svg>
                </span>
                <span className="ledger__party">
                  <span className="ledger__label">{out ? 'Paid to' : 'Received from'}</span>
                  <strong className="ledger__who">{other.accountHolderName}</strong>
                  <span className="ledger__date">{shortDate(t.completedAt)} · {shortTime(t.completedAt)}</span>
                </span>
                <span className={`ledger__amount ${out ? 'amt-dr' : 'amt-cr'}`}>
                  {out ? '−' : '+'}{money(t.amount)}
                </span>
            </Link>
          </article>
        );
      })}
    </div>
  );
}
