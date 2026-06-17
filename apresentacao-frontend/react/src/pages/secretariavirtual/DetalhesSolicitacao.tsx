import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import type { SolicitacaoResumo, DocumentoRequest } from '../../types/solicitacao';
import { TIPO_LABELS } from '../../types/solicitacao';
import { buscarPorId, complementar, cancelar } from '../../api/solicitacaoApi';
import StatusBadge from '../../components/StatusBadge';

export default function DetalhesSolicitacao() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [solicitacao, setSolicitacao] = useState<SolicitacaoResumo | null>(null);
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);

  const [docTipo, setDocTipo] = useState('');
  const [docNome, setDocNome] = useState('');
  const [enviandoDoc, setEnviandoDoc] = useState(false);

  function carregar() {
    if (!id) return;
    setCarregando(true);
    buscarPorId(Number(id))
      .then(setSolicitacao)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false));
  }

  useEffect(carregar, [id]);

  async function handleComplementar() {
    if (!docTipo.trim() || !docNome.trim()) return;
    setEnviandoDoc(true);
    try {
      const doc: DocumentoRequest = { tipo: docTipo.trim(), nomeArquivo: docNome.trim() };
      await complementar(Number(id), doc);
      setDocTipo('');
      setDocNome('');
      carregar();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao complementar');
    } finally {
      setEnviandoDoc(false);
    }
  }

  async function handleCancelar() {
    if (!confirm('Deseja realmente cancelar esta solicitacao?')) return;
    try {
      await cancelar(Number(id));
      navigate('/secretaria-virtual');
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao cancelar');
    }
  }

  if (carregando) return <p className="loading">Carregando...</p>;
  if (!solicitacao) return <p className="error-msg">Solicitacao nao encontrada.</p>;

  const podeComplementar = solicitacao.status === 'PENDENTE_COMPLEMENTACAO' || solicitacao.status === 'PENDENTE_ANALISE';
  const podeCancelar = solicitacao.status === 'PENDENTE_ANALISE';

  return (
    <div className="page">
      <h2>Detalhes da Solicitacao #{solicitacao.protocoloId}</h2>

      {erro && <p className="error-msg">{erro}</p>}

      <div className="detail-card">
        <div className="detail-row">
          <span className="detail-label">Protocolo:</span>
          <span>#{solicitacao.protocoloId}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Tipo:</span>
          <span>{TIPO_LABELS[solicitacao.tipo]}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Status:</span>
          <StatusBadge status={solicitacao.status} />
        </div>
        <div className="detail-row">
          <span className="detail-label">Data de Abertura:</span>
          <span>{solicitacao.dataAbertura}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Periodo Letivo:</span>
          <span>{solicitacao.periodoLetivoId}</span>
        </div>
        <div className="detail-row">
          <span className="detail-label">Descricao:</span>
          <span>{solicitacao.descricao}</span>
        </div>

        {solicitacao.justificativaAnalise && (
          <div className="detail-row">
            <span className="detail-label">Justificativa da Analise:</span>
            <span>{solicitacao.justificativaAnalise}</span>
          </div>
        )}

        {solicitacao.dataAnalise && (
          <div className="detail-row">
            <span className="detail-label">Data da Analise:</span>
            <span>{solicitacao.dataAnalise}</span>
          </div>
        )}
      </div>

      {podeComplementar && (
        <div className="section">
          <h3>Complementar Solicitacao</h3>
          <p className="section-desc">Anexe documentos adicionais para complementar esta solicitacao.</p>
          <div className="doc-input-row">
            <input
              type="text"
              placeholder="Tipo do documento"
              value={docTipo}
              onChange={(e) => setDocTipo(e.target.value)}
            />
            <input
              type="text"
              placeholder="Nome do arquivo"
              value={docNome}
              onChange={(e) => setDocNome(e.target.value)}
            />
            <button
              className="btn btn-primary btn-sm"
              onClick={handleComplementar}
              disabled={enviandoDoc}
            >
              {enviandoDoc ? 'Enviando...' : 'Enviar'}
            </button>
          </div>
        </div>
      )}

      <div className="form-actions">
        {podeCancelar && (
          <button className="btn btn-danger" onClick={handleCancelar}>
            Cancelar Solicitacao
          </button>
        )}
        <button className="btn" onClick={() => navigate('/secretaria-virtual')}>
          Voltar
        </button>
      </div>
    </div>
  );
}
