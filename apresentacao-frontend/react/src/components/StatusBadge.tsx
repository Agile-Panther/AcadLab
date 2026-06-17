import type { StatusSolicitacao } from '../types/solicitacao';
import { STATUS_LABELS } from '../types/solicitacao';

const STATUS_CLASS: Record<StatusSolicitacao, string> = {
  PENDENTE_ANALISE: 'badge-pendente',
  EM_ANALISE: 'badge-em-analise',
  PENDENTE_COMPLEMENTACAO: 'badge-pendente',
  DEFERIDA: 'badge-deferido',
  INDEFERIDA: 'badge-indeferido',
  CONCLUIDA: 'badge-concluida',
  CANCELADA: 'badge-cancelada',
};

export default function StatusBadge({ status }: { status: StatusSolicitacao }) {
  return (
    <span className={`status-badge ${STATUS_CLASS[status]}`}>
      {STATUS_LABELS[status]}
    </span>
  );
}
