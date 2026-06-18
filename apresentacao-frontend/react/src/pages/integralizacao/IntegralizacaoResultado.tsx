import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { IntegralizacaoResumo } from '../../types/integralizacao';
import { buscarPorId, getEstudanteInfo, gerarProtocolo } from '../../services/integralizacaoService';

export default function IntegralizacaoResultado() {
  const { id } = useParams<{ id: string }>();
  const [integ, setInteg] = useState<IntegralizacaoResumo | null>(null);
  const navigate = useNavigate();
  const integId = Number(id);

  useEffect(() => {
    buscarPorId(integId).then(setInteg);
  }, [integId]);

  if (!integ) return <div className="page-integralizacao"><p>Carregando...</p></div>;

  const est = getEstudanteInfo(integ.estudanteId);
  const protocolo = gerarProtocolo(integ.id);

  return (
    <div className="page-integralizacao">
      <div className="page-header">
        <div>
          <h1>Validação de Integralização Curricular</h1>
          <p className="page-subtitle">F-08 · Secretária</p>
        </div>
        <div className="user-avatar sa">SA</div>
      </div>

      <div className="alert alert-success">
        ✓ Análise registrada com sucesso. Protocolo {protocolo} — Resultado: {integ.status}.
      </div>

      <div className="card">
        <div className="resultado-card-header">
          <h3>Protocolo {protocolo} — Análise Concluída</h3>
          <span className={`status-badge ${integ.status === 'APTO' ? 'badge-success' : 'badge-danger'}`}>
            {integ.status === 'APTO' ? 'Apto' : 'Inapto'}
          </span>
        </div>

        <div className="resultado-details">
          <div className="detail-row">
            <span className="detail-label">Estudante</span>
            <span className="detail-value">{est.nome} — {est.matricula}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Curso</span>
            <span className="detail-value">{est.curso}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Resultado</span>
            <span className={`detail-value ${integ.status === 'APTO' ? 'text-green' : 'text-red'}`}>
              {integ.status === 'APTO' ? 'Apto para Colação de Grau' : 'Inapto'}
            </span>
          </div>
          {integ.dataAprovacao && (
            <div className="detail-row">
              <span className="detail-label">Analisado por</span>
              <span className="detail-value">Secretaria · {integ.dataAprovacao}</span>
            </div>
          )}
          {integ.status === 'APTO' && (
            <div className="detail-row">
              <span className="detail-label">Próximo passo</span>
              <span className="detail-value">Aguardando aprovação do Coordenador (US03)</span>
            </div>
          )}
        </div>

        {integ.itensChecklist.length > 0 && (
          <div className="checklist-verificado">
            <h4 className="text-green">Todos os requisitos verificados</h4>
            {integ.itensChecklist.map((item, idx) => (
              <p key={idx} className={item.cumprido ? 'text-green' : 'text-red'}>
                {formatTipo(item.tipo)}: {item.descricao} {item.cumprido ? '✓' : '✗'}
              </p>
            ))}
          </div>
        )}

        {integ.status === 'APTO' && (
          <div className="rn-info">
            <span className="rn-icon">ℹ</span>
            Próxima etapa: o Coordenador Acadêmico deve aprovar formalmente a aptidão (US03) antes de registrar a colação.
          </div>
        )}

        <button className="btn btn-primary" onClick={() => navigate('/integralizacao')}>
          Ver próxima solicitação
        </button>
      </div>
    </div>
  );
}

function formatTipo(tipo: string): string {
  const map: Record<string, string> = {
    DISCIPLINAS_OBRIGATORIAS: 'Disciplinas obrigatórias',
    CARGA_OPTATIVA: 'Carga optativa',
    HORAS_COMPLEMENTARES: 'Atividades complementares',
    SITUACAO_DISCENTE: 'Pendências acadêmicas',
  };
  return map[tipo] ?? tipo;
}
