import type { ClaimState } from '../types';
import {
  DecisionBadge,
  RiskBadge,
  ScoreBar,
  StatusBadge,
  formatMoney,
  formatTime,
} from './ui';
import WorkflowGraphView from './WorkflowGraph';

export default function ClaimDetail({
  claim,
  onClose,
}: {
  claim: ClaimState;
  onClose: () => void;
}) {
  return (
    <div className="drawer-overlay" onClick={onClose}>
      <aside className="drawer" onClick={(e) => e.stopPropagation()}>
        <header className="drawer-header">
          <div>
            <div className="drawer-id">{claim.claimId}</div>
            <h2>{claim.customerName}</h2>
          </div>
          <button className="icon-btn" onClick={onClose} aria-label="Close">
            ×
          </button>
        </header>

        <div className="drawer-body">
          <div className="detail-decision">
            <div>
              <span className="muted">Final decision</span>
              <DecisionBadge decision={claim.finalDecision} />
            </div>
            <div>
              <span className="muted">Status</span>
              <StatusBadge status={claim.status} />
            </div>
            <div>
              <span className="muted">Risk</span>
              <RiskBadge level={claim.riskLevel} />
            </div>
          </div>

          <section className="detail-grid">
            <Field label="Policy" value={claim.policyNumber} />
            <Field label="Claim type" value={claim.claimType} />
            <Field label="Claim amount" value={formatMoney(claim.claimAmount)} />
            <Field label="Coverage limit" value={formatMoney(claim.coverageAmount)} />
            <Field label="Incident date" value={claim.incidentDate} />
            <Field label="Submitted" value={formatTime(claim.submittedAt)} />
          </section>

          <section className="card">
            <h3>Agent Node Outputs</h3>
            <NodeOut title="Claim Validation" ok={claim.validationStatus === 'VALID'}>
              {claim.validationStatus} — {claim.validationReason}
            </NodeOut>
            <NodeOut title="Policy Verification" ok={claim.policyEligible}>
              {claim.policyStatus} — {claim.coverageReason}
            </NodeOut>
            <NodeOut title="Document Verification" ok={claim.documentStatus === 'COMPLETE'}>
              {claim.documentStatus}
              {claim.missingDocuments.length > 0 &&
                ` — missing: ${claim.missingDocuments.join(', ')}`}
            </NodeOut>
            <div className="node-out">
              <div className="node-out-title">Fraud Detection</div>
              <ScoreBar value={claim.fraudScore} danger />
              <ul className="chips">
                {claim.fraudIndicators.map((f, i) => (
                  <li key={i} className="chip">
                    {f}
                  </li>
                ))}
              </ul>
            </div>
            <div className="node-out">
              <div className="node-out-title">
                Risk Assessment <RiskBadge level={claim.riskLevel} />
              </div>
              <ScoreBar value={claim.riskScore} danger />
            </div>
            <NodeOut title="Recommendation" ok={claim.recommendation === 'APPROVED'}>
              <DecisionBadge decision={claim.recommendation} /> {claim.recommendationReason}
            </NodeOut>
            {claim.reviewerDecision && (
              <NodeOut title="Human Review" ok={claim.reviewerDecision === 'APPROVED'}>
                <DecisionBadge decision={claim.reviewerDecision} /> by{' '}
                {claim.reviewerName || 'reviewer'}
                {claim.reviewerComments ? ` — ${claim.reviewerComments}` : ''}
              </NodeOut>
            )}
          </section>

          <section className="card">
            <h3>Workflow Path</h3>
            <WorkflowGraphView path={claim.nodePath} />
          </section>

          <section className="card">
            <h3>Audit Trail</h3>
            <ol className="timeline">
              {claim.auditLog.map((a, i) => (
                <li key={i}>
                  <span className="timeline-node">{a.node}</span>
                  <span className="timeline-msg">{a.message}</span>
                  <span className="timeline-time">{formatTime(a.timestamp)}</span>
                </li>
              ))}
            </ol>
          </section>
        </div>
      </aside>
    </div>
  );
}

function Field({ label, value }: { label: string; value: string }) {
  return (
    <div className="field">
      <span className="muted">{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function NodeOut({
  title,
  ok,
  children,
}: {
  title: string;
  ok: boolean;
  children: React.ReactNode;
}) {
  return (
    <div className="node-out">
      <div className="node-out-title">
        <span className={`dot ${ok ? 'dot-ok' : 'dot-bad'}`} />
        {title}
      </div>
      <div className="node-out-body">{children}</div>
    </div>
  );
}
