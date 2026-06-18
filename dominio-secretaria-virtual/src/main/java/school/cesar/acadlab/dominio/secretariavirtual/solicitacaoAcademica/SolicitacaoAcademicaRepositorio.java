package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import java.util.List;
import java.util.Optional;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.ProtocoloId;

public interface SolicitacaoAcademicaRepositorio {
    SolicitacaoAcademicaId proximoId();
    ProtocoloId proximoProtocoloId();
    void salvar(SolicitacaoAcademica solicitacao);
    SolicitacaoAcademica obter(SolicitacaoAcademicaId id);
    List<SolicitacaoAcademica> pesquisarPorEstudante(EstudanteId estudanteId);
    Optional<SolicitacaoAcademica> pesquisarAbertaPorEstudanteTipoPeriodo(
            EstudanteId estudanteId, TipoSolicitacao tipo, PeriodoLetivoId periodoLetivoId);
    List<SolicitacaoAcademica> pesquisarPorStatus(StatusSolicitacao status);
}
