import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { IntegralizacaoResumo } from '../../types/integralizacao';
import { buscarPorId, aprovarAptidao, getEstudanteInfo, gerarProtocolo } from '../../services/integralizacaoService';

export default function IntegralizacaoAprovacao() {
  const { id } = useParams<{ id: string }>();
  const [integ, setInteg] = useState<IntegralizacaoResumo | null>(null);
  const [observacao, setObservacao] = useState('');
  const [dataAprovacao, setDataAprovacao] = useState(new Date().toISOString().split('T')[0]);
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState(false);
  const navigate = useNavigate();
  const integId = Number(id);

  useEffect(() => {
    buscarPorId(integId).then(setInteg);
  }, [integId]);

  if (!integ) return <div className="page-integralizacao"><p>Carregando...</p></div>;

  const est = getEstudanteInfo(integ.estudanteId);
  const protocolo = gerarProtocolo(integ.id);

  async function handleAprovar() {
    try {
      setErro('');
      await aprovarAptidao(integId, { coordenadorId: 1 });
      setSucesso(true);
    } catch (e) {
      setErro(e instanceof Error ? e.message : 'Erro ao aprovar aptidão');
    }
  }

  if (sucesso) {
    return (
      <div className="page-integralizacao">
        <div className="page-header">
          <div>
            <h1>Validação de Integralização Curricular</h1>
            <p className="page-subtitle">F-08 · Coordenador</p>
          </div>
          <div className="user-avatar ca">CA</div>
        </div>
        <div className="alert alert-success">
          ✓ Aptidão aprovada com sucesso. {est.nome} está apto para colação de grau.
        </div>
        <div className="card">
          <p>A secretaria já pode registrar a cerimônia de colação de grau.</p>
          <div className="form-actions">
            <button className="btn btn-primary" onClick={() => navigate(`/integralizacao/${integId}/colacao`)}>
              Registrar Colação
            </button>
            <button className="btn btn-outline" onClick={() => navigate('/integralizacao')}>
              Voltar ao painel
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="page-integralizacao">
      <div className="page-header">
        <div>
          <h1>Validação de Integralização Curricular</h1>
          <p className="page-subtitle">F-08 · Coordenador — Aprovação de Aptidão</p>
        </div>
        <div className="user-avatar ca">CA</div>
      </div>

      <div className="alert alert-info alert-coordenador">
        🎓 Aprovação exclusiva do Coordenador Acadêmico — RN 5: Somente o Coordenador pode aprovar aptidão para colação de grau.
      </div>

      <div className="card estudante-card-compact">
        <div className="avatar-circle purple">{est.nome.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
        <div>
          <h3>{est.nome} — {est.matricula}</h3>
          <p>{est.curso} · {est.periodo} · Protocolo {protocolo}</p>
        </div>
        <span className="status-badge badge-success">Apto</span>
      </div>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <div className="analise-content">
        <div className="checklist-panel">
          <h3>Verificação de Requisitos (RN 6)</h3>
          <p className="detalhe-meta">Todos os critérios são obrigatórios para aprovação da aptidão</p>

          <div className="checklist-items">
            {integ.itensChecklist.map((item, idx) => (
              <div key={idx} className={`checklist-row ${item.cumprido ? 'cumprido' : 'pendente'}`}>
                <span className={`check-icon ${item.cumprido ? 'check-ok' : 'check-fail'}`}>
                  {item.cumprido ? '✓' : '✗'}
                </span>
                <div>
                  <strong className={item.cumprido ? 'text-green' : 'text-red'}>{formatTipo(item.tipo)}</strong>
                  <p className="text-muted">{item.descricao}</p>
                </div>
              </div>
            ))}
            <div className="checklist-row cumprido">
              <span className="check-icon check-ok">✓</span>
              <div>
                <strong className="text-green">Análise da secretaria concluída</strong>
                <p className="text-muted">Resultado: Apto{integ.dataAprovacao ? ` — ${integ.dataAprovacao}` : ''}</p>
              </div>
            </div>
          </div>

          <div className="rn-info">
            <span className="rn-icon">ℹ</span>
            RN 6: Aptidão exige 100% das obrigatórias + carga optativa mínima + horas complementares.
          </div>
        </div>

        <div className="resultado-panel">
          <h3>Registro de Aprovação</h3>

          <label className="form-label">Observação da coordenação (opcional)</label>
          <textarea
            className="form-textarea"
            value={observacao}
            onChange={e => setObservacao(e.target.value)}
            placeholder="Observações sobre a aprovação..."
            rows={4}
          />

          <label className="form-label">Data da aprovação</label>
          <input
            type="date"
            className="form-input"
            value={dataAprovacao}
            onChange={e => setDataAprovacao(e.target.value)}
          />

          <div className="form-actions">
            <button className="btn btn-outline" onClick={() => navigate('/integralizacao')}>Cancelar</button>
            <button
              className="btn btn-purple"
              onClick={handleAprovar}
              disabled={integ.status !== 'APTO'}
            >
              Aprovar Aptidão
            </button>
          </div>

          <div className="rn-info">
            <span className="rn-icon">ℹ</span>
            RN 5: Somente o Coordenador Acadêmico pode aprovar a aptidão para colação de grau.
          </div>
        </div>
      </div>
    </div>
  );
}

function formatTipo(tipo: string): string {
  const map: Record<string, string> = {
    DISCIPLINAS_OBRIGATORIAS: '100% das disciplinas obrigatórias',
    CARGA_OPTATIVA: 'Carga horária optativa mínima',
    HORAS_COMPLEMENTARES: 'Horas de atividades complementares',
    SITUACAO_DISCENTE: 'Sem pendências acadêmicas',
  };
  return map[tipo] ?? tipo;
}
