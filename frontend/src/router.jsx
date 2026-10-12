import { useEffect, useState } from 'react';

/* Tiny hash router so the app has zero routing dependencies.
   Swap for react-router-dom later: only useRoute, navigate and Link are used. */
const read = () => (window.location.hash.replace(/^#/, '') || '/').split('?')[0];

export function useRoute() {
  const [path, setPath] = useState(read());
  useEffect(() => {
    const onChange = () => { setPath(read()); window.scrollTo(0, 0); };
    window.addEventListener('hashchange', onChange);
    return () => window.removeEventListener('hashchange', onChange);
  }, []);
  return path;
}

export function navigate(to) {
  window.location.hash = to;
}

export function Link({ to, children, ...rest }) {
  return <a href={`#${to}`} {...rest}>{children}</a>;
}
