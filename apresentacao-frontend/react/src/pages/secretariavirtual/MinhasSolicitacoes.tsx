import { useEffect, useState } from 'react';
import type { SolicitacaoResumo, DocumentoRequest } from '../../types/solicitacao';
import { TIPO_LABELS, STATUS_LABELS, formatProtocolo, formatPeriodo } from '../../types/solicitacao';
import { buscarPorEstudante, buscarPorId, complementar, cancelar } from '../../api/solicitacaoApi';
import StatusBadge from '../../components/StatusBadge';
import Modal from '../../components/Modal';
import AlertBox from '../../components/AlertBox';
import NovaSolicitacaoModal from './NovaSolicitacao';

const ESTUDANTE_ID = 1;

export default function MinhasSolicitacoes() {
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoResumo[]>([]);
  const [selecionada, setSelecionada] = useState<SolicitacaoResumo | null>(null);
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);
  const [sucesso, setSucesso] = useState('');

  // Nova solicitação modal
  const [mostrarNovaModal, setMostrarNovaModal] = useState(false);

  // Cancelar modal
  const [mostrarCancelarModal, setMostrarCancelarModal] = useState(false);
  const [processandoCancelar, setProcessandoCancelar] = useState(false);

  // Complementar
  const [docNome, setDocNome] = useState('');
  const [enviandoDoc, setEnviandoDoc] = useState(false);
  const [observacaoComplementar, setObservacaoComplementar] = useState('');

  function carregar() {
    setCarregando(true);
    buscarPorEstudante(ESTUDANTE_ID)
      .then(setSolicitacoes)
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

  async function handleConfirmarCancelar() {
    if (!selecionada) return;
    setProcessandoCancelar(true);
    try {
      await cancelar(selecionada.id);
      setSucesso(`Solicitação ${formatProtocolo(selecionada.protocoloId)} cancelada com sucesso.`);
      setSelecionada(null);
      setMostrarCancelarModal(false);
      carregar();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao cancelar');
    } finally {
      setProcessandoCancelar(false);
    }
  }

  async function handleEnviarComplementacao() {
    if (!selecionada || !docNome.trim()) return;
    setEnviandoDoc(true);
    try {
      const doc: DocumentoRequest = { tipo: 'documento_adicional', nomeArquivo: docNome.trim() };
      await complementar(selecionada.id, doc);
      setSucesso(`Complementação enviada para ${formatProtocolo(selecionada.protocoloId)}.`);
      setDocNome('');
      setObservacaoComplementar('');
      const updated = await buscarPorId(selecionada.id);
      setSelecionada(updated);
      carregar();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao complementar');
    } finally {
      setEnviandoDoc(false);
    }
  }

  function handleNovaSolicitacaoCriada() {
    setMostrarNovaModal(false);
    carregar();
    setSucesso('Solicitação enviada com sucesso!');
  }

  // Group by periodo
  const periodos = Array.from(new Set(solicitacoes.map((s) => s.periodoLetivoId)));

  if (carregando) return <p className="loading">Carregando...</p>;

  return (
    <>
      {sucesso && <AlertBox variant="success">{sucesso}</AlertBox>}
      {erro && <div className="error-msg">{erro}</div>}

      <div className="two-panel">
        {/* Left Panel - Minhas Solicitações */}
        <div className="panel-left">
          <div className="panel-left-header">
            <div>
              <h2>Minhas Solicitações</h2>
              <div className="subtitle">Todos os períodos</div>
            </div>
            <button
              className="btn btn-primary btn-sm"
              onClick={() => setMostrarNovaModal(true)}
            >
              + Nova
            </button>
          </div>
          <div className="panel-list">
            {periodos.map((periodo) => {
              const items = solicitacoes.filter((s) => s.periodoLetivoId === periodo);
              return items.map((s) => (
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
                    {formatPeriodo(s.periodoLetivoId)} · {s.dataAbertura}
                  </div>
                </div>
              ));
            })}
            {solicitacoes.length === 0 && (
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
                <StatusBadge status={selecionada.status} />
              </div>
              <div className="panel-right-body">
                {/* Complementação warning */}
                {selecionada.status === 'PENDENTE_COMPLEMENTACAO' && (
                  <AlertBox variant="warning">
                    A secretaria solicitou documentos adicionais para análise desta solicitação.
                  </AlertBox>
                )}

                {/* Detail fields */}
                <div className="detail-grid">
                  <div className="detail-field">
                    <div className="field-label">Tipo</div>
                    <div className="field-value">{TIPO_LABELS[selecionada.tipo]}</div>
                  </div>
                  <div className="detail-field">
                    <div className="field-label">Período</div>
                    <div className="field-value">{formatPeriodo(selecionada.periodoLetivoId)}</div>
                  </div>
                  <div className="detail-field">
                    <div className="field-label">Abertura</div>
                    <div className="field-value">{selecionada.dataAbertura}</div>
                  </div>
                </div>

                <div className="detail-field">
                  <div className="field-label">Justificativa</div>
                  <div className="field-value">{selecionada.descricao}</div>
                </div>

                {selecionada.justificativaAnalise && (
                  <div className="detail-field">
                    <div className="field-label">Parecer da secretaria</div>
                    <div className="field-value">{selecionada.justificativaAnalise}</div>
                  </div>
                )}

                {/* Movimentações (Timeline) */}
                <h3 style={{ marginTop: 24, marginBottom: 16, fontSize: 16 }}>Movimentações</h3>
                <div className="timeline-vertical">
                  <div className="timeline-v-item">
                    <div className="timeline-v-dot dot-blue" />
                    <div className="timeline-v-date">{selecionada.dataAbertura}</div>
                    <div className="timeline-v-text">Solicitação aberta pelo estudante</div>
                  </div>

                  {(selecionada.status === 'EM_ANALISE' ||
                    selecionada.status === 'DEFERIDA' ||
                    selecionada.status === 'INDEFERIDA' ||
                    selecionada.status === 'CONCLUIDA') && (
                    <div className="timeline-v-item">
                      <div className="timeline-v-dot dot-blue" />
                      <div className="timeline-v-date">{selecionada.dataAbertura}</div>
                      <div className="timeline-v-text">Solicitação recebida pela secretaria — Em análise</div>
                    </div>
                  )}

                  {selecionada.status === 'PENDENTE_COMPLEMENTACAO' && (
                    <div className="timeline-v-item">
                      <div className="timeline-v-dot dot-yellow" />
                      <div className="timeline-v-text">Secretaria solicitou complementação</div>
                    </div>
                  )}

                  {(selecionada.status === 'DEFERIDA' || selecionada.status === 'CONCLUIDA') && selecionada.dataAnalise && (
                    <div className="timeline-v-item">
                      <div className="timeline-v-dot dot-green" />
                      <div className="timeline-v-date">{selecionada.dataAnalise}</div>
                      <div className="timeline-v-text">Solicitação deferida</div>
                    </div>
                  )}

                  {selecionada.status === 'INDEFERIDA' && selecionada.dataAnalise && (
                    <div className="timeline-v-item">
                      <div className="timeline-v-dot dot-yellow" />
                      <div className="timeline-v-date">{selecionada.dataAnalise}</div>
                      <div className="timeline-v-text">Solicitação indeferida</div>
                    </div>
                  )}

                  {selecionada.status !== 'CONCLUIDA' && selecionada.status !== 'CANCELADA' && selecionada.status !== 'INDEFERIDA' && (
                    <div className="timeline-v-item">
                      <div className="timeline-v-dot dot-gray" />
                      <div className="timeline-v-waiting">Decisão da secretaria</div>
                    </div>
                  )}
                </div>

                {/* Complementação section */}
                {selecionada.status === 'PENDENTE_COMPLEMENTACAO' && (
                  <div className="mt-24">
                    <h3 style={{ fontSize: 16, marginBottom: 12 }}>Enviar Complementação</h3>

                    {/* Existing docs */}
                    {selecionada.documentos && selecionada.documentos.length > 0 && (
                      <div className="mb-16">
                        <div className="field-label" style={{ marginBottom: 8 }}>Documentos já anexados</div>
                        {selecionada.documentos.map((doc, i) => (
                          <div key={i} className="doc-attached" style={{ marginBottom: 4 }}>
                            <span className="doc-name">
                              <span className="doc-icon">📎</span>
                              {doc.nomeArquivo}
                            </span>
                          </div>
                        ))}
                      </div>
                    )}

                    <div className="form-group">
                      <label className="form-label">Documento adicional</label>
                      <input
                        className="form-input"
                        type="text"
                        placeholder="Nome do arquivo (ex: prova_assinada_RC203.pdf)"
                        value={docNome}
                        onChange={(e) => setDocNome(e.target.value)}
                      />
                    </div>

                    <div className="form-group">
                      <label className="form-label">Observação adicional (opcional)</label>
                      <textarea
                        className="form-textarea"
                        rows={2}
                        value={observacaoComplementar}
                        onChange={(e) => setObservacaoComplementar(e.target.value)}
                        placeholder="Segue em anexo o comprovante solicitado pela secretaria."
                      />
                    </div>

                    <AlertBox variant="info">
                      RN 5: Complementação não é permitida em solicitações com status Concluída ou Indeferida.
                    </AlertBox>

                    <div className="btn-group mt-16">
                      <button className="btn" onClick={() => setSelecionada(null)}>Cancelar</button>
                      <button
                        className="btn btn-primary"
                        onClick={handleEnviarComplementacao}
                        disabled={enviandoDoc || !docNome.trim()}
                      >
                        {enviandoDoc ? 'Enviando...' : 'Enviar Complementação'}
                      </button>
                    </div>
                  </div>
                )}

                {/* RN info notices */}
                {selecionada.status === 'EM_ANALISE' && (
                  <div className="mt-24">
                    <AlertBox variant="info">
                      RN 4: Esta solicitação está em análise. Não é possível realizar complementação neste momento.
                    </AlertBox>
                  </div>
                )}

                {/* Cancel button */}
                {selecionada.status === 'PENDENTE_ANALISE' && (
                  <div className="mt-24">
                    <button
                      className="btn btn-outline-danger"
                      onClick={() => setMostrarCancelarModal(true)}
                    >
                      Cancelar Solicitação
                    </button>
                  </div>
                )}
              </div>
            </>
          ) : (
            <div className="panel-empty">Selecione uma solicitação para ver os detalhes.</div>
          )}
        </div>
      </div>

      {/* Modal: Cancelar Solicitação */}
      {mostrarCancelarModal && selecionada && (
        <Modal
          title="Cancelar Solicitação"
          onClose={() => setMostrarCancelarModal(false)}
          footer={
            <>
              <button className="btn" onClick={() => setMostrarCancelarModal(false)}>
                Manter Solicitação
              </button>
              <button
                className="btn btn-danger"
                onClick={handleConfirmarCancelar}
                disabled={processandoCancelar}
              >
                Confirmar Cancelamento
              </button>
            </>
          }
        >
          <AlertBox variant="danger">
            Esta ação não pode ser desfeita. A solicitação será encerrada permanentemente.
          </AlertBox>

          <table className="summary-table">
            <tbody>
              <tr>
                <td>Protocolo</td>
                <td className="value-link">{formatProtocolo(selecionada.protocoloId)}</td>
              </tr>
              <tr>
                <td>Tipo</td>
                <td>{TIPO_LABELS[selecionada.tipo]}</td>
              </tr>
              <tr>
                <td>Status atual</td>
                <td className="value-status">{STATUS_LABELS[selecionada.status]} — Aguardando secretaria</td>
              </tr>
            </tbody>
          </table>

          <p style={{ fontWeight: 500, marginBottom: 12 }}>
            Tem certeza que deseja cancelar esta solicitação?
          </p>

          <AlertBox variant="info">
            RN 6: Cancelamento permitido apenas para solicitações com status Pendente de Análise.
          </AlertBox>
        </Modal>
      )}

      {/* Modal: Nova Solicitação */}
      {mostrarNovaModal && (
        <NovaSolicitacaoModal
          onClose={() => setMostrarNovaModal(false)}
          onSuccess={handleNovaSolicitacaoCriada}
        />
      )}
    </>
  );
}
