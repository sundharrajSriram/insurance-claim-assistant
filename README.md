# Multi-Agent Insurance Claim Processing System (Java)

An **Intelligent Insurance Claim Processing System** built for the Agentic AI /
BFSI case study. The original case study specifies a **LangGraph + Streamlit**
Python solution; this project delivers the same multi-agent design as a modern
**Java application**:

- **Backend** — Spring Boot 3 (Java 17) with a lightweight, dependency-free
  `StateGraph` engine that ports LangGraph's concepts (nodes, edges, conditional
  edges, shared state, `END`) to Java. Each specialised agent is a graph node.
- **Frontend** — a React 18 + TypeScript + Vite single-page app ("ClaimFlow")
  providing claim submission, a dashboard, a manual-review queue and history —
  connected to the backend over a REST API.

---

## Architecture

```
START: Submit Claim (React UI)
   │
   ▼
claim_validation → policy_verification → document_verification
   → fraud_detection → risk_assessment → recommendation
                                              │
        ┌─────────────── conditional router ─┴───────────────┐
        │ approve / reject                     manual review  │
        ▼                                                     ▼
     finalize ◄───────────── reviewed ─────────────── human_review (HITL)
        │                                                     │
        ▼                                              (pause until a
      END: Final Decision + Audit Trail               reviewer decides)
```

### Agents (graph nodes)

| Node | Role | Key checks | Output |
|------|------|-----------|--------|
| `claim_validation` | Validate the request | Mandatory fields, date, amount, type | `validationStatus`, `validationReason` |
| `policy_verification` | Policy eligibility | Active, premium paid, coverage limit | `policyEligible`, `coverageAmount`, `coverageReason` |
| `document_verification` | Document completeness | Required docs per claim type | `missingDocuments`, `documentStatus` |
| `fraud_detection` | Suspicious patterns | High amount, keywords, missing docs | `fraudScore`, `fraudIndicators` |
| `risk_assessment` | Overall risk | Fraud, policy, amount, type | `riskScore`, `riskLevel` |
| `recommendation` | AI recommendation | All prior outputs | `recommendation`, `recommendationReason` |
| `human_review` | Human-in-the-loop | Reviewer decision for risky/incomplete claims | `reviewerDecision`, `reviewerComments` |

The **conditional router** after `recommendation` routes each claim to
`APPROVED`, `REJECTED`, or `MANUAL_REVIEW`. Manual-review claims **pause** the
graph and appear in the review queue; recording a reviewer decision resumes the
graph to `finalize`.

The shared `ClaimState` (the Java equivalent of the LangGraph `TypedDict` state)
carries the claim input, every node's output, the final decision, and a
node-level `auditLog` for full explainability.

---

## Prerequisites

- **Java 17+**
- **Node.js 18+** and npm

(Maven is not required — the backend ships with the Maven Wrapper `./mvnw`.)

## Running the app

Open two terminals.

### 1. Backend (port 8080)

```bash
cd insurance-claim-assistant/backend
./mvnw spring-boot:run
```

### 2. Frontend (port 5173)

```bash
cd insurance-claim-assistant/frontend
npm install
npm run dev
```

Then open **http://localhost:5173**. The Vite dev server proxies `/api` calls to
the backend on port 8080, so no extra configuration is needed.

## Demo instructions

1. Go to **Submit Claim** and click **Load approved / rejected / manual review
   sample**, or fill the form manually.
2. Click **Run Multi-Agent Workflow** — the decision, reasoning and a mini audit
   trail appear instantly.
3. The **Dashboard** shows claim counts and the workflow graph.
4. Claims routed to **MANUAL_REVIEW** appear in **Manual Review**; approve or
   reject them with reviewer comments (the human-in-the-loop step).
5. **History** lists every claim; click a row to open the detail drawer with
   node outputs, fraud indicators, risk scores, the executed workflow path, and
   the full audit trail.

### Sample policy numbers

| Policy | Status | Use for |
|--------|--------|---------|
| `POL-1001` | Active Health, ₹5,00,000 | Approved |
| `POL-1003` | Active Property, ₹20,00,000 | Manual review (high value) |
| `POL-1005` | **Lapsed** Health | Rejected |
| `POL-1006` | **Premium unpaid** Motor | Rejected |

## REST API

| Method | Endpoint | Purpose |
|--------|----------|---------|
| `POST` | `/api/claims` | Submit a claim (runs the workflow) |
| `GET`  | `/api/claims` | List all claims |
| `GET`  | `/api/claims/{id}` | Claim detail |
| `GET`  | `/api/claims/stats` | Dashboard counts |
| `GET`  | `/api/claims/review/pending` | Manual-review queue |
| `POST` | `/api/claims/{id}/review` | Record a reviewer decision |
| `GET`  | `/api/policies` | Sample policies |
| `GET`  | `/api/samples` | Sample claims (approved/rejected/manual) |
| `GET`  | `/api/workflow/graph` | Workflow graph definition |

## Tests

```bash
cd insurance-claim-assistant/backend
./mvnw test
```

The tests cover all three decision paths (approved, rejected, manual review →
reviewer decision) and invalid-claim rejection.

## Project layout

```
insurance-claim-assistant/
├── backend/                     # Spring Boot API + StateGraph workflow
│   └── src/main/java/com/insuranceclaim/
│       ├── graph/               # StateGraph engine + node wiring
│       │   └── nodes/           # the seven agent nodes + finalize
│       ├── model/               # ClaimState (shared state), DTOs, enums
│       ├── data/                # sample policies + sample claims
│       ├── service/             # orchestration + in-memory store
│       └── controller/          # REST endpoints
└── frontend/                    # React + TypeScript + Vite UI
    └── src/components/          # dashboard, submit, review, history, detail
```
