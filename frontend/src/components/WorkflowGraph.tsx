import { useEffect, useState } from 'react';
import { api } from '../api';
import type { WorkflowGraph } from '../types';

/**
 * Renders the LangGraph-style workflow as a vertical flow of nodes with the
 * conditional branches to human review and final decision. If a `path` is
 * provided, executed nodes are highlighted.
 */
export default function WorkflowGraphView({ path }: { path?: string[] }) {
  const [graph, setGraph] = useState<WorkflowGraph | null>(null);

  useEffect(() => {
    api.graph().then(setGraph).catch(() => setGraph(null));
  }, []);

  if (!graph) return null;
  const active = new Set(path ?? []);

  return (
    <div className="graph">
      {graph.nodes.map((n, i) => (
        <div key={n.id} className="graph-row">
          <div className={`graph-node ${active.has(n.id) ? 'graph-node-active' : ''}`}>
            <span className="graph-node-index">{i + 1}</span>
            {n.label}
          </div>
          {i < graph.nodes.length - 1 && <div className="graph-arrow">↓</div>}
        </div>
      ))}
    </div>
  );
}
