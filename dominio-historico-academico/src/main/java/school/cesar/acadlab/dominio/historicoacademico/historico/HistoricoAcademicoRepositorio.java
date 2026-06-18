package school.cesar.acadlab.dominio.historicoacademico.historico;

import java.util.Optional;

public interface HistoricoAcademicoRepositorio {
    HistoricoAcademicoId proximoId();
    RegistroDisciplinaId proximoRegistroId();
    AproveitamentoId proximoAproveitamentoId();
    RetificacaoId proximoRetificacaoId();
    AcompanhamentoId proximoAcompanhamentoId();
    void salvar(HistoricoAcademico historico);
    HistoricoAcademico obter(HistoricoAcademicoId id);
    Optional<HistoricoAcademico> buscarPorEstudante(EstudanteId estudanteId);
}
