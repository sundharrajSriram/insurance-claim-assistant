import { useEffect, useState } from 'react';
import { api } from '../api';
import type { ClaimRequest, ClaimState, Policy } from '../types';
import { DecisionBadge, RiskBadge, ScoreBar, StatusBadge, formatMoney, formatTime } from './ui';

const DOCUMENT_OPTIONS = [
  'Hospital Report',
  'Medical Bills',
  'ID Proof',
  'Repair Bill',
  'Police FIR',
  'Damage Photos',
  'Repair Estimate',
  'Ownership Proof',
  'Booking Proof',
  'Receipts',
  'Death Certificate',
  'Nominee ID',
  'Policy Document',
];

const EMPTY: ClaimRequest = {
  claimId: '',
  customerName: '',
  policyNumber: '',
  claimType: 'Health',
  claimAmount: 0,
  incidentDate: '',
  description: '',
  documentsUploaded: [],
};

export default function SubmitClaim({ onProcessed }: { onProcessed: () => void }) {
  const [form, setForm] = useState<ClaimRequest>(EMPTY);
  const [types, setTypes] = useState<string[]>([]);
  const [policies, setPolicies] = useState<Policy[]>([]);
  const [samples, setSamples] = useState<Record<string, ClaimRequest>>({});
  const [result, setResult] = useState<ClaimState | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);
  const [step, setStep] = useState(0);

  useEffect(() => {
    api.claimTypes().then(setTypes).catch(() => setTypes(['Health', 'Motor', 'Property']));
    api.samples().then(setSamples).catch(() => setSamples({}));
    api.policies().then(setPolicies).catch(() => setPolicies([]));
  }, []);

  const set = <K extends keyof ClaimRequest>(k: K, v: ClaimRequest[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  const toggleDoc = (doc: string) =>
    setForm((f) => ({
      ...f,
      documentsUploaded: f.documentsUploaded.includes(doc)
        ? f.documentsUploaded.filter((d) => d !== doc)
        : [...f.documentsUploaded, doc],
    }));

  const loadSample = (key: string) => {
    if (samples[key]) {
      setForm(samples[key]);
      setResult(null);
      setError(null);
      setStep(0);
    }
  };

  const selectedPolicy = policies.find(
    (p) => p.policyNumber.toLowerCase() === form.policyNumber.toLowerCase()
  );

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    setResult(null);
    try {
      const claim = await api.submitClaim({ ...form, claimAmount: Number(form.claimAmount) });
      setResult(claim);
      setStep(2);
      onProcessed();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Submission failed');
    } finally {
      setBusy(false);
    }
  };

  const resetForm = () => {
    setForm(EMPTY);
    setResult(null);
    setError(null);
    setStep(0);
  };

  const STEPS = ['Claim Details', 'Documents & Review', 'Decision'];

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1>Submit a Claim</h1>
          <p className="muted">
            Fill in claim details, select documents, then run the multi-agent workflow.
          </p>
        </div>
        <div className="sample-buttons">
          {Object.keys(samples).map((key) => (
            <button key={key} type="button" className="btn btn-ghost" onClick={() => loadSample(key)}>
              Load {key.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Stepper */}
      <div className="stepper">
        {STEPS.map((label, i) => (
          <div key={i} className="stepper-item">
            <div
              className={`stepper-circle ${i < step ? 'stepper-done' : ''} ${i === step ? 'stepper-active' : ''}`}
            >
              {i < step ? '\u2713' : i + 1}
            </div>
            <span className={`stepper-label ${i === step ? 'stepper-label-active' : ''}`}>
              {label}
            </span>
            {i < STEPS.length - 1 && <div className={`stepper-line ${i < step ? 'stepper-line-done' : ''}`} />}
          </div>
        ))}
      </div>

      <div className="submit-layout-v2">
        {/* Left: Form */}
        <form className="card submit-form" onSubmit={submit}>
          {/* Section 1: Claimant Information */}
          {step === 0 && (
            <>
              <div className="form-section">
                <div className="form-section-header">
                  <span className="form-section-num">1</span>
                  <div>
                    <h3 className="form-section-title">Claimant Information</h3>
                    <p className="muted">Enter the customer and policy details.</p>
                  </div>
                </div>
                <div className="form-row">
                  <label>
                    Claim ID <span className="optional">(optional)</span>
                    <input
                      value={form.claimId}
                      onChange={(e) => set('claimId', e.target.value)}
                      placeholder="Auto-generated if blank"
                    />
                  </label>
                  <label>
                    Customer name <span className="required-star">*</span>
                    <input
                      value={form.customerName}
                      onChange={(e) => set('customerName', e.target.value)}
                      required
                      placeholder="Full name"
                    />
                  </label>
                </div>
                <div className="form-row">
                  <label>
                    Policy number <span className="required-star">*</span>
                    <input
                      value={form.policyNumber}
                      onChange={(e) => set('policyNumber', e.target.value)}
                      placeholder="e.g. POL-1001"
                      required
                    />
                    {selectedPolicy && (
                      <span className="field-hint field-hint-ok">
                        {selectedPolicy.holderName} &middot; {selectedPolicy.type} &middot;{' '}
                        {selectedPolicy.active ? 'Active' : 'Lapsed'} &middot;{' '}
                        {formatMoney(selectedPolicy.coverageLimit)}
                      </span>
                    )}
                    {form.policyNumber && !selectedPolicy && (
                      <span className="field-hint field-hint-warn">
                        Policy not found in registry
                      </span>
                    )}
                  </label>
                </div>
              </div>

              <div className="form-section">
                <div className="form-section-header">
                  <span className="form-section-num">2</span>
                  <div>
                    <h3 className="form-section-title">Incident Details</h3>
                    <p className="muted">Describe the claim event.</p>
                  </div>
                </div>
                <div className="form-row form-row-3">
                  <label>
                    Claim type <span className="required-star">*</span>
                    <select value={form.claimType} onChange={(e) => set('claimType', e.target.value)}>
                      {types.map((t) => (
                        <option key={t} value={t}>{t}</option>
                      ))}
                    </select>
                  </label>
                  <label>
                    Claim amount ({'\u20B9'}) <span className="required-star">*</span>
                    <input
                      type="number"
                      min={0}
                      value={form.claimAmount || ''}
                      onChange={(e) => set('claimAmount', Number(e.target.value))}
                      required
                      placeholder="0"
                    />
                  </label>
                  <label>
                    Incident date <span className="required-star">*</span>
                    <input
                      type="date"
                      value={form.incidentDate}
                      onChange={(e) => set('incidentDate', e.target.value)}
                      required
                    />
                  </label>
                </div>
                <label>
                  Incident description <span className="required-star">*</span>
                  <textarea
                    rows={4}
                    value={form.description}
                    onChange={(e) => set('description', e.target.value)}
                    placeholder="Describe what happened in detail (minimum 10 characters)..."
                    required
                  />
                </label>
              </div>

              <div className="form-actions">
                <button type="button" className="btn btn-primary" onClick={() => setStep(1)}
                  disabled={!form.customerName || !form.policyNumber || !form.claimType || !form.claimAmount || !form.incidentDate || !form.description}
                >
                  Next: Select Documents
                </button>
              </div>
            </>
          )}

          {/* Section 2: Documents */}
          {step === 1 && (
            <>
              <div className="form-section">
                <div className="form-section-header">
                  <span className="form-section-num">3</span>
                  <div>
                    <h3 className="form-section-title">Document Upload</h3>
                    <p className="muted">Select documents uploaded with this claim. Missing required documents will be flagged.</p>
                  </div>
                </div>
                <div className="doc-grid">
                  {DOCUMENT_OPTIONS.map((doc) => (
                    <button
                      type="button"
                      key={doc}
                      className={`doc-card ${form.documentsUploaded.includes(doc) ? 'doc-card-on' : ''}`}
                      onClick={() => toggleDoc(doc)}
                    >
                      <span className="doc-check">{form.documentsUploaded.includes(doc) ? '\u2713' : ''}</span>
                      <span className="doc-name">{doc}</span>
                    </button>
                  ))}
                </div>
                <div className="doc-summary">
                  <span className="muted">
                    {form.documentsUploaded.length} document{form.documentsUploaded.length !== 1 ? 's' : ''} selected
                  </span>
                </div>
              </div>

              {/* Quick summary before submit */}
              <div className="form-section">
                <div className="form-section-header">
                  <span className="form-section-num">4</span>
                  <div>
                    <h3 className="form-section-title">Review & Submit</h3>
                    <p className="muted">Verify claim details before running the workflow.</p>
                  </div>
                </div>
                <div className="submit-summary">
                  <div className="summary-row">
                    <span className="muted">Customer</span>
                    <strong>{form.customerName}</strong>
                  </div>
                  <div className="summary-row">
                    <span className="muted">Policy</span>
                    <strong>{form.policyNumber}</strong>
                  </div>
                  <div className="summary-row">
                    <span className="muted">Type</span>
                    <strong>{form.claimType}</strong>
                  </div>
                  <div className="summary-row">
                    <span className="muted">Amount</span>
                    <strong>{form.claimAmount ? formatMoney(form.claimAmount) : '-'}</strong>
                  </div>
                  <div className="summary-row">
                    <span className="muted">Incident</span>
                    <strong>{form.incidentDate || '-'}</strong>
                  </div>
                  {form.claimId && (
                    <div className="summary-row">
                      <span className="muted">Claim ID</span>
                      <strong>{form.claimId}</strong>
                    </div>
                  )}
                </div>
              </div>

              {error && <div className="alert alert-error">{error}</div>}

              <div className="form-actions">
                <button type="button" className="btn btn-ghost" onClick={() => setStep(0)}>
                  Back
                </button>
                <button className="btn btn-primary btn-submit" disabled={busy} type="submit">
                  {busy ? 'Processing...' : 'Run Multi-Agent Workflow'}
                </button>
              </div>
            </>
          )}

          {/* Step 3: shown only after result */}
          {step === 2 && result && (
            <>
              <div className="decision-hero">
                <div className={`decision-icon ${result.finalDecision === 'APPROVED' ? 'decision-icon-ok' : result.finalDecision === 'REJECTED' ? 'decision-icon-bad' : 'decision-icon-review'}`}>
                  {result.finalDecision === 'APPROVED' ? '\u2713' : result.finalDecision === 'REJECTED' ? '\u2717' : '\u2691'}
                </div>
                <div className="decision-hero-body">
                  <h2 className="decision-hero-title">
                    {result.finalDecision === 'APPROVED' ? 'Claim Approved'
                      : result.finalDecision === 'REJECTED' ? 'Claim Rejected'
                      : 'Sent to Manual Review'}
                  </h2>
                  <div className="decision-hero-id">{result.claimId}</div>
                </div>
              </div>

              <div className="decision-badges-row">
                <DecisionBadge decision={result.finalDecision} />
                <StatusBadge status={result.status} />
                {result.riskLevel && <RiskBadge level={result.riskLevel} />}
              </div>

              <p className="decision-reason">{result.recommendationReason}</p>

              {/* Node progression */}
              <div className="form-section">
                <h3 className="form-section-title">Agent Progression</h3>
                <div className="node-progression">
                  <NodeStep label="Validation" status={result.validationStatus === 'VALID' ? 'pass' : 'fail'} detail={result.validationStatus} />
                  <NodeStep label="Policy" status={result.policyEligible ? 'pass' : 'fail'} detail={result.policyStatus} />
                  <NodeStep label="Documents" status={result.documentStatus === 'COMPLETE' ? 'pass' : 'fail'} detail={result.documentStatus} />
                  <NodeStep label="Fraud" status={result.fraudScore < 40 ? 'pass' : result.fraudScore < 70 ? 'warn' : 'fail'} detail={`Score: ${result.fraudScore}`} />
                  <NodeStep label="Risk" status={result.riskLevel === 'LOW' ? 'pass' : result.riskLevel === 'MEDIUM' ? 'warn' : 'fail'} detail={result.riskLevel} />
                  <NodeStep label="Decision" status={result.recommendation === 'APPROVED' ? 'pass' : result.recommendation === 'REJECTED' ? 'fail' : 'warn'} detail={result.recommendation?.replace('_', ' ')} />
                </div>
              </div>

              {/* Scores */}
              <div className="decision-scores">
                <div>
                  <span className="muted">Fraud Score</span>
                  <ScoreBar value={result.fraudScore} danger />
                </div>
                <div>
                  <span className="muted">Risk Score</span>
                  <ScoreBar value={result.riskScore} danger />
                </div>
              </div>

              {/* Audit */}
              <div className="form-section">
                <h3 className="form-section-title">Audit Trail</h3>
                <ul className="mini-audit-v2">
                  {result.auditLog.map((a, i) => (
                    <li key={i}>
                      <span className="mini-audit-node">{a.node}</span>
                      <span className="mini-audit-msg">{a.message}</span>
                      <span className="mini-audit-time">{formatTime(a.timestamp)}</span>
                    </li>
                  ))}
                </ul>
              </div>

              {result.status === 'PENDING_REVIEW' && (
                <div className="alert alert-review">
                  This claim has been routed to the Manual Review queue for a human decision.
                </div>
              )}

              <div className="form-actions">
                <button type="button" className="btn btn-primary" onClick={resetForm}>
                  Submit Another Claim
                </button>
              </div>
            </>
          )}
        </form>

        {/* Right sidebar - Policy reference */}
        {step < 2 && (
          <div className="submit-sidebar">
            <div className="card">
              <h3>Available Policies</h3>
              <p className="muted">Reference list of registered policies.</p>
              <div className="policy-list">
                {policies.map((p) => (
                  <button
                    type="button"
                    key={p.policyNumber}
                    className={`policy-item ${form.policyNumber === p.policyNumber ? 'policy-item-active' : ''}`}
                    onClick={() => {
                      set('policyNumber', p.policyNumber);
                      set('customerName', p.holderName);
                      set('claimType', p.type);
                    }}
                  >
                    <div className="policy-item-top">
                      <span className="policy-num">{p.policyNumber}</span>
                      <span className={`badge ${p.active ? 'badge-approved' : 'badge-rejected'}`}>
                        {p.active ? 'Active' : 'Lapsed'}
                      </span>
                    </div>
                    <div className="policy-name">{p.holderName}</div>
                    <div className="policy-meta">
                      <span>{p.type}</span>
                      <span>{formatMoney(p.coverageLimit)}</span>
                    </div>
                  </button>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function NodeStep({ label, status, detail }: { label: string; status: 'pass' | 'warn' | 'fail'; detail: string }) {
  const cls = status === 'pass' ? 'node-step-pass' : status === 'warn' ? 'node-step-warn' : 'node-step-fail';
  const icon = status === 'pass' ? '\u2713' : status === 'warn' ? '!' : '\u2717';
  return (
    <div className={`node-step ${cls}`}>
      <span className="node-step-icon">{icon}</span>
      <div className="node-step-info">
        <span className="node-step-label">{label}</span>
        <span className="node-step-detail">{detail}</span>
      </div>
    </div>
  );
}
