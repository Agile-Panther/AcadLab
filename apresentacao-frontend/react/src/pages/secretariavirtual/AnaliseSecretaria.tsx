import { useEffect, useState } from 'react';
import type { SolicitacaoResumo } from '../../types/solicitacao';
import { TIPO_LABELS } from '../../types/solicitacao';
import {
  buscarPendentes,
  buscarPorId,
  iniciarAnalise,
  deferir,
  indeferir,
  solicitarComplementacao,
  concluir,
  vincularEConcluir,
} from '../../api/solicitacaoApi';
import StatusBadge from '../../components/StatusBadge';

const ANALISTA_ID = 1;

export default function AnaliseSecretaria() {
  const [pendentes, setPendentes] = useState<SolicitacaoResumo[]>([]);
  const [selecionada, setSelecionada] = useState<SolicitacaoResumo | null>(null);
  const [justificativa, setJustificativa] = useState('');
  const [impactoAcademico, setImpactoAcademico] = useState(false);
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [processando, setProcessando] = useState(false);

  function carregar() {
    setCarregando(true);
    buscarPendentes()
      .then(setPendentes)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false));
  }

  useEffect(carregar, []);

  async function handleSelecionar(id: number) {
    try {
      const s = await buscarPorId(id);
      setSelecionada(s);
      setJustificativa('');
      setImpactoAcademico(false);
      setErro('');
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao carregar');
    }
  }

  async function executarAcao(acao: () => Promise<void>) {
    setProcessando(true);
    setErro('');
    try {
      await acao();
      setSelecionada(null);
      carregar();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro na operacao');
    } finally {
      setProcessando(false);
    }
  }

  function handleIniciarAnalise() {
    if (!selecionada) return;
    executarAcao(() => iniciarAnalise(selecionada.id, ANALISTA_ID));
  }

  function handleDeferir() {
    if (!selecionada || !justificativa.trim()) {
      setErro('A justificativa e obrigatoria para deferir.');
      return;
    }
    executarAcao(() => deferir(selecionada.id, ANALISTA_ID, justificativa.trim(), impactoAcademico));
  }

  function handleIndeferir() {
    if (!selecionada || !justificativa.trim()) {
      setErro('A justificativa e obrigatoria para indeferir.');
      return;
    }
    executarAcao(() => indeferir(selecionada.id, ANALISTA_ID, justificativa.trim()));
  }

  function handleSolicitarComplementacao() {
    if (!selecionada) return;
    executarAcao(() => solicitarComplementacao(selecionada.id, ANALISTA_ID));
  }

  function handleConcluir() {
    if (!selecionada) return;
    executarAcao(() => concluir(selecionada.id));
  }

  function handleVincularEConcluir() {
    if (!selecionada) return;
    executarAcao(() => vincularEConcluir(selecionada.id));
  }

  if (carregando) return <p className="loading">Carregando...</p>;

  return (
    <div className="page">
      <h2>Painel da Secretaria - Analise de Solicitacoes</h2>

      {erro && <p className="error-msg">{erro}</p>}

      <div className="analise-layout">
        <div className="analise-lista">
          <h3>Solicitacoes Pendentes ({pendentes.length})</h3>
          {pendentes.length === 0 ? (
            <p className="empty-state">Nenhuma solicitacao pendente.</p>
          ) : (
            <ul className="solicitacao-list">
              {pendentes.map((s) => (
                <li
                  key={s.id}
                  className={`solicitacao-item ${selecionada?.id === s.id ? 'selected' : ''}`}
                  onClick={() => handleSelecionar(s.id)}
                >
                  <div className="solicitacao-item-header">
                    <strong>#{s.protocoloId}</strong>
                    <StatusBadge status={s.status} />
                  </div>
                  <div className="solicitacao-item-body">
                    <span>{TIPO_LABELS[s.tipo]}</span>
                    <span className="text-muted">{s.dataAbertura}</span>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="analise-detalhe">
          {selecionada ? (
            <>
              <h3>Solicitacao #{selecionada.protocoloId}</h3>

              <div className="detail-card">
                <div className="detail-row">
                  <span className="detail-label">Tipo:</span>
                  <span>{TIPO_LABELS[selecionada.tipo]}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Status:</span>
                  <StatusBadge status={selecionada.status} />
                </div>
                <div className="detail-row">
                  <span className="detail-label">Estudante ID:</span>
                  <span>{selecionada.estudanteId}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Periodo Letivo:</span>
                  <span>{selecionada.periodoLetivoId}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Descricao:</span>
                  <span>{selecionada.descricao}</span>
                </div>
                <div className="detail-row">
                  <span className="detail-label">Data de Abertura:</span>
                  <span>{selecionada.dataAbertura}</span>
                </div>
              </div>

              {selecionada.status === 'PENDENTE_ANALISE' && (
                <div className="analise-acoes">
                  <button
                    className="btn btn-primary"
                    onClick={handleIniciarAnalise}
                    disabled={processando}
                  >
                    Iniciar Analise
                  </button>
                </div>
              )}

              {selecionada.status === 'EM_ANALISE' && (
                <div className="analise-acoes">
                  <div className="form-group">
                    <label htmlFor="justificativa">Justificativa</label>
                    <textarea
                      id="justificativa"
                      rows={3}
                      value={justificativa}
                      onChange={(e) => setJustificativa(e.target.value)}
                      placeholder="Justificativa para deferir ou indeferir..."
                    />
                  </div>

                  <div className="form-group checkbox-group">
                    <label>
                      <input
                        type="checkbox"
                        checked={impactoAcademico}
                        onChange={(e) => setImpactoAcademico(e.target.checked)}
                      />
                      Possui impacto academico
                    </label>
                  </div>

                  <div className="btn-group">
                    <button className="btn btn-success" onClick={handleDeferir} disabled={processando}>
                      Deferir
                    </button>
                    <button className="btn btn-danger" onClick={handleIndeferir} disabled={processando}>
                      Indeferir
                    </button>
                    <button className="btn btn-secondary" onClick={handleSolicitarComplementacao} disabled={processando}>
                      Solicitar Complementacao
                    </button>
                  </div>
                </div>
              )}

              {selecionada.status === 'DEFERIDA' && (
                <div className="analise-acoes">
                  <div className="btn-group">
                    <button className="btn btn-primary" onClick={handleConcluir} disabled={processando}>
                      Concluir
                    </button>
                    <button className="btn btn-secondary" onClick={handleVincularEConcluir} disabled={processando}>
                      Vincular Alteracoes e Concluir
                    </button>
                  </div>
                </div>
              )}
            </>
          ) : (
            <p className="empty-state">Selecione uma solicitacao para analisar.</p>
          )}
        </div>
      </div>
    </div>
  );
}
