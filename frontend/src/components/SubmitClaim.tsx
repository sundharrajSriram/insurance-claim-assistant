import { useEffect, useState } from 'react';
import { api } from '../api';
import type { ClaimRequest, ClaimState } from '../types';
import { DecisionBadge, StatusBadge } from './ui';

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
  const [samples, setSamples] = useState<Record<string, ClaimRequest>>({});
  const [result, setResult] = useState<ClaimState | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    api.claimTypes().then(setTypes).catch(() => setTypes(['Health', 'Motor', 'Property']));
    api.samples().then(setSamples).catch(() => setSamples({}));
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
    }
  };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError(null);
    setResult(null);
    try {
      const claim = await api.submitClaim({ ...form, claimAmount: Number(form.claimAmount) });
      setResult(claim);
      onProcessed();
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Submission failed');
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="page">
      <div className="page-head">
        <div>
          <h1>Submit a Claim</h1>
          <p className="muted">
            The claim runs through the multi-agent workflow and returns a decision instantly.
          </p>
        </div>
        <div className="sample-buttons">
          {Object.keys(samples).map((key) => (
            <button key={key} type="button" className="btn btn-ghost" onClick={() => loadSample(key)}>
              Load {key.replace('_', ' ')} sample
            </button>
          ))}
        </div>
      </div>

      <div className="submit-layout">
        <form className="card form" onSubmit={submit}>
          <div className="form-row">
            <label>
              Customer name
              <input
                value={form.customerName}
                onChange={(e) => set('customerName', e.target.value)}
                required
              />
            </label>
            <label>
              Policy number
              <input
                value={form.policyNumber}
                onChange={(e) => set('policyNumber', e.target.value)}
                placeholder="POL-1001"
                required
              />
            </label>
          </div>

          <div className="form-row">
            <label>
              Claim type
              <select value={form.claimType} onChange={(e) => set('claimType', e.target.value)}>
                {types.map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
              </select>
            </label>
            <label>
              Claim amount (₹)
              <input
                type="number"
                min={0}
                value={form.claimAmount || ''}
                onChange={(e) => set('claimAmount', Number(e.target.value))}
                required
              />
            </label>
            <label>
              Incident date
              <input
                type="date"
                value={form.incidentDate}
                onChange={(e) => set('incidentDate', e.target.value)}
                required
              />
            </label>
          </div>

          <label>
            Incident description
            <textarea
              rows={3}
              value={form.description}
              onChange={(e) => set('description', e.target.value)}
              placeholder="Describe what happened…"
              required
            />
          </label>

          <div className="docs">
            <span className="muted">Documents uploaded</span>
            <div className="chips chips-select">
              {DOCUMENT_OPTIONS.map((doc) => (
                <button
                  type="button"
                  key={doc}
                  className={`chip chip-toggle ${
                    form.documentsUploaded.includes(doc) ? 'chip-on' : ''
                  }`}
                  onClick={() => toggleDoc(doc)}
                >
                  {doc}
                </button>
              ))}
            </div>
          </div>

          {error && <div className="alert alert-error">{error}</div>}

          <button className="btn btn-primary" disabled={busy} type="submit">
            {busy ? 'Processing…' : 'Run Multi-Agent Workflow'}
          </button>
        </form>

        <div className="card result-card">
          <h3>Decision</h3>
          {!result && <p className="muted">Submit a claim to see the agent decision here.</p>}
          {result && (
            <div className="result">
              <div className="result-id">{result.claimId}</div>
              <div className="result-badges">
                <DecisionBadge decision={result.finalDecision} />
                <StatusBadge status={result.status} />
              </div>
              <p className="result-reason">{result.recommendationReason}</p>
              <ul className="mini-audit">
                {result.auditLog.map((a, i) => (
                  <li key={i}>
                    <strong>{a.node}</strong>: {a.message}
                  </li>
                ))}
              </ul>
              {result.status === 'PENDING_REVIEW' && (
                <div className="alert alert-review">
                  Routed to the Manual Review queue for a human decision.
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
