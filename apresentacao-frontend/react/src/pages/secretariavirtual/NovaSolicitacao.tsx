import { useState } from 'react';
import type { TipoSolicitacao, DocumentoRequest } from '../../types/solicitacao';
import { TIPO_LABELS, DOCUMENTOS_OBRIGATORIOS } from '../../types/solicitacao';
import { abrirSolicitacao } from '../../api/solicitacaoApi';
import Modal from '../../components/Modal';
import AlertBox from '../../components/AlertBox';

const ESTUDANTE_ID = 1;
const TIPOS = Object.keys(TIPO_LABELS) as TipoSolicitacao[];
const MAX_JUSTIFICATIVA = 500;

const PERIODOS = [
  { id: 1, label: '2025.1' },
  { id: 2, label: '2025.2' },
];

interface Props {
  onClose: () => void;
  onSuccess: () => void;
}

export default function NovaSolicitacaoModal({ onClose, onSuccess }: Props) {
  const [tipo, setTipo] = useState<TipoSolicitacao>('REVISAO_DE_NOTA');
  const [periodoLetivoId, setPeriodoLetivoId] = useState(2);
  const [justificativa, setJustificativa] = useState('');
  const [documentos, setDocumentos] = useState<DocumentoRequest[]>([]);
  const [erro, setErro] = useState('');
  const [enviando, setEnviando] = useState(false);

  const obrigatorios = DOCUMENTOS_OBRIGATORIOS[tipo];
  const tiposAnexados = documentos.map((d) => d.tipo);
  const faltantes = obrigatorios.filter((o) => !tiposAnexados.includes(o));

  function adicionarDocumento() {
    const input = document.createElement('input');
    input.type = 'text';
    const nome = prompt('Nome do arquivo (ex: comprovante_prova2.pdf):');
    if (!nome?.trim()) return;

    const tipoDoc = faltantes.length > 0 ? faltantes[0] : 'documento_adicional';
    setDocumentos([...documentos, { tipo: tipoDoc, nomeArquivo: nome.trim() }]);
  }

  function removerDocumento(index: number) {
    setDocumentos(documentos.filter((_, i) => i !== index));
  }

  async function handleSubmit() {
    setErro('');

    if (!justificativa.trim()) {
      setErro('A justificativa é obrigatória.');
      return;
    }

    if (faltantes.length > 0) {
      setErro(`Documentos obrigatórios faltando: ${faltantes.join(', ')}`);
      return;
    }

    setEnviando(true);
    try {
      await abrirSolicitacao({
        estudanteId: ESTUDANTE_ID,
        periodoLetivoId,
        tipo,
        descricao: justificativa.trim(),
        documentos,
      });
      onSuccess();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao abrir solicitação');
    } finally {
      setEnviando(false);
    }
  }

  return (
    <Modal
      title="Nova Solicitação Acadêmica"
      onClose={onClose}
      footer={
        <>
          <button className="btn" onClick={onClose}>Cancelar</button>
          <button
            className="btn btn-primary"
            onClick={handleSubmit}
            disabled={enviando}
          >
            {enviando ? 'Enviando...' : 'Enviar Solicitação'}
          </button>
        </>
      }
    >
      {erro && <div className="error-msg">{erro}</div>}

      <div className="form-group">
        <label className="form-label">
          Tipo de Solicitação <span className="required">*</span>
        </label>
        <select
          className="form-select"
          value={tipo}
          onChange={(e) => {
            setTipo(e.target.value as TipoSolicitacao);
            setDocumentos([]);
          }}
        >
          {TIPOS.map((t) => (
            <option key={t} value={t}>{TIPO_LABELS[t]}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label className="form-label">
          Período Letivo <span className="required">*</span>
        </label>
        <select
          className="form-select"
          value={periodoLetivoId}
          onChange={(e) => setPeriodoLetivoId(Number(e.target.value))}
        >
          {PERIODOS.map((p) => (
            <option key={p.id} value={p.id}>{p.label}</option>
          ))}
        </select>
      </div>

      <div className="form-group">
        <label className="form-label">
          Justificativa <span className="required">*</span>
        </label>
        <textarea
          className="form-textarea"
          rows={4}
          value={justificativa}
          onChange={(e) => {
            if (e.target.value.length <= MAX_JUSTIFICATIVA) {
              setJustificativa(e.target.value);
            }
          }}
          placeholder="Descreva o motivo da sua solicitação..."
        />
        <div className="form-hint">
          {justificativa.length} / {MAX_JUSTIFICATIVA}
        </div>
      </div>

      <div className="form-group">
        <label className="form-label">Documentos obrigatórios</label>

        {documentos.map((doc, i) => (
          <div key={i} className="doc-attached">
            <span className="doc-name">
              <span className="doc-icon">📎</span>
              {doc.nomeArquivo}
            </span>
            <button className="doc-remove" onClick={() => removerDocumento(i)}>
              Remover
            </button>
          </div>
        ))}

        <button
          type="button"
          className="doc-add"
          onClick={adicionarDocumento}
          style={{ marginTop: documentos.length > 0 ? 8 : 0 }}
        >
          + Clique para anexar outro documento (máx. 5 MB)
        </button>
      </div>

      <AlertBox variant="info">
        RN 2: Não é possível abrir mais de uma solicitação do mesmo tipo por período letivo.
      </AlertBox>
    </Modal>
  );
}
