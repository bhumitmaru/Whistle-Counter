import { Flame, Menu, X } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, NavLink, Outlet, useLocation } from "react-router-dom";

const navItems = [
  { to: "/", label: "Counter", end: true },
  { to: "/how-it-works", label: "How it works" },
  { to: "/about", label: "About" },
  { to: "/faq", label: "FAQ" },
];

export default function SiteLayout() {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();
  useEffect(() => {
    const target = location.hash && document.querySelector(location.hash);
    if (target) requestAnimationFrame(() => target.scrollIntoView({ behavior: "smooth" }));
    else window.scrollTo({ top: 0, behavior: "instant" });
  }, [location.pathname, location.hash]);
  return (
    <div className="site-shell">
      <a className="skip-link" href="#main">Skip to content</a>
      <header className="site-header">
        <div className="site-header-inner">
          <Link to="/" className="brand" aria-label="Whistle Counter home" onClick={() => setMenuOpen(false)}>
            <span className="brand-mark"><Flame aria-hidden="true" /></span>
            <span>Whistle <span className="brand-light">Counter</span></span>
          </Link>
          <button className="menu-toggle" type="button" aria-expanded={menuOpen} aria-controls="site-nav" aria-label={menuOpen ? "Close navigation" : "Open navigation"} onClick={() => setMenuOpen(!menuOpen)}>
            {menuOpen ? <X /> : <Menu />}
          </button>
          <nav id="site-nav" className={`site-nav${menuOpen ? " is-open" : ""}`} aria-label="Main navigation">
            {navItems.map(({ to, label, end }) => <NavLink key={to} to={to} end={end} onClick={() => setMenuOpen(false)} className={({ isActive }) => isActive ? "nav-link active" : "nav-link"}>{label}</NavLink>)}
            <Link className="nav-cta" to="/#counter" onClick={() => setMenuOpen(false)}>Use the counter <span aria-hidden="true">↗</span></Link>
          </nav>
        </div>
      </header>
      <main id="main"><Outlet /></main>
      <footer className="site-footer">
        <div className="footer-inner">
          <Link to="/" className="brand footer-brand"><span className="brand-mark"><Flame aria-hidden="true" /></span><span>Whistle <span className="brand-light">Counter</span></span></Link>
          <p>A simple helper for a busy kitchen. Stay nearby and follow your cooker’s instructions.</p>
          <div className="footer-links"><Link to="/how-it-works">How it works</Link><Link to="/about">About</Link><Link to="/faq">FAQ</Link></div>
          <small>© {new Date().getFullYear()} Whistle Counter <span className="footer-credit">· Built by <a href="https://www.linkedin.com/in/bhumitmaru/" target="_blank" rel="noreferrer">Bhumit Maru <span aria-hidden="true">↗</span></a></span></small>
        </div>
      </footer>
    </div>
  );
}
