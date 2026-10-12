import { Link, useRoute } from '../router';
import { useAuth } from '../context/AuthContext';
import { useBank } from '../context/BankContext';
import { Icon } from './Icon';

const NAV = [
  { to: '/', label: 'Overview', icon: 'home' },
  { to: '/accounts', label: 'Accounts', icon: 'wallet' },
  { to: '/transfer', label: 'Send money', icon: 'send' },
  { to: '/passbook', label: 'Passbook', icon: 'book' },
  { to: '/profile', label: 'Profile', icon: 'user' },
];

export function AppLayout({ children }) {
  const path = useRoute();
  const { user, isStaff, signOut } = useAuth();
  const { profile, loading } = useBank();
  const desk = { to: '/admin', label: 'Bank desk', icon: 'shield' };
  const items = isStaff ? (!loading && !profile ? [desk] : [...NAV, desk]) : NAV;

  return (
    <>
      <div className="shell">
        <aside className="rail">
          <Link to="/" className="brand"><span className="brand__mark">S</span><span className="brand__name">Siddu Bank</span></Link>
          <nav className="nav" aria-label="Main">
            {items.map((i) => (
              <Link key={i.to} to={i.to} className="nav__link" aria-current={path === i.to ? 'page' : undefined}>
                <Icon name={i.icon} /> {i.label}
              </Link>
            ))}
          </nav>
          <div className="rail__foot">
            <div className="who">
              <div className="who__email" title={user.email}>{user.email}</div>
              <div className="who__role">{user.roles.join(', ').toLowerCase()}</div>
            </div>
            <button className="btn btn--ghost btn--sm" onClick={signOut}><Icon name="logout" size={16} /> Sign out</button>
          </div>
        </aside>
        <main className="main">{children}</main>
      </div>
    </>
  );
}

const SAMPLE = [
  ['01 Oct', 'Salary credited', '+84,500', 'cr'],
  ['03 Oct', 'Rent to landlord', '−22,000', 'dr'],
  ['06 Oct', 'Dinner split with Meera', '−1,250', 'dr'],
];

export function AuthLayout({ title, sub, children }) {
  return (
    <div className="auth-root">
      <div className="auth">
      <section className="auth__aside">
        <div className="brand"><span className="brand__mark">S</span><span className="brand__name">Siddu Bank</span></div>
        <div>
          <h2>Every rupee, written down.</h2>
          <p>Check balances, send money and read your passbook from one place.</p>
        </div>
        <div className="minileder" aria-hidden="true">
          {SAMPLE.map(([d, who, amt, k]) => (
            <div className="minileder__row" key={d}><span>{d}</span><span>{who}</span><span className={k}>{amt}</span></div>
          ))}
        </div>
      </section>
      <section className="auth__main">
        <div className="auth__card">
          <div><h1>{title}</h1>{sub && <p className="muted" style={{ marginTop: 6 }}>{sub}</p>}</div>
          {children}
        </div>
      </section>
      </div>
    </div>
  );
}
