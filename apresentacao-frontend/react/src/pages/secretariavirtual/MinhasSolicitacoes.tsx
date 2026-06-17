import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import type { SolicitacaoResumo } from '../../types/solicitacao';
import { TIPO_LABELS } from '../../types/solicitacao';
import { buscarPorEstudante, cancelar } from '../../api/solicitacaoApi';
import StatusBadge from '../../components/StatusBadge';

const ESTUDANTE_ID = 1;

export default function MinhasSolicitacoes() {
  const [solicitacoes, setSolicitacoes] = useState<SolicitacaoResumo[]>([]);
  const [erro, setErro] = useState('');
  const [carregando, setCarregando] = useState(true);

  function carregar() {
    setCarregando(true);
    buscarPorEstudante(ESTUDANTE_ID)
      .then(setSolicitacoes)
      .catch((e) => setErro(e.message))
      .finally(() => setCarregando(false));
  }

  useEffect(carregar, []);

  async function handleCancelar(id: number) {
    if (!confirm('Deseja realmente cancelar esta solicitacao?')) return;
    try {
      await cancelar(id);
      carregar();
    } catch (e: unknown) {
      setErro(e instanceof Error ? e.message : 'Erro ao cancelar');
    }
  }

  if (carregando) return <p className="loading">Carregando...</p>;

  return (
    <div className="page">
      <div className="page-header">
        <h2>Minhas Solicitacoes</h2>
        <Link to="/secretaria-virtual/nova" className="btn btn-primary">
          Nova Solicitacao
        </Link>
      </div>

      {erro && <p className="error-msg">{erro}</p>}

      {solicitacoes.length === 0 ? (
        <p className="empty-state">Nenhuma solicitacao encontrada.</p>
      ) : (
        <table className="data-table">
          <thead>
            <tr>
              <th>Protocolo</th>
              <th>Tipo</th>
              <th>Status</th>
              <th>Data de Abertura</th>
              <th>Acoes</th>
            </tr>
          </thead>
          <tbody>
            {solicitacoes.map((s) => (
              <tr key={s.id}>
                <td>#{s.protocoloId}</td>
                <td>{TIPO_LABELS[s.tipo]}</td>
                <td><StatusBadge status={s.status} /></td>
                <td>{s.dataAbertura}</td>
                <td className="actions">
                  <Link to={`/secretaria-virtual/${s.id}`} className="btn btn-sm">
                    Detalhes
                  </Link>
                  {s.status === 'PENDENTE_ANALISE' && (
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={() => handleCancelar(s.id)}
                    >
                      Cancelar
                    </button>
                  )}
                  {(s.status === 'PENDENTE_COMPLEMENTACAO' || s.status === 'PENDENTE_ANALISE') && (
                    <Link
                      to={`/secretaria-virtual/${s.id}/complementar`}
                      className="btn btn-sm btn-secondary"
                    >
                      Complementar
                    </Link>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
