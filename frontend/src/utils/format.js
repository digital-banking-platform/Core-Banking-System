const inr = new Intl.NumberFormat('en-IN', { style: 'currency', currency: 'INR', minimumFractionDigits: 2 });

export function money(value) {
  const n = Number(value);
  return Number.isFinite(n) ? inr.format(n) : '—';
}

export function groupAccount(n = '') {
  return String(n).replace(/(\d{4})(?=\d)/g, '$1 ').trim();
}

export function maskAccount(n = '') {
  return `••${String(n).slice(-4)}`;
}

export function shortDate(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleDateString('en-IN', { day: '2-digit', month: 'short', year: 'numeric' });
}

export function shortTime(iso) {
  if (!iso) return '';
  return new Date(iso).toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });
}

export function greeting() {
  const h = new Date().getHours();
  if (h < 12) return 'Good morning';
  if (h < 17) return 'Good afternoon';
  return 'Good evening';
}

export function firstName(full = '') {
  return String(full).trim().split(/\s+/)[0] || '';
}

export function titleCase(s = '') {
  return s.charAt(0) + s.slice(1).toLowerCase();
}

export function newIdempotencyKey() {
  if (globalThis.crypto && crypto.randomUUID) return crypto.randomUUID();
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = (Math.random() * 16) | 0;
    return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16);
  });
}

export function stripRole(r = '') {
  return String(r).replace(/^ROLE_/, '');
}
