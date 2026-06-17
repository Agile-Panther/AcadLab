import { useEffect, useState } from 'react';
import type { SolicitacaoResumo, Estatisticas } from '../../types/solicitacao';
import { TIPO_LABELS, formatProtocolo } from '../../types/solicitacao';
import {
  buscarTodas,
  buscarEstatisticas,
  buscarPorId,
  iniciarAnalise,
  deferir,
  indeferir,
  solicitarComplementacao,
  concluir,
  vincularEConcluir,
} from '../../api/solicitacaoApi';
import StatusBadge from '../../components/StatusBadge';
import Modal from '../../components/Modal';
import AlertBox from '../../components/AlertBox';

const ANALISTA_ID = 1;

export default function AnaliseSecretaria() {
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoResumo[]>([]);
  const [stats, setStats] = useState<Estatisticas | null>(null);
  const [selecionada, setSelecionada] = useState<SolicitacaoResumo | null>(null);
  const [busca, setBusca] = useState('');
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [processando, setProcessando] = useState(false);
  const [sucesso, setSucesso] = useState('');

  // Deferir modal
  const [mostrarDeferirModal, setMostrarDeferirModal] = useState(false);
  const [observacaoDeferir, setObservacaoDeferir] = useState('');
  const [impactoAcademico, setImpactoAcademico] = useState(false);

  // Indeferir modal
  const [mostrarIndeferirModal, setMostrarIndeferirModal] = useState(false);
  const [justificativaIndeferir, setJustificativaIndeferir] = useState('');

  function carregar() {
    setCarregando(true);
    Promise.all([buscarTodas(), buscarEstatisticas()])
      .then(([s, e]) => {
        setSolicitacoes(s);
        setStats(e);
      })
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false));
  }

  useEffect(carregar, []);

  async function handleSelecionar(id: number) {
    try {
      const s = await buscarPorId(id);
      setSelecionada(s);
      setErro('');
      setSucesso('');
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao carregar');
    }
  }

  async function executarAcao(acao: () => Promise<void>, msgSucesso: string) {
    setProcessando(true);
    setErro('');
    try {
      await acao();
      setSucesso(msgSucesso);
      setSelecionada(null);
      carregar();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro na operação');
    } finally {
      setProcessando(false);
    }
  }

  function handleIniciarAnalise() {
    if (!selecionada) return;
    executarAcao(
      () => iniciarAnalise(selecionada.id, ANALISTA_ID),
      `Análise iniciada para ${formatProtocolo(selecionada.protocoloId)}.`
    );
  }

  function handleConfirmarDeferir() {
    if (!selecionada) return;
    executarAcao(
      () => deferir(selecionada.id, ANALISTA_ID, observacaoDeferir.trim(), impactoAcademico),
      `Solicitação ${formatProtocolo(selecionada.protocoloId)} deferida. Estudante notificado automaticamente.`
    );
    setMostrarDeferirModal(false);
    setObservacaoDeferir('');
    setImpactoAcademico(false);
  }

  function handleConfirmarIndeferir() {
    if (!selecionada || !justificativaIndeferir.trim()) {
      setErro('A justificativa é obrigatória para indeferir.');
      return;
    }
    executarAcao(
      () => indeferir(selecionada.id, ANALISTA_ID, justificativaIndeferir.trim()),
      `Solicitação ${formatProtocolo(selecionada.protocoloId)} indeferida.`
    );
    setMostrarIndeferirModal(false);
    setJustificativaIndeferir('');
  }

  function handleSolicitarComplementacao() {
    if (!selecionada) return;
    executarAcao(
      () => solicitarComplementacao(selecionada.id, ANALISTA_ID),
      `Complementação solicitada para ${formatProtocolo(selecionada.protocoloId)}.`
    );
  }

  function handleConcluir() {
    if (!selecionada) return;
    executarAcao(
      () => concluir(selecionada.id),
      `Solicitação ${formatProtocolo(selecionada.protocoloId)} concluída.`
    );
  }

  function handleVincularEConcluir() {
    if (!selecionada) return;
    executarAcao(
      () => vincularEConcluir(selecionada.id),
      `Alterações vinculadas e solicitação ${formatProtocolo(selecionada.protocoloId)} concluída.`
    );
  }

  const filtradas = solicitacoes.filter((s) => {
    if (!busca.trim()) return true;
    const termo = busca.toLowerCase();
    return (
      formatProtocolo(s.protocoloId).toLowerCase().includes(termo) ||
      TIPO_LABELS[s.tipo].toLowerCase().includes(termo) ||
      s.status.toLowerCase().includes(termo)
    );
  });

  if (carregando) return <p className="loading">Carregando...</p>;

  return (
    <>
      {/* Stats Cards */}
      {stats && (
        <div className="stats-row">
          <div className="stat-card stat-blue">
            <div className="stat-value">{stats.PENDENTE_ANALISE}</div>
            <div className="stat-label">Abertas</div>
          </div>
          <div className="stat-card stat-blue">
            <div className="stat-value">{stats.EM_ANALISE}</div>
            <div className="stat-label">Em Análise</div>
          </div>
          <div className="stat-card stat-green">
            <div className="stat-value">{stats.DEFERIDA}</div>
            <div className="stat-label">Deferidas</div>
          </div>
          <div className="stat-card stat-red">
            <div className="stat-value">{stats.INDEFERIDA}</div>
            <div className="stat-label">Indeferidas</div>
          </div>
        </div>
      )}

      {sucesso && <AlertBox variant="success">{sucesso}</AlertBox>}
      {erro && <div className="error-msg">{erro}</div>}

      {/* Two Panel Layout */}
      <div className="two-panel">
        {/* Left Panel - List */}
        <div className="panel-left">
          <div className="panel-left-header">
            <h2>Solicitações Acadêmicas</h2>
            <button className="btn btn-primary btn-sm" onClick={() => {}}>
              + Nova Solicitação
            </button>
          </div>
          <div className="panel-search">
            <input
              type="text"
              placeholder="Buscar protocolo ou tipo..."
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
            />
          </div>
          <div className="panel-list">
            {filtradas.map((s) => (
              <div
                key={s.id}
                className={`panel-list-item ${selecionada?.id === s.id ? 'selected' : ''}`}
                onClick={() => handleSelecionar(s.id)}
              >
                <div className="item-protocol">{formatProtocolo(s.protocoloId)}</div>
                <div className="item-header">
                  <span className="item-title">{TIPO_LABELS[s.tipo]}</span>
                  <StatusBadge status={s.status} />
                </div>
                <div className="item-meta">
                  Estudante #{s.estudanteId} · {s.dataAbertura}
                </div>
              </div>
            ))}
            {filtradas.length === 0 && (
              <div className="panel-empty">Nenhuma solicitação encontrada.</div>
            )}
          </div>
        </div>

        {/* Right Panel - Detail */}
        <div className="panel-right">
          {selecionada ? (
            <>
              <div className="panel-right-header">
                <h2>Protocolo {formatProtocolo(selecionada.protocoloId)}</h2>
                {selecionada.status === 'PENDENTE_ANALISE' && (
                  <button
                    className="btn btn-primary btn-sm"
                    onClick={handleIniciarAnalise}
                    disabled={processando}
                  >
                    Analisar Solicitação
                  </button>
                )}
              </div>
              <div className="panel-right-body">
                <div className="detail-field">
                  <div className="field-label">Tipo</div>
                  <div className="field-value">{TIPO_LABELS[selecionada.tipo]}</div>
                </div>

                <div className="detail-field">
                  <div className="field-label">Estudante</div>
                  <div className="field-value">Estudante #{selecionada.estudanteId}</div>
                </div>

                <div className="detail-field">
                  <div className="field-label">Abertura</div>
                  <div className="field-value">{selecionada.dataAbertura}</div>
                </div>

                <div className="detail-field">
                  <div className="field-label">Justificativa</div>
                  <div className="field-value">{selecionada.descricao}</div>
                </div>

                <div style={{ marginBottom: 16 }}>
                  <StatusBadge status={selecionada.status} />
                </div>

                {/* Documents */}
                {selecionada.documentos && selecionada.documentos.length > 0 && (
                  <div className="detail-field">
                    <div className="field-label">Documentos</div>
                    {selecionada.documentos.map((doc, i) => (
                      <div key={i} className="doc-attached" style={{ marginTop: i > 0 ? 8 : 4 }}>
                        <span className="doc-name">
                          <span className="doc-icon">📎</span>
                          {doc.nomeArquivo}
                        </span>
                      </div>
                    ))}
                  </div>
                )}

                {/* Actions for EM_ANALISE */}
                {selecionada.status === 'EM_ANALISE' && (
                  <div className="btn-group mt-24">
                    <button
                      className="btn btn-success"
                      onClick={() => setMostrarDeferirModal(true)}
                      disabled={processando}
                    >
                      Deferir
                    </button>
                    <button
                      className="btn"
                      onClick={() => setMostrarIndeferirModal(true)}
                      disabled={processando}
                    >
                      Indeferir
                    </button>
                  </div>
                )}

                {/* Actions for DEFERIDA */}
                {selecionada.status === 'DEFERIDA' && (
                  <>
                    {selecionada.possuiImpactoAcademico && !selecionada.alteracoesVinculadas && (
                      <div className="linked-change">
                        ✓ Pendente confirmação do professor
                      </div>
                    )}
                    {selecionada.alteracoesVinculadas && (
                      <div className="linked-change">
                        ✓ Alterações vinculadas ao protocolo
                      </div>
                    )}
                    <div className="btn-group mt-16">
                      {selecionada.possuiImpactoAcademico && !selecionada.alteracoesVinculadas ? (
                        <button
                          className="btn btn-primary"
                          onClick={handleVincularEConcluir}
                          disabled={processando}
                        >
                          Vincular Alterações e Concluir
                        </button>
                      ) : (
                        <button
                          className="btn btn-primary"
                          onClick={handleConcluir}
                          disabled={processando}
                        >
                          Concluir
                        </button>
                      )}
                    </div>
                  </>
                )}

                {/* RN 4 info */}
                {(selecionada.status === 'EM_ANALISE' || selecionada.status === 'DEFERIDA') && (
                  <div className="mt-24">
                    <AlertBox variant="warning">
                      RN 4: Solicitação com impacto só é concluída após todas as alterações vinculadas ao protocolo serem efetivadas.
                    </AlertBox>
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="panel-empty">Selecione uma solicitação para analisar.</div>
          )}
        </div>
      </div>

      {/* Modal: Registrar Deferimento */}
      {mostrarDeferirModal && selecionada && (
        <Modal
          title="Registrar Deferimento"
          onClose={() => setMostrarDeferirModal(false)}
          footer={
            <>
              <button className="btn" onClick={() => setMostrarDeferirModal(false)}>
                Cancelar
              </button>
              <button
                className="btn btn-success"
                onClick={handleConfirmarDeferir}
                disabled={processando}
              >
                Confirmar Deferimento
              </button>
            </>
          }
        >
          <div className="protocol-summary">
            <div className="ps-protocol">
              {formatProtocolo(selecionada.protocoloId)} · {TIPO_LABELS[selecionada.tipo]}
            </div>
            <div className="ps-detail">Estudante #{selecionada.estudanteId}</div>
          </div>

          <div className="form-group">
            <label className="form-label">Observação do deferimento (opcional)</label>
            <textarea
              className="form-textarea"
              rows={3}
              value={observacaoDeferir}
              onChange={(e) => setObservacaoDeferir(e.target.value)}
              placeholder="A solicitação foi analisada e o lançamento será revisado pelo professor."
            />
          </div>

          <div className="form-group">
            <label className="form-checkbox">
              <input
                type="checkbox"
                checked={impactoAcademico}
                onChange={(e) => setImpactoAcademico(e.target.checked)}
              />
              Possui impacto acadêmico (alterações vinculadas)
            </label>
          </div>

          {impactoAcademico && (
            <AlertBox variant="info">
              Alterações vinculadas ao protocolo serão registradas. A solicitação só será concluída após confirmação do professor responsável.
            </AlertBox>
          )}

          <AlertBox variant="warning">
            RN 4: Solicitação deferida com impacto só é concluída após vinculação das alterações ao protocolo.
          </AlertBox>
        </Modal>
      )}

      {/* Modal: Indeferir */}
      {mostrarIndeferirModal && selecionada && (
        <Modal
          title="Registrar Indeferimento"
          onClose={() => setMostrarIndeferirModal(false)}
          footer={
            <>
              <button className="btn" onClick={() => setMostrarIndeferirModal(false)}>
                Cancelar
              </button>
              <button
                className="btn btn-danger"
                onClick={handleConfirmarIndeferir}
                disabled={processando || !justificativaIndeferir.trim()}
              >
                Confirmar Indeferimento
              </button>
            </>
          }
        >
          <div className="protocol-summary">
            <div className="ps-protocol">
              {formatProtocolo(selecionada.protocoloId)} · {TIPO_LABELS[selecionada.tipo]}
            </div>
            <div className="ps-detail">Estudante #{selecionada.estudanteId}</div>
          </div>

          <div className="form-group">
            <label className="form-label">
              Justificativa do indeferimento <span className="required">*</span>
            </label>
            <textarea
              className="form-textarea"
              rows={3}
              value={justificativaIndeferir}
              onChange={(e) => setJustificativaIndeferir(e.target.value)}
              placeholder="Descreva o motivo do indeferimento..."
            />
          </div>
        </Modal>
      )}
    </>
  );
}
