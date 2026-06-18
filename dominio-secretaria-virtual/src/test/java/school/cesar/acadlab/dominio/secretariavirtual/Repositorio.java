package school.cesar.acadlab.dominio.secretariavirtual;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.ProtocoloId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.StatusSolicitacao;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

public class Repositorio implements SolicitacaoAcademicaRepositorio {

    private int proximoIdSeq = 1;
    private int proximoProtocoloIdSeq = 1;
    private final Map<SolicitacaoAcademicaId, SolicitacaoAcademica> solicitacoes = new HashMap<>();

    @Override
    public SolicitacaoAcademicaId proximoId() { return new SolicitacaoAcademicaId(proximoIdSeq++); }

    @Override
    public ProtocoloId proximoProtocoloId() { return new ProtocoloId(proximoProtocoloIdSeq++); }

    @Override
    public void salvar(SolicitacaoAcademica solicitacao) {
        notNull(solicitacao, "A solicitação não pode ser nula");
        solicitacoes.put(solicitacao.getId(), solicitacao);
    }

    @Override
    public SolicitacaoAcademica obter(SolicitacaoAcademicaId id) {
        notNull(id, "O id da solicitação não pode ser nulo");
        return Optional.ofNullable(solicitacoes.get(id)).orElseThrow(
                () -> new IllegalArgumentException("Solicitação não encontrada: " + id));
    }

    @Override
    public List<SolicitacaoAcademica> pesquisarPorEstudante(EstudanteId estudanteId) {
        var resultado = new ArrayList<SolicitacaoAcademica>();
        for (var solicitacao : solicitacoes.values()) {
            if (estudanteId.equals(solicitacao.getEstudanteId())) {
                resultado.add(solicitacao);
            }
        }
        return resultado;
    }

    @Override
    public Optional<SolicitacaoAcademica> pesquisarAbertaPorEstudanteTipoPeriodo(
            EstudanteId estudanteId, TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId) {
        return solicitacoes.values().stream()
                .filter(s -> s.getEstudanteId().equals(estudanteId)
                        && s.getTipo() == tipo
                        && s.getPeriodoLetivoId().equals(periodoLetivoId)
                        && s.getStatus() != StatusSolicitacao.CONCLUIDA
                        && s.getStatus() != StatusSolicitacao.CANCELADA
                        && s.getStatus() != StatusSolicitacao.INDEFERIDA)
                .findFirst();
    }

    @Override
    public List<SolicitacaoAcademica> pesquisarPorStatus(StatusSolicitacao status) {
        var resultado = new ArrayList<SolicitacaoAcademica>();
        for (var solicitacao : solicitacoes.values()) {
            if (solicitacao.getStatus() == status) {
                resultado.add(solicitacao);
            }
        }
        return resultado;
    }
}
