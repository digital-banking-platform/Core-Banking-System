import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';
import { useAuth } from './AuthContext';
import * as accountApi from '../api/accountApi';

const BankContext = createContext(null);
const primaryAccountKey = (email) => `siddu.primary-account:${email}`;

function readPrimaryAccount(email) {
  try { return localStorage.getItem(primaryAccountKey(email)); } catch { return null; }
}

function savePrimaryAccount(email, accountNumber) {
  try { localStorage.setItem(primaryAccountKey(email), accountNumber); } catch { /* storage unavailable */ }
}

/* Loads the signed-in customer's profile and accounts once, and exposes reload().
   profile === null after loading means onboarding is not finished. */
export function BankProvider({ children }) {
  const { user } = useAuth();
  const [state, setState] = useState({ loading: true, profile: null, accounts: [], error: '' });
  const [activeNumber, setActiveNumber] = useState(null);

  const reload = useCallback(async () => {
    setState((s) => ({ ...s, loading: true, error: '' }));
    try {
      let profile = null;
      try { profile = await accountApi.getProfile(); } catch (e) { if (e.status !== 404) throw e; }
      const accounts = profile ? await accountApi.getAccounts() : [];
      setState({ loading: false, profile, accounts, error: '' });
      const savedPrimary = user?.email ? readPrimaryAccount(user.email) : null;
      setActiveNumber((cur) => {
        const selected = accounts.find((a) => a.accountNumber === savedPrimary)
          || accounts.find((a) => a.accountNumber === cur)
          || accounts[0];
        if (selected && user?.email) savePrimaryAccount(user.email, selected.accountNumber);
        return selected?.accountNumber || null;
      });
    } catch (e) {
      setState((s) => ({ ...s, loading: false, error: e.message }));
    }
  }, [user?.email]);

  useEffect(() => { if (user) reload(); }, [user, reload]);

  const setPrimary = useCallback((accountNumber) => {
    if (!state.accounts.some((account) => account.accountNumber === accountNumber)) return;
    setActiveNumber(accountNumber);
    if (user?.email) savePrimaryAccount(user.email, accountNumber);
  }, [state.accounts, user?.email]);

  const value = useMemo(() => ({
    ...state,
    reload,
    active: state.accounts.find((a) => a.accountNumber === activeNumber) || null,
    setActive: setPrimary,
    setPrimary,
  }), [state, reload, activeNumber, setPrimary]);

  return <BankContext.Provider value={value}>{children}</BankContext.Provider>;
}

export const useBank = () => useContext(BankContext);
