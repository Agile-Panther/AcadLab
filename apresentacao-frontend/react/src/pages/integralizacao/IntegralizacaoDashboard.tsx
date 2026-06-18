import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { IntegralizacaoResumo } from '../../types/integralizacao';
import { buscarTodas, getEstudanteInfo, gerarProtocolo } from '../../services/integralizacaoService';

function StatusBadge({ status }: { status: string }) {
  const map: Record<string, { label: string; cls: string }> = {
    EM_ANALISE: { label: 'Em Análise', cls: 'badge-warning' },
    APTO: { label: 'Apto', cls: 'badge-success' },
    INAPTO: { label: 'Inapto', cls: 'badge-danger' },
  };
  const info = map[status] ?? { label: status, cls: 'badge-default' };
  return <span className={`status-badge ${info.cls}`}>{info.label}</span>;
}

function AguardandoBadge({ integralizacao }: { integralizacao: IntegralizacaoResumo }) {
  if (integralizacao.status === 'EM_ANALISE' && integralizacao.itensChecklist.length === 0) {
    return <span className="status-badge badge-info">Aguardando Análise</span>;
  }
  return <StatusBadge status={integralizacao.status} />;
}

export default function IntegralizacaoDashboard() {
  const [integralizacoes, setIntegralizacoes] = useState<IntegralizacaoResumo[]>([]);
  const [selecionada, setSelecionada] = useState<IntegralizacaoResumo | null>(null);
  const [busca, setBusca] = useState('');
  const [erro, setErro] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    buscarTodas()
      .then(setIntegralizacoes)
      .catch(() => setErro('Erro ao carregar solicitações'));
  }, []);

  const aguardando = integralizacoes.filter(i => i.status === 'EM_ANALISE' && i.itensChecklist.length === 0).length;
  const emAnalise = integralizacoes.filter(i => i.status === 'EM_ANALISE' && i.itensChecklist.length > 0).length;
  const aptos = integralizacoes.filter(i => i.status === 'APTO').length;
  const colacoes = integralizacoes.filter(i => i.aprovadorId !== null).length;

  const filtradas = integralizacoes.filter(i => {
    if (!busca) return true;
    const est = getEstudanteInfo(i.estudanteId);
    const protocolo = gerarProtocolo(i.id);
    const texto = `${est.nome} ${est.matricula} ${protocolo}`.toLowerCase();
    return texto.includes(busca.toLowerCase());
  });

  const sel = selecionada;
  const estSel = sel ? getEstudanteInfo(sel.estudanteId) : null;

  return (
    <div className="page-integralizacao">
      <div className="page-header">
        <div>
          <h1>Validação de Integralização Curricular</h1>
          <p className="page-subtitle">F-08 · Colação de Grau</p>
        </div>
        <div className="user-avatar sa">SA</div>
      </div>

      <div className="stats-row">
        <div className="stat-card">
          <span className="stat-number stat-blue">{aguardando}</span>
          <span className="stat-label">Aguardando Análise</span>
        </div>
        <div className="stat-card">
          <span className="stat-number stat-orange">{emAnalise}</span>
          <span className="stat-label">Em Análise</span>
        </div>
        <div className="stat-card">
          <span className="stat-number stat-green">{aptos}</span>
          <span className="stat-label">Aptos</span>
        </div>
        <div className="stat-card">
          <span className="stat-number stat-purple">{colacoes}</span>
          <span className="stat-label">Colações Registradas</span>
        </div>
      </div>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <div className="dashboard-content">
        <div className="solicitacoes-panel">
          <input
            type="text"
            className="search-input"
            placeholder="Buscar estudante ou protocolo..."
            value={busca}
            onChange={e => setBusca(e.target.value)}
          />

          <h3 className="panel-title">Solicitações de Conclusão</h3>

          <div className="solicitacoes-list">
            {filtradas.map(integ => {
              const est = getEstudanteInfo(integ.estudanteId);
              return (
                <div
                  key={integ.id}
                  className={`solicitacao-item${sel?.id === integ.id ? ' selected' : ''}`}
                  onClick={() => setSelecionada(integ)}
                >
                  <div className="solicitacao-info">
                    <span className="solicitacao-protocolo">{gerarProtocolo(integ.id)}</span>
                    <span className="solicitacao-nome">{est.nome}</span>
                    <span className="solicitacao-detalhe">{est.matricula} · {est.curso}</span>
                  </div>
                  <AguardandoBadge integralizacao={integ} />
                </div>
              );
            })}
            {filtradas.length === 0 && (
              <p className="empty-message">Nenhuma solicitação encontrada.</p>
            )}
          </div>
        </div>

        {sel && estSel && (
          <div className="detalhe-panel">
            <div className="detalhe-header">
              <div className="avatar-circle">{estSel.nome.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
              <div className="detalhe-info">
                <h3>{estSel.nome}</h3>
                <p>{estSel.matricula} · {estSel.curso} · {estSel.periodo}</p>
              </div>
              <AguardandoBadge integralizacao={sel} />
            </div>

            <div className="detalhe-body">
              <h4>Progresso de Integralização</h4>
              <p className="detalhe-meta">Base: Matriz Curricular {sel.matrizCurricularId}</p>

              {sel.itensChecklist.length > 0 ? (
                <div className="checklist-progress">
                  {sel.itensChecklist.map((item, idx) => (
                    <div key={idx} className="progress-item">
                      <span className="progress-label">{formatTipo(item.tipo)}</span>
                      <div className={`progress-bar-wrapper`}>
                        <div
                          className={`progress-bar ${item.cumprido ? 'progress-green' : 'progress-orange'}`}
                          style={{ width: item.cumprido ? '100%' : '70%' }}
                        />
                      </div>
                      <span className="progress-text">{item.descricao}</span>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="empty-message">Checklist ainda não gerado.</p>
              )}

              <div className="detalhe-actions">
                <button className="btn btn-primary" onClick={() => navigate(`/integralizacao/${sel.id}/analise`)}>
                  Iniciar Análise
                </button>
                <button className="btn btn-outline" onClick={() => navigate(`/integralizacao/estudante/${sel.estudanteId}`)}>
                  Ver Histórico
                </button>
              </div>
            </div>

            <div className="rn-info">
              <span className="rn-icon">ℹ</span>
              RN 1: Solicitação permitida somente após encerramento do último período letivo cursado.
            </div>
            <div className="rn-info rn-warning">
              <span className="rn-icon">ℹ</span>
              RN 2: Carga optativa e horas complementares insuficientes — análise indicará Inapto.
            </div>
          </div>
        )}
      </div>
    </div>
  );
}

function formatTipo(tipo: string): string {
  const map: Record<string, string> = {
    DISCIPLINAS_OBRIGATORIAS: 'Disciplinas Obrigatórias',
    CARGA_OPTATIVA: 'Carga Optativa',
    HORAS_COMPLEMENTARES: 'Atividades Complementares',
    SITUACAO_DISCENTE: 'Situação Discente',
  };
  return map[tipo] ?? tipo;
}
