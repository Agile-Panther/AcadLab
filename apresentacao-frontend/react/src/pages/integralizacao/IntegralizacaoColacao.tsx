import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import type { IntegralizacaoResumo } from '../../types/integralizacao';
import { buscarPorId, registrarColacao, getEstudanteInfo, gerarProtocolo } from '../../services/integralizacaoService';

export default function IntegralizacaoColacao() {
  const { id } = useParams<{ id: string }>();
  const [integ, setInteg] = useState<IntegralizacaoResumo | null>(null);
  const [dataCerimonia, setDataCerimonia] = useState('');
  const [horario, setHorario] = useState('');
  const [local, setLocal] = useState('');
  const [modalidade, setModalidade] = useState('Pública');
  const [observacoes, setObservacoes] = useState('');
  const [erro, setErro] = useState('');
  const [sucesso, setSucesso] = useState(false);
  const navigate = useNavigate();
  const integId = Number(id);

  useEffect(() => {
    buscarPorId(integId).then(setInteg);
  }, [integId]);

  if (!integ) return <div className="page-integralizacao"><p>Carregando...</p></div>;

  const est = getEstudanteInfo(integ.estudanteId);
  const protocolo = gerarProtocolo(integ.id);

  async function handleRegistrar() {
    if (!dataCerimonia || !local) {
      setErro('Data e local da cerimônia são obrigatórios.');
      return;
    }
    try {
      setErro('');
      await registrarColacao(integId, {
        dataCerimonia,
        horario: horario || null,
        local,
        modalidade,
        observacoes: observacoes || null,
      });
      setSucesso(true);
    } catch (e) {
      setErro(e instanceof Error ? e.message : 'Erro ao registrar colação');
    }
  }

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
          ✓ Colação de grau registrada com sucesso para {est.nome}. Protocolo {protocolo}.
        </div>
        <div className="card">
          <h3>Colação registrada</h3>
          <div className="resultado-details">
            <div className="detail-row">
              <span className="detail-label">Estudante</span>
              <span className="detail-value">{est.nome} — {est.matricula}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Data da cerimônia</span>
              <span className="detail-value">{dataCerimonia}</span>
            </div>
            <div className="detail-row">
              <span className="detail-label">Local</span>
              <span className="detail-value">{local}</span>
            </div>
          </div>
          <button className="btn btn-primary" onClick={() => navigate('/integralizacao')}>
            Voltar ao painel
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="page-integralizacao">
      <div className="page-header">
        <div>
          <h1>Validação de Integralização Curricular</h1>
          <p className="page-subtitle">F-08 · Secretária — Registrar Colação</p>
        </div>
        <div className="user-avatar sa">SA</div>
      </div>

      <div className="alert alert-info alert-coordenador">
        🎓 Aptidão aprovada pelo Coordenador Acadêmico{integ.dataAprovacao ? ` em ${integ.dataAprovacao}` : ''} — {est.nome} apto para colação de grau.
      </div>

      <div className="card estudante-card-compact">
        <div className="avatar-circle purple">{est.nome.split(' ').map(n => n[0]).join('').slice(0, 2)}</div>
        <div>
          <h3>{est.nome} — {est.matricula}</h3>
          <p>{est.curso} · {protocolo} · Aptidão aprovada{integ.dataAprovacao ? ` em ${integ.dataAprovacao}` : ''}</p>
        </div>
      </div>

      {erro && <div className="alert alert-danger">{erro}</div>}

      <div className="card">
        <h3>Dados da Cerimônia de Colação</h3>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Data da Cerimônia *</label>
            <input
              type="date"
              className="form-input"
              value={dataCerimonia}
              onChange={e => setDataCerimonia(e.target.value)}
            />
          </div>
          <div className="form-group form-group-small">
            <label className="form-label">Horário *</label>
            <input
              type="time"
              className="form-input"
              value={horario}
              onChange={e => setHorario(e.target.value)}
            />
          </div>
        </div>

        <div className="form-row">
          <div className="form-group">
            <label className="form-label">Local da Cerimônia *</label>
            <input
              type="text"
              className="form-input"
              value={local}
              onChange={e => setLocal(e.target.value)}
              placeholder="Ex.: Auditório Principal — Campus I"
            />
          </div>
          <div className="form-group form-group-small">
            <label className="form-label">Modalidade</label>
            <select className="form-input" value={modalidade} onChange={e => setModalidade(e.target.value)}>
              <option>Pública</option>
              <option>Privada</option>
            </select>
          </div>
        </div>

        <div className="form-group">
          <label className="form-label">Observações</label>
          <textarea
            className="form-textarea"
            value={observacoes}
            onChange={e => setObservacoes(e.target.value)}
            placeholder="Informações adicionais sobre a cerimônia..."
            rows={3}
          />
        </div>

        <div className="rn-info">
          <span className="rn-icon">ℹ</span>
          RN 7: Registro da colação restrito a estudante com aptidão formalmente aprovada pelo Coordenador.
        </div>
        <div className="rn-info">
          <span className="rn-icon">ℹ</span>
          RN 8: Data da cerimônia deve ser igual ou posterior à data de aprovação da aptidão{integ.dataAprovacao ? ` (${integ.dataAprovacao})` : ''}.
        </div>

        <div className="form-actions">
          <button className="btn btn-outline" onClick={() => navigate('/integralizacao')}>Cancelar</button>
          <button className="btn btn-danger" onClick={handleRegistrar}>
            Registrar Colação de Grau
          </button>
        </div>
      </div>
    </div>
  );
}
