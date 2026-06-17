import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { IntegralizacaoResumo } from '../../types/integralizacao';
import { buscarPorEstudante, getEstudanteInfo, iniciarAnalise } from '../../services/integralizacaoService';

export default function IntegralizacaoEstudante() {
  const { estudanteId } = useParams<{ estudanteId: string }>();
  const [integralizacoes, setIntegralizacoes] = useState<IntegralizacaoResumo[]>([]);
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);
  const navigate = useNavigate();

  const id = Number(estudanteId);
  const est = getEstudanteInfo(id);

  useEffect(() => {
    buscarPorEstudante(id)
      .then(setIntegralizacoes)
      .catch(() => setErro('Erro ao carregar dados'))
      .finally(() => setCarregando(false));
  }, [id]);

  const integ = integralizacoes[0];

  async function handleSolicitar() {
    try {
      const novoId = await iniciarAnalise({ estudanteId: id, matrizCurricularId: 1 });
      navigate(`/integralizacao/${novoId}/analise`);
    } catch (e) {
      setErro(e instanceof Error ? e.message : 'Erro ao solicitar análise');
    }
  }

  if (carregando) return <div className="page-integralizacao"><p>Carregando...</p></div>;

  return (
    <div className="page-integralizacao">
      <div className="page-header">
        <div>
          <h1>Validação de Integralização Curricular</h1>
          <p className="page-subtitle">F-08 · Estudante: {est.nome} — {est.matricula}</p>
        </div>
        <div className="user-avatar cl">{est.nome.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
      </div>

      <div className="card">
        <div className="estudante-card-header">
          <div className="avatar-circle">{est.nome.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
          <div>
            <h3>{est.nome}</h3>
            <p>{est.matricula} · {est.curso} · {est.periodo}</p>
          </div>
          <span className="status-badge badge-info">Situação: Ativo</span>
        </div>
      </div>

      {erro && <div className="alert alert-danger">{erro}</div>}

      {integ ? (
        <div className="card">
          <h3>Sua Integralização Curricular</h3>
          <p className="detalhe-meta">Matriz {integ.matrizCurricularId} — Para solicitar análise, verifique os requisitos abaixo</p>

          <div className="checklist-items">
            {integ.itensChecklist.length > 0 ? (
              integ.itensChecklist.map((item, idx) => (
                <div key={idx} className={`checklist-row ${item.cumprido ? 'cumprido' : 'pendente'}`}>
                  <span className={`check-icon ${item.cumprido ? 'check-ok' : 'check-fail'}`}>
                    {item.cumprido ? '✓' : '✗'}
                  </span>
                  <div>
                    <strong>{formatTipo(item.tipo)}</strong>
                    <p className={item.cumprido ? 'text-green' : 'text-red'}>{item.descricao}</p>
                  </div>
                </div>
              ))
            ) : (
              <>
                <div className="checklist-row cumprido">
                  <span className="check-icon check-ok">✓</span>
                  <div><strong>Disciplinas obrigatórias concluídas</strong><p className="text-green">Verificação pendente</p></div>
                </div>
                <div className="checklist-row cumprido">
                  <span className="check-icon check-ok">✓</span>
                  <div><strong>Sem pendências acadêmicas impeditivas</strong><p className="text-green">Nenhuma pendência</p></div>
                </div>
                <div className="checklist-row cumprido">
                  <span className="check-icon check-ok">✓</span>
                  <div><strong>Último período letivo encerrado</strong><p className="text-green">Verificação pendente</p></div>
                </div>
              </>
            )}
          </div>

          {integ.itensChecklist.some(i => !i.cumprido) && (
            <div className="alert alert-warning">
              Você possui requisitos pendentes. A análise indicará Inapto até que sejam cumpridos. Você ainda pode solicitar a análise formal.
            </div>
          )}

          <button className="btn btn-primary btn-lg" onClick={handleSolicitar}>
            Solicitar Análise de Conclusão
          </button>

          <div className="rn-info">
            <span className="rn-icon">ℹ</span>
            RN 2: Pendências abertas no sistema impedem o início da análise formal.
          </div>
        </div>
      ) : (
        <div className="card">
          <h3>Nenhuma solicitação de integralização encontrada</h3>
          <p>Solicite a análise de conclusão para iniciar o processo.</p>
          <button className="btn btn-primary btn-lg" onClick={handleSolicitar}>
            Solicitar Análise de Conclusão
          </button>
        </div>
      )}
    </div>
  );
}

function formatTipo(tipo: string): string {
  const map: Record<string, string> = {
    DISCIPLINAS_OBRIGATORIAS: 'Disciplinas obrigatórias concluídas',
    CARGA_OPTATIVA: 'Carga horária optativa mínima',
    HORAS_COMPLEMENTARES: 'Horas de atividades complementares',
    SITUACAO_DISCENTE: 'Sem pendências acadêmicas impeditivas',
  };
  return map[tipo] ?? tipo;
}
