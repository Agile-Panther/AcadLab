import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import type { TipoSolicitacao, DocumentoRequest } from '../../types/solicitacao';
import { TIPO_LABELS, DOCUMENTOS_OBRIGATORIOS } from '../../types/solicitacao';
import { abrirSolicitacao } from '../../api/solicitacaoApi';

const ESTUDANTE_ID = 1;
const TIPOS = Object.keys(TIPO_LABELS) as TipoSolicitacao[];

export default function NovaSolicitacao() {
  const navigate = useNavigate();
  const [tipo, setTipo] = useState<TipoSolicitacao>('TRANCAMENTO_DISCIPLINA');
  const [periodoLetivoId, setPeriodoLetivoId] = useState(1);
  const [descricao, setDescricao] = useState('');
  const [documentos, setDocumentos] = useState<DocumentoRequest[]>([]);
  const [novoDocTipo, setNovoDocTipo] = useState('');
  const [novoDocNome, setNovoDocNome] = useState('');
  const [erro, setErro] = useState('');
  const [enviando, setEnviando] = useState(false);

  const obrigatorios = DOCUMENTOS_OBRIGATORIOS[tipo];
  const tiposAnexados = documentos.map((d) => d.tipo);
  const faltantes = obrigatorios.filter((o) => !tiposAnexados.includes(o));

  function adicionarDocumento() {
    if (!novoDocTipo.trim() || !novoDocNome.trim()) return;
    setDocumentos([...documentos, { tipo: novoDocTipo.trim(), nomeArquivo: novoDocNome.trim() }]);
    setNovoDocTipo('');
    setNovoDocNome('');
  }

  function removerDocumento(index: number) {
    setDocumentos(documentos.filter((_, i) => i !== index));
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro('');

    if (!descricao.trim()) {
      setErro('A descricao e obrigatoria.');
      return;
    }

    if (faltantes.length > 0) {
      setErro(`Documentos obrigatorios faltando: ${faltantes.join(', ')}`);
      return;
    }

    setEnviando(true);
    try {
      await abrirSolicitacao({
        estudanteId: ESTUDANTE_ID,
        periodoLetivoId,
        tipo,
        descricao: descricao.trim(),
        documentos,
      });
      navigate('/secretaria-virtual');
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao abrir solicitacao');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <div className="page">
      <h2>Nova Solicitacao Academica</h2>

      {erro && <p className="error-msg">{erro}</p>}

      <form onSubmit={handleSubmit} className="form">
        <div className="form-group">
          <label htmlFor="tipo">Tipo de Solicitacao</label>
          <select
            id="tipo"
            value={tipo}
            onChange={(e) => setTipo(e.target.value as TipoSolicitacao)}
          >
            {TIPOS.map((t) => (
              <option key={t} value={t}>{TIPO_LABELS[t]}</option>
            ))}
          </select>
        </div>

        <div className="form-group">
          <label htmlFor="periodo">Periodo Letivo (ID)</label>
          <input
            id="periodo"
            type="number"
            min={1}
            value={periodoLetivoId}
            onChange={(e) => setPeriodoLetivoId(Number(e.target.value))}
          />
        </div>

        <div className="form-group">
          <label htmlFor="descricao">Descricao</label>
          <textarea
            id="descricao"
            rows={4}
            value={descricao}
            onChange={(e) => setDescricao(e.target.value)}
            placeholder="Descreva o motivo da sua solicitacao..."
          />
        </div>

        {obrigatorios.length > 0 && (
          <div className="info-box">
            <strong>Documentos obrigatorios para este tipo:</strong>
            <ul>
              {obrigatorios.map((d) => (
                <li key={d} className={tiposAnexados.includes(d) ? 'doc-ok' : 'doc-faltando'}>
                  {d} {tiposAnexados.includes(d) ? '(anexado)' : '(faltando)'}
                </li>
              ))}
            </ul>
          </div>
        )}

        <fieldset className="form-group">
          <legend>Anexar Documentos</legend>
          <div className="doc-input-row">
            <input
              type="text"
              placeholder="Tipo do documento"
              value={novoDocTipo}
              onChange={(e) => setNovoDocTipo(e.target.value)}
            />
            <input
              type="text"
              placeholder="Nome do arquivo"
              value={novoDocNome}
              onChange={(e) => setNovoDocNome(e.target.value)}
            />
            <button type="button" className="btn btn-sm" onClick={adicionarDocumento}>
              Adicionar
            </button>
          </div>

          {documentos.length > 0 && (
            <ul className="doc-list">
              {documentos.map((d, i) => (
                <li key={i}>
                  <span>{d.tipo} - {d.nomeArquivo}</span>
                  <button type="button" className="btn btn-sm btn-danger" onClick={() => removerDocumento(i)}>
                    Remover
                  </button>
                </li>
              ))}
            </ul>
          )}
        </fieldset>

        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={enviando}>
            {enviando ? 'Enviando...' : 'Abrir Solicitacao'}
          </button>
          <button type="button" className="btn" onClick={() => navigate('/secretaria-virtual')}>
            Voltar
          </button>
        </div>
      </form>
    </div>
  );
}
