import { groupAccount, titleCase } from '../utils/format';
import { Link } from '../router';
import { Stamp } from './ui';

/* Balance is never sent with the account list; reveal it only after PIN verification. */
export function PassbookCover({ account, kycStatus, isPrimary = false, onSetPrimary }) {
  return (
    <section className="cover" aria-label={`${titleCase(account.type)} account`}>
      <div className="cover__top">
        <div>
          <div className="cover__type">{titleCase(account.type)} account{isPrimary ? ' · Primary' : ''}</div>
          <div className="cover__name">{account.holderName}</div>
        </div>
        {kycStatus && <Stamp status={kycStatus} />}
      </div>
      <div className="cover__number" aria-label={`Account number ${account.accountNumber}`}>{groupAccount(account.accountNumber)}</div>
      <div className="cover__meta">
        <span>IFSC<b className="acct">{account.ifsc}</b></span>
        <span>Branch<b>{account.branch}</b></span>
      </div>
      <div className="cover__bal">
        {onSetPrimary && !isPrimary && <button type="button" className="btn btn--ghost btn--sm" onClick={() => onSetPrimary(account.accountNumber)}>Set as primary</button>}
        <Link to={`/balance/${encodeURIComponent(account.accountNumber)}`} className="btn btn--sm">Check balance</Link>
      </div>
    </section>
  );
}
