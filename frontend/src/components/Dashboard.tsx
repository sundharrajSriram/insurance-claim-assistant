import { useEffect, useState } from 'react';
import { api } from '../api';
import type { ClaimState, Stats } from '../types';
import { DecisionBadge, formatMoney, formatTime } from './ui';
import WorkflowGraphView from './WorkflowGraph';

export default function Dashboard({
  refreshKey,
  onOpen,
}: {
  refreshKey: number;
  onOpen: (claim: ClaimState) => void;
}) {
  const [stats, setStats] = useState<Stats | null>(null);
  const [claims, setClaims] = useState<ClaimState[]>([]);

  useEffect(() => {
    api.stats().then(setStats).catch(() => setStats(null));
    api.listClaims().then(setClaims).catch(() => setClaims([]));
  }, [refreshKey]);

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1>Dashboard</h1>
          <p className="muted">Overview of claim volumes and agent decisions.</p>
        </div>
      </div>

      <div className="stat-grid">
        <StatCard label="Total claims" value={stats?.total ?? 0} tone="neutral" />
        <StatCard label="Approved" value={stats?.approved ?? 0} tone="approved" />
        <StatCard label="Rejected" value={stats?.rejected ?? 0} tone="rejected" />
        <StatCard label="Awaiting review" value={stats?.pendingReview ?? 0} tone="review" />
      </div>

      <div className="dash-layout">
        <div className="card">
          <h3>Recent Claims</h3>
          {claims.length === 0 && <p className="muted">No claims yet. Submit one to begin.</p>}
          {claims.slice(0, 8).map((c) => (
            <button key={c.claimId} className="list-row" onClick={() => onOpen(c)}>
              <span className="list-id">{c.claimId}</span>
              <span className="list-name">{c.customerName}</span>
              <span className="muted">{c.claimType}</span>
              <span>{formatMoney(c.claimAmount)}</span>
              <DecisionBadge decision={c.finalDecision} />
              <span className="muted list-time">{formatTime(c.submittedAt)}</span>
            </button>
          ))}
        </div>

        <div className="card">
          <h3>Agent Workflow</h3>
          <p className="muted">LangGraph-style multi-agent pipeline.</p>
          <WorkflowGraphView />
        </div>
      </div>
    </div>
  );
}

function StatCard({
  label,
  value,
  tone,
}: {
  label: string;
  value: number;
  tone: 'neutral' | 'approved' | 'rejected' | 'review';
}) {
  return (
    <div className={`stat-card stat-${tone}`}>
      <div className="stat-value">{value}</div>
      <div className="stat-label">{label}</div>
    </div>
  );
}
