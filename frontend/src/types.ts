export type Decision = 'APPROVED' | 'REJECTED' | 'MANUAL_REVIEW' | 'PENDING';
export type WorkflowStatus = 'PROCESSING' | 'PENDING_REVIEW' | 'COMPLETED';
export type RiskLevel = 'LOW' | 'MEDIUM' | 'HIGH';

export interface AuditEntry {
  node: string;
  message: string;
  timestamp: string;
}

export interface ClaimState {
  claimId: string;
  customerName: string;
  policyNumber: string;
  claimType: string;
  claimAmount: number;
  incidentDate: string;
  description: string;
  documentsUploaded: string[];
  missingDocuments: string[];
  documentStatus: string;
  validationStatus: string;
  validationReason: string;
  policyStatus: string;
  coverageAmount: number;
  policyEligible: boolean;
  coverageReason: string;
  fraudScore: number;
  fraudIndicators: string[];
  riskScore: number;
  riskLevel: RiskLevel;
  recommendation: Decision;
  recommendationReason: string;
  reviewerDecision: Decision | null;
  reviewerComments: string | null;
  reviewerName: string | null;
  finalDecision: Decision;
  auditLog: AuditEntry[];
  status: WorkflowStatus;
  nodePath: string[];
  currentNode: string;
  submittedAt: string;
  updatedAt: string;
}

export interface ClaimRequest {
  customerName: string;
  policyNumber: string;
  claimType: string;
  claimAmount: number;
  incidentDate: string;
  description: string;
  documentsUploaded: string[];
}

export interface Policy {
  policyNumber: string;
  holderName: string;
  type: string;
  active: boolean;
  premiumPaid: boolean;
  coverageLimit: number;
}

export interface Stats {
  total: number;
  approved: number;
  rejected: number;
  pendingReview: number;
}

export interface GraphNode {
  id: string;
  label: string;
}

export interface GraphEdge {
  from: string;
  to: string;
  label: string;
}

export interface WorkflowGraph {
  nodes: GraphNode[];
  edges: GraphEdge[];
}
