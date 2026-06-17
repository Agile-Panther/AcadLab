import type { StatusSolicitacao } from '../types/solicitacao';
import { STATUS_LABELS } from '../types/solicitacao';

const STATUS_COLORS: Record<StatusSolicitacao, string> = {
  PENDENTE_ANALISE: '#f59e0b',
  EM_ANALISE: '#3b82f6',
  PENDENTE_COMPLEMENTACAO: '#f97316',
  DEFERIDA: '#10b981',
  INDEFERIDA: '#ef4444',
  CONCLUIDA: '#6b7280',
  CANCELADA: '#9ca3af',
};

export default function StatusBadge({ status }: { status: StatusSolicitacao }) {
  return (
    <span
      className="status-badge"
      style={{ backgroundColor: STATUS_COLORS[status] }}
    >
      {STATUS_LABELS[status]}
    </span>
  );
}
