import { useEffect, useMemo, useState } from 'react';
import { api } from '../api';
import type { ClaimState, Decision } from '../types';
import { DecisionBadge, RiskBadge, formatMoney, formatTime } from './ui';

const FILTERS: (Decision | 'ALL')[] = ['ALL', 'APPROVED', 'REJECTED', 'MANUAL_REVIEW', 'PENDING'];

function formatPath(path: string[]): string {
  if (!path || path.length === 0) return '-';
  return path.map((p) => p.replace(/_/g, ' ')).join(' \u2192 ');
}

export default function History({
  refreshKey,
  onOpen,
}: {
  refreshKey: number;
  onOpen: (claim: ClaimState) => void;
}) {
  const [claims, setClaims] = useState<ClaimState[]>([]);
  const [filter, setFilter] = useState<Decision | 'ALL'>('ALL');
  const [query, setQuery] = useState('');
  const [expanded, setExpanded] = useState<string | null>(null);

  useEffect(() => {
    api.listClaims().then(setClaims).catch(() => setClaims([]));
  }, [refreshKey]);

  const rows = useMemo(() => {
    return claims.filter((c) => {
      const matchFilter = filter === 'ALL' || c.finalDecision === filter;
      const q = query.trim().toLowerCase();
      const matchQuery =
        !q ||
        c.claimId.toLowerCase().includes(q) ||
        c.customerName.toLowerCase().includes(q) ||
        c.policyNumber.toLowerCase().includes(q);
      return matchFilter && matchQuery;
    });
  }, [claims, filter, query]);

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1>Claim History / Audit Trail</h1>
          <p className="muted">Every processed claim with graph path, node outputs, AI recommendation, reviewer decision, and final status.</p>
        </div>
      </div>

      <div className="toolbar">
        <div className="filter-tabs">
          {FILTERS.map((f) => (
            <button
              key={f}
              className={`filter-tab ${filter === f ? 'filter-tab-active' : ''}`}
              onClick={() => setFilter(f)}
            >
              {f.replace('_', ' ')}
            </button>
          ))}
        </div>
        <input
          className="search"
          placeholder="Search id, name, policy..."
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      <div className="card history-card">
        <table className="table table-history">
          <thead>
            <tr>
              <th>Claim</th>
              <th>Customer</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Risk</th>
              <th>AI Rec.</th>
              <th>Reviewer</th>
              <th>Final</th>
              <th>Submitted</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {rows.map((c) => (
              <>
                <tr key={c.claimId} onClick={() => onOpen(c)}>
                  <td className="mono">{c.claimId}</td>
                  <td>{c.customerName}</td>
                  <td>{c.claimType}</td>
                  <td>{formatMoney(c.claimAmount)}</td>
                  <td>{c.riskLevel && <RiskBadge level={c.riskLevel} />}</td>
                  <td>
                    {c.recommendation && <DecisionBadge decision={c.recommendation} />}
                  </td>
                  <td>
                    {c.reviewerDecision ? (
                      <span>
                        <DecisionBadge decision={c.reviewerDecision} />
                        {c.reviewerName && <span className="muted reviewer-hint"> {c.reviewerName}</span>}
                      </span>
                    ) : (
                      <span className="muted">-</span>
                    )}
                  </td>
                  <td>
                    <DecisionBadge decision={c.finalDecision} />
                  </td>
                  <td className="muted">{formatTime(c.submittedAt)}</td>
                  <td>
                    <button
                      className="expand-btn"
                      onClick={(e) => {
                        e.stopPropagation();
                        setExpanded(expanded === c.claimId ? null : c.claimId);
                      }}
                      title="Toggle details"
                    >
                      {expanded === c.claimId ? '\u25B2' : '\u25BC'}
                    </button>
                  </td>
                </tr>
                {expanded === c.claimId && (
                  <tr key={c.claimId + '-detail'} className="history-expand-row">
                    <td colSpan={10}>
                      <div className="history-expand">
                        <div className="history-expand-section">
                          <span className="history-expand-label">Graph Path</span>
                          <span className="history-expand-path">{formatPath(c.nodePath)}</span>
                        </div>
                        <div className="history-expand-section">
                          <span className="history-expand-label">Node Outputs</span>
                          <div className="history-nodes">
                            <NodeBadge label="Validation" value={c.validationStatus} ok={c.validationStatus === 'VALID'} />
                            <NodeBadge label="Policy" value={c.policyStatus} ok={c.policyEligible} />
                            <NodeBadge label="Documents" value={c.documentStatus} ok={c.documentStatus === 'COMPLETE'} />
                            <NodeBadge label="Fraud" value={`Score: ${c.fraudScore}`} ok={c.fraudScore < 40} />
                            <NodeBadge label="Risk" value={c.riskLevel} ok={c.riskLevel === 'LOW'} />
                          </div>
                        </div>
                        <div className="history-expand-section">
                          <span className="history-expand-label">AI Recommendation</span>
                          <span>{c.recommendationReason}</span>
                        </div>
                        {c.reviewerComments && (
                          <div className="history-expand-section">
                            <span className="history-expand-label">Reviewer Comments</span>
                            <span>{c.reviewerComments}</span>
                          </div>
                        )}
                      </div>
                    </td>
                  </tr>
                )}
              </>
            ))}
            {rows.length === 0 && (
              <tr>
                <td colSpan={10} className="muted center">
                  No claims match.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function NodeBadge({ label, value, ok }: { label: string; value: string; ok: boolean }) {
  return (
    <span className={`hist-node-badge ${ok ? 'hist-node-pass' : 'hist-node-fail'}`}>
      <span className="hist-node-icon">{ok ? '\u2713' : '\u2717'}</span>
      <span className="hist-node-label">{label}:</span> {value}
    </span>
  );
}
