import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { IntegralizacaoResumo, ItemChecklistRequest } from '../../types/integralizacao';
import {
  buscarPorId,
  gerarChecklist,
  registrarResultado,
  getEstudanteInfo,
  gerarProtocolo,
} from '../../services/integralizacaoService';

const checklistPadrao: ItemChecklistRequest[] = [
  { tipo: 'DISCIPLINAS_OBRIGATORIAS', descricao: '60 de 60 disciplinas aprovadas', cumprido: true },
  { tipo: 'CARGA_OPTATIVA', descricao: '220h de 240h exigidas', cumprido: false },
  { tipo: 'HORAS_COMPLEMENTARES', descricao: '80h de 120h exigidas', cumprido: false },
  { tipo: 'SITUACAO_DISCENTE', descricao: 'Nenhuma pendência registrada no sistema', cumprido: true },
];

export default function IntegralizacaoAnalise() {
  const { id } = useParams<{ id: string }>();
  const [integ, setInteg] = useState<IntegralizacaoResumo | null>(null);
  const [itens, setItens] = useState<ItemChecklistRequest[]>([]);
  const [resultado, setResultado] = useState<'APTO' | 'INAPTO'>('APTO');
  const [observacao, setObservacao] = useState('');
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState(false);
  const navigate = useNavigate();

  const integId = Number(id);

  useEffect(() => {
    buscarPorId(integId)
      .then(data => {
        setInteg(data);
        if (data.itensChecklist.length > 0) {
          setItens(data.itensChecklist.map(i => ({
            tipo: i.tipo,
            descricao: i.descricao,
            cumprido: i.cumprido,
          })));
          const temPendencia = data.itensChecklist.some(i => !i.cumprido);
          setResultado(temPendencia ? 'INAPTO' : 'APTO');
        } else {
          setItens(checklistPadrao);
          const temPendencia = checklistPadrao.some(i => !i.cumprido);
          setResultado(temPendencia ? 'INAPTO' : 'APTO');
        }
      })
      .catch(() => setErro('Erro ao carregar integralização'));
  }, [integId]);

  const est = integ ? getEstudanteInfo(integ.estudanteId) : null;
  const protocolo = integ ? gerarProtocolo(integ.id) : '';
  const pendencias = itens.filter(i => !i.cumprido);
  const resultadoCalculado = pendencias.length > 0 ? 'INAPTO' : 'APTO';

  async function handleRegistrar() {
    try {
      setErro('');
      await gerarChecklist(integId, itens);
      await registrarResultado(integId, { resultado });
      setSucesso(true);
      setTimeout(() => navigate(`/integralizacao/${integId}/resultado`), 1500);
    } catch (e) {
      setErro(e instanceof Error ? e.message : 'Erro ao registrar resultado');
    }
  }

  if (!integ || !est) return <div className="page-integralizacao"><p>Carregando...</p></div>;

  if (sucesso) {
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
          ✓ Análise registrada com sucesso. Protocolo {protocolo} — Resultado: {resultado}.
        </div>
      </div>
    );
  }

  return (
    <div className="page-integralizacao">
      <div className="page-header">
        <div>
          <h1>Validação de Integralização Curricular</h1>
          <p className="page-subtitle">F-08 · Secretária — Analisando {protocolo}</p>
        </div>
        <div className="user-avatar sa">SA</div>
      </div>

      <div className="card estudante-card-compact">
        <div className="avatar-circle">{est.nome.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
        <div>
          <h3>{est.nome} — {est.matricula}</h3>
          <p>{est.curso} · {est.periodo} · Protocolo {protocolo} · Solicitado em 15/11/2025</p>
        </div>
      </div>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <div className="analise-content">
        <div className="checklist-panel">
          <h3>Checklist de Integralização</h3>
          <p className="detalhe-meta">Matriz Curricular {integ.matrizCurricularId} — Verificação automática dos requisitos</p>

          <div className="checklist-items">
            {itens.map((item, idx) => (
              <div key={idx} className={`checklist-row ${item.cumprido ? 'cumprido' : 'pendente'}`}>
                <span className={`check-icon ${item.cumprido ? 'check-ok' : 'check-fail'}`}>
                  {item.cumprido ? '✓' : '✗'}
                </span>
                <div>
                  <strong className={item.cumprido ? 'text-green' : 'text-red'}>{formatTipoAnalise(item.tipo)}</strong>
                  <p className={item.cumprido ? 'text-muted' : 'text-red'}>{item.descricao}{!item.cumprido ? ` — Falta ${calcFalta(item)}` : ''}</p>
                </div>
              </div>
            ))}
          </div>

          <div className={`resultado-calculado ${resultadoCalculado === 'INAPTO' ? 'resultado-inapto' : 'resultado-apto'}`}>
            <strong>Resultado Calculado: {resultadoCalculado}</strong>
            {pendencias.length > 0 && (
              <p>{pendencias.length} pendência{pendencias.length > 1 ? 's' : ''} identificada{pendencias.length > 1 ? 's' : ''}: {pendencias.map(p => formatTipoShort(p.tipo).toLowerCase()).join(' e ')}.</p>
            )}
          </div>

          <div className="rn-info">
            <span className="rn-icon">ℹ</span>
            RN 3: O checklist considera apenas registros acadêmicos consolidados e atividades complementares deferidas.
          </div>
        </div>

        <div className="resultado-panel">
          <h3>Registrar Resultado da Análise</h3>

          <label className="form-label">Resultado da análise *</label>
          <div className="radio-group">
            <label className={`radio-option ${resultado === 'APTO' ? 'selected' : ''}`}>
              <input
                type="radio"
                name="resultado"
                value="APTO"
                checked={resultado === 'APTO'}
                onChange={() => setResultado('APTO')}
              />
              <span className="radio-dot apto" />
              Apto para Colação
            </label>
            <label className={`radio-option ${resultado === 'INAPTO' ? 'selected' : ''}`}>
              <input
                type="radio"
                name="resultado"
                value="INAPTO"
                checked={resultado === 'INAPTO'}
                onChange={() => setResultado('INAPTO')}
              />
              <span className="radio-dot inapto" />
              Inapto
            </label>
          </div>

          {resultado === 'INAPTO' && pendencias.length > 0 && (
            <div>
              <label className="form-label">Pendências identificadas (obrigatório se Inapto)</label>
              {pendencias.map((p, idx) => (
                <div key={idx} className="pendencia-tag">{formatPendencia(p)}</div>
              ))}
            </div>
          )}

          <label className="form-label">Observação da análise (opcional)</label>
          <textarea
            className="form-textarea"
            value={observacao}
            onChange={e => setObservacao(e.target.value)}
            placeholder="Observações sobre a análise..."
            rows={3}
          />

          <div className="form-actions">
            <button className="btn btn-outline" onClick={() => navigate('/integralizacao')}>Cancelar</button>
            <button
              className={`btn ${resultado === 'INAPTO' ? 'btn-danger' : 'btn-primary'}`}
              onClick={handleRegistrar}
            >
              Registrar {resultado === 'INAPTO' ? 'Inapto' : 'Apto'}
            </button>
          </div>

          <div className="rn-info rn-warning">
            <span className="rn-icon">ℹ</span>
            RN 4: Resultado Inapto requer ao menos uma pendência registrada.
          </div>
        </div>
      </div>
    </div>
  );
}

function formatTipoAnalise(tipo: string): string {
  const map: Record<string, string> = {
    DISCIPLINAS_OBRIGATORIAS: 'Disciplinas obrigatórias concluídas',
    CARGA_OPTATIVA: 'Carga horária optativa mínima',
    HORAS_COMPLEMENTARES: 'Horas de atividades complementares',
    SITUACAO_DISCENTE: 'Pendências acadêmicas impeditivas',
  };
  return map[tipo] ?? tipo;
}

function formatTipoShort(tipo: string): string {
  const map: Record<string, string> = {
    DISCIPLINAS_OBRIGATORIAS: 'disciplinas obrigatórias',
    CARGA_OPTATIVA: 'carga optativa',
    HORAS_COMPLEMENTARES: 'atividades complementares',
    SITUACAO_DISCENTE: 'pendências acadêmicas',
  };
  return map[tipo] ?? tipo;
}

function formatPendencia(item: ItemChecklistRequest): string {
  const map: Record<string, string> = {
    CARGA_OPTATIVA: 'Carga optativa insuficiente: ' + item.descricao,
    HORAS_COMPLEMENTARES: 'Atividades complementares: ' + item.descricao,
    DISCIPLINAS_OBRIGATORIAS: 'Disciplinas obrigatórias: ' + item.descricao,
    SITUACAO_DISCENTE: 'Pendências acadêmicas: ' + item.descricao,
  };
  return map[item.tipo] ?? item.descricao;
}

function calcFalta(item: ItemChecklistRequest): string {
  const match = item.descricao.match(/(\d+)h?\s*de\s*(\d+)/);
  if (match) {
    const atual = parseInt(match[1]);
    const total = parseInt(match[2]);
    return `${total - atual}h`;
  }
  return '';
}
