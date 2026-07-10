import type {
  ClaimRequest,
  ClaimState,
  Decision,
  Policy,
  Stats,
  WorkflowGraph,
} from './types';

const BASE = '/api';

async function handle<T>(res: Response): Promise<T> {
  if (!res.ok) {
    let message = `Request failed (${res.status})`;
    try {
      const body = await res.json();
      if (body && body.message) message = body.message;
    } catch {
      /* ignore */
    }
    throw new Error(message);
  }
  return res.json() as Promise<T>;
}

export const api = {
  submitClaim: (payload: ClaimRequest) =>
    fetch(`${BASE}/claims`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(payload),
    }).then((r) => handle<ClaimState>(r)),

  listClaims: () => fetch(`${BASE}/claims`).then((r) => handle<ClaimState[]>(r)),

  getClaim: (id: string) => fetch(`${BASE}/claims/${id}`).then((r) => handle<ClaimState>(r)),

  stats: () => fetch(`${BASE}/claims/stats`).then((r) => handle<Stats>(r)),

  pendingReview: () =>
    fetch(`${BASE}/claims/review/pending`).then((r) => handle<ClaimState[]>(r)),

  review: (id: string, decision: Decision, reviewerName: string, comments: string) =>
    fetch(`${BASE}/claims/${id}/review`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ decision, reviewerName, comments }),
    }).then((r) => handle<ClaimState>(r)),

  policies: () => fetch(`${BASE}/policies`).then((r) => handle<Policy[]>(r)),

  claimTypes: () => fetch(`${BASE}/claim-types`).then((r) => handle<string[]>(r)),

  samples: () => fetch(`${BASE}/samples`).then((r) => handle<Record<string, ClaimRequest>>(r)),

  graph: () => fetch(`${BASE}/workflow/graph`).then((r) => handle<WorkflowGraph>(r)),
};
