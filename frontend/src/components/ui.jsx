import { useId } from 'react';
import { Icon } from './Icon';

export function Field({ label, hint, error, children }) {
  return (
    <div className="field">
      <label>{label}</label>
      {children}
      {error ? <span className="field__error" role="alert">{error}</span> : hint ? <span className="field__hint">{hint}</span> : null}
    </div>
  );
}

/* Input that wires label, hint and error together for screen readers */
export function TextField({ label, hint, error, className = '', ...props }) {
  const id = useId();
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      <input id={id} className={`input ${className}`} aria-invalid={!!error} aria-describedby={`${id}-d`} {...props} />
      <span id={`${id}-d`} className={error ? 'field__error' : 'field__hint'} role={error ? 'alert' : undefined}>{error || hint}</span>
    </div>
  );
}

export function SelectField({ label, hint, error, children, ...props }) {
  const id = useId();
  return (
    <div className="field">
      <label htmlFor={id}>{label}</label>
      <select id={id} className="select" aria-invalid={!!error} {...props}>{children}</select>
      {error ? <span className="field__error" role="alert">{error}</span> : hint ? <span className="field__hint">{hint}</span> : null}
    </div>
  );
}

export function Button({ variant, size, block, busy, children, ...props }) {
  const cls = ['btn', variant && `btn--${variant}`, size && `btn--${size}`, block && 'btn--block'].filter(Boolean).join(' ');
  return (
    <button className={cls} disabled={busy || props.disabled} {...props}>
      {busy && <span className="spinner" aria-hidden="true" />}
      {children}
    </button>
  );
}

export function Alert({ tone = 'info', children }) {
  const icon = { bad: 'alert', wait: 'clock', ok: 'check', info: 'info' }[tone];
  return <div className={`alert alert--${tone}`} role={tone === 'bad' ? 'alert' : undefined}><Icon name={icon} /><div>{children}</div></div>;
}

const STAMPS = {
  VERIFIED: ['ok', 'KYC verified'], PENDING: ['wait', 'Pending'], REJECTED: ['bad', 'KYC rejected'],
  SUCCESS: ['ok', 'Success'], FAILED: ['bad', 'Failed'],
  ACTIVE: ['ok', 'Active'], SUSPENDED: ['bad', 'Suspended'], CLOSED: ['idle', 'Closed'],
  BLOCKED: ['bad', 'Blocked'], DORMANT: ['idle', 'Dormant'], DEACTIVATE: ['idle', 'Deactivated'],
};
export function Stamp({ status, label, big }) {
  const [tone, text] = STAMPS[status] || ['idle', status];
  return <span className={`stamp stamp--${tone} ${big ? 'stamp--big' : ''}`}>{label || text}</span>;
}

export function Pager({ page, totalPages, onChange }) {
  if (!totalPages || totalPages <= 1) return null;
  return (
    <div className="pager">
      <Button variant="ghost" size="sm" onClick={() => onChange(page - 1)} disabled={page <= 0}>Previous</Button>
      <span className="num">Page {page + 1} of {totalPages}</span>
      <Button variant="ghost" size="sm" onClick={() => onChange(page + 1)} disabled={page >= totalPages - 1}>Next</Button>
    </div>
  );
}

export function Empty({ title, children, action }) {
  return <div className="empty"><h3>{title}</h3><p>{children}</p>{action}</div>;
}

export function Skeleton({ h = 18, w = '100%' }) {
  return <div className="skeleton" style={{ height: h, width: w }} />;
}

export function PageHead({ title, sub, children }) {
  return (
    <div className="page-head">
      <div><h1>{title}</h1>{sub && <p>{sub}</p>}</div>
      {children}
    </div>
  );
}
