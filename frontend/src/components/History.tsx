import { useEffect, useMemo, useState } from 'react';
import { api } from '../api';
import type { ClaimState, Decision } from '../types';
import { DecisionBadge, RiskBadge, formatMoney, formatTime } from './ui';

const FILTERS: (Decision | 'ALL')[] = ['ALL', 'APPROVED', 'REJECTED', 'MANUAL_REVIEW', 'PENDING'];

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
          <h1>Claim History</h1>
          <p className="muted">Every processed claim with its final decision and audit trail.</p>
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
          placeholder="Search id, name, policy…"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>

      <div className="card">
        <table className="table">
          <thead>
            <tr>
              <th>Claim</th>
              <th>Customer</th>
              <th>Type</th>
              <th>Amount</th>
              <th>Risk</th>
              <th>Decision</th>
              <th>Submitted</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((c) => (
              <tr key={c.claimId} onClick={() => onOpen(c)}>
                <td className="mono">{c.claimId}</td>
                <td>{c.customerName}</td>
                <td>{c.claimType}</td>
                <td>{formatMoney(c.claimAmount)}</td>
                <td>{c.riskLevel && <RiskBadge level={c.riskLevel} />}</td>
                <td>
                  <DecisionBadge decision={c.finalDecision} />
                </td>
                <td className="muted">{formatTime(c.submittedAt)}</td>
              </tr>
            ))}
            {rows.length === 0 && (
              <tr>
                <td colSpan={7} className="muted center">
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
