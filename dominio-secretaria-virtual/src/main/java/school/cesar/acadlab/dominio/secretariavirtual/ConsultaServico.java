package school.cesar.acadlab.dominio.secretariavirtual;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.StatusSolicitacao;

public class ConsultaServico {
    private final SolicitacaoAcademicaRepositorio repositorio;

    public ConsultaServico(SolicitacaoAcademicaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public SolicitacaoAcademica obterPorId(SolicitacaoAcademicaId id) {
        return repositorio.obter(id);
    }

    public List<SolicitacaoAcademica> listarPorEstudante(EstudanteId estudanteId) {
        return repositorio.pesquisarPorEstudante(estudanteId);
    }

    public List<SolicitacaoAcademica> listarPendentesDeAnalise() {
        return repositorio.pesquisarPorStatus(StatusSolicitacao.PENDENTE_ANALISE);
    }
}
