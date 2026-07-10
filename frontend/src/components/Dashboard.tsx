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

  const latestClaim = claims.length > 0 ? claims[0] : null;

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

      {/* Graph progression for latest claim */}
      {latestClaim && (
        <div className="card dash-progression">
          <div className="dash-prog-header">
            <h3>Latest Claim: Agent Progression</h3>
            <button className="link" onClick={() => onOpen(latestClaim)}>
              {latestClaim.claimId} &middot; {latestClaim.customerName}
            </button>
          </div>
          <p className="muted">
            Graph flow: validation &rarr; policy &rarr; documents &rarr; fraud &rarr; risk &rarr; recommendation
          </p>
          <div className="node-progression">
            <ProgBadge label="Validation" ok={latestClaim.validationStatus === 'VALID'} detail={latestClaim.validationStatus} />
            <ProgBadge label="Policy" ok={latestClaim.policyEligible} detail={latestClaim.policyStatus} />
            <ProgBadge label="Documents" ok={latestClaim.documentStatus === 'COMPLETE'} detail={latestClaim.documentStatus} />
            <ProgBadge label="Fraud" ok={latestClaim.fraudScore < 40} detail={`Score: ${latestClaim.fraudScore}`} />
            <ProgBadge label="Risk" ok={latestClaim.riskLevel === 'LOW'} detail={latestClaim.riskLevel} />
            <ProgBadge label="Decision" ok={latestClaim.recommendation === 'APPROVED'} detail={latestClaim.recommendation?.replace('_', ' ')} />
          </div>
        </div>
      )}

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

function ProgBadge({ label, ok, detail }: { label: string; ok: boolean; detail: string }) {
  return (
    <div className={`prog-badge ${ok ? 'prog-badge-pass' : 'prog-badge-fail'}`}>
      <span className="prog-badge-icon">{ok ? '\u2713' : '\u2717'}</span>
      <div className="prog-badge-text">
        <span className="prog-badge-label">{label}</span>
        <span className="prog-badge-detail">{detail}</span>
      </div>
    </div>
  );
}
