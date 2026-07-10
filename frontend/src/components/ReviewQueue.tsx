import { useEffect, useState } from 'react';
import { api } from '../api';
import type { ClaimState, Decision } from '../types';
import { RiskBadge, ScoreBar, formatMoney } from './ui';

export default function ReviewQueue({
  refreshKey,
  onReviewed,
  onOpen,
}: {
  refreshKey: number;
  onReviewed: () => void;
  onOpen: (claim: ClaimState) => void;
}) {
  const [queue, setQueue] = useState<ClaimState[]>([]);
  const [reviewer, setReviewer] = useState('Claims Officer');
  const [comments, setComments] = useState<Record<string, string>>({});
  const [busy, setBusy] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const load = () => api.pendingReview().then(setQueue).catch(() => setQueue([]));

  useEffect(() => {
    load();
  }, [refreshKey]);

  const decide = async (id: string, decision: Decision) => {
    setBusy(id);
    setError(null);
    try {
      await api.review(id, decision, reviewer, comments[id] ?? '');
      await load();
      onReviewed();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Review failed');
    } finally {
      setBusy(null);
    }
  };

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1>Manual Review Queue</h1>
          <p className="muted">
            Human-in-the-loop review for risky or incomplete claims flagged by the agents.
          </p>
        </div>
        <label className="reviewer-field">
          Reviewer
          <input value={reviewer} onChange={(e) => setReviewer(e.target.value)} />
        </label>
      </div>

      {error && <div className="alert alert-error">{error}</div>}
      {queue.length === 0 && (
        <div className="card empty">
          <p className="muted">Nothing to review right now.</p>
        </div>
      )}

      <div className="review-list">
        {queue.map((c) => (
          <div key={c.claimId} className="card review-card">
            <div className="review-top">
              <button className="link" onClick={() => onOpen(c)}>
                {c.claimId} · {c.customerName}
              </button>
              <RiskBadge level={c.riskLevel} />
            </div>
            <div className="review-meta">
              <span>{c.claimType}</span>
              <span>{formatMoney(c.claimAmount)}</span>
              <span className="muted">Policy {c.policyNumber}</span>
            </div>
            <p className="review-reason">{c.recommendationReason}</p>
            <div className="review-scores">
              <div>
                <span className="muted">Fraud</span>
                <ScoreBar value={c.fraudScore} danger />
              </div>
              <div>
                <span className="muted">Risk</span>
                <ScoreBar value={c.riskScore} danger />
              </div>
            </div>
            <textarea
              rows={2}
              placeholder="Reviewer comments…"
              value={comments[c.claimId] ?? ''}
              onChange={(e) => setComments((m) => ({ ...m, [c.claimId]: e.target.value }))}
            />
            <div className="review-actions">
              <button
                className="btn btn-approve"
                disabled={busy === c.claimId}
                onClick={() => decide(c.claimId, 'APPROVED')}
              >
                Approve
              </button>
              <button
                className="btn btn-reject"
                disabled={busy === c.claimId}
                onClick={() => decide(c.claimId, 'REJECTED')}
              >
                Reject
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
