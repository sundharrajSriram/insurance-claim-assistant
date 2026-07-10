import type { Decision, RiskLevel, WorkflowStatus } from '../types';

export function DecisionBadge({ decision }: { decision: Decision }) {
  const map: Record<Decision, string> = {
    APPROVED: 'badge badge-approved',
    REJECTED: 'badge badge-rejected',
    MANUAL_REVIEW: 'badge badge-review',
    PENDING: 'badge badge-pending',
  };
  const label = decision.replace('_', ' ');
  return <span className={map[decision]}>{label}</span>;
}

export function StatusBadge({ status }: { status: WorkflowStatus }) {
  const map: Record<WorkflowStatus, string> = {
    PROCESSING: 'badge badge-pending',
    PENDING_REVIEW: 'badge badge-review',
    COMPLETED: 'badge badge-neutral',
  };
  return <span className={map[status]}>{status.replace('_', ' ')}</span>;
}

export function RiskBadge({ level }: { level: RiskLevel }) {
  const map: Record<RiskLevel, string> = {
    LOW: 'badge badge-approved',
    MEDIUM: 'badge badge-review',
    HIGH: 'badge badge-rejected',
  };
  return <span className={map[level]}>{level}</span>;
}

export function ScoreBar({ value, danger }: { value: number; danger?: boolean }) {
  const color = danger
    ? value >= 60
      ? '#e5484d'
      : value >= 30
        ? '#f5a524'
        : '#30a46c'
    : '#4f7cff';
  return (
    <div className="scorebar">
      <div className="scorebar-fill" style={{ width: `${value}%`, background: color }} />
      <span className="scorebar-label">{value}</span>
    </div>
  );
}

export function formatMoney(n: number): string {
  return new Intl.NumberFormat('en-IN', {
    style: 'currency',
    currency: 'INR',
    maximumFractionDigits: 0,
  }).format(n);
}

export function formatTime(iso: string): string {
  try {
    return new Date(iso).toLocaleString();
  } catch {
    return iso;
  }
}
