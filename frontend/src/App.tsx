import { useState } from 'react';
import Dashboard from './components/Dashboard';
import SubmitClaim from './components/SubmitClaim';
import ReviewQueue from './components/ReviewQueue';
import History from './components/History';
import ClaimDetail from './components/ClaimDetail';
import type { ClaimState } from './types';

type Tab = 'dashboard' | 'submit' | 'review' | 'history';

const NAV: { id: Tab; label: string; icon: string }[] = [
  { id: 'dashboard', label: 'Dashboard', icon: '▚' },
  { id: 'submit', label: 'Submit Claim', icon: '＋' },
  { id: 'review', label: 'Manual Review', icon: '⚑' },
  { id: 'history', label: 'History', icon: '≣' },
];

export default function App() {
  const [tab, setTab] = useState<Tab>('dashboard');
  const [refreshKey, setRefreshKey] = useState(0);
  const [selected, setSelected] = useState<ClaimState | null>(null);

  const refresh = () => setRefreshKey((k) => k + 1);

  return (
    <div className="app">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">CF</div>
          <div>
            <div className="brand-name">ClaimFlow</div>
            <div className="brand-sub">Multi-Agent BFSI</div>
          </div>
        </div>
        <nav>
          {NAV.map((n) => (
            <button
              key={n.id}
              className={`nav-item ${tab === n.id ? 'nav-item-active' : ''}`}
              onClick={() => setTab(n.id)}
            >
              <span className="nav-icon">{n.icon}</span>
              {n.label}
            </button>
          ))}
        </nav>
        <div className="sidebar-foot">
          <div className="muted">Insurance Claim Processing</div>
          <div className="muted">LangGraph-style workflow · Spring Boot</div>
        </div>
      </aside>

      <main className="content">
        {tab === 'dashboard' && <Dashboard refreshKey={refreshKey} onOpen={setSelected} />}
        {tab === 'submit' && <SubmitClaim onProcessed={refresh} />}
        {tab === 'review' && (
          <ReviewQueue refreshKey={refreshKey} onReviewed={refresh} onOpen={setSelected} />
        )}
        {tab === 'history' && <History refreshKey={refreshKey} onOpen={setSelected} />}
      </main>

      {selected && <ClaimDetail claim={selected} onClose={() => setSelected(null)} />}
    </div>
  );
}
