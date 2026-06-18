package school.cesar.acadlab.dominio.secretariavirtual;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademica;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;

public class AnaliseServico {
    private final SolicitacaoAcademicaRepositorio repositorio;

    public AnaliseServico(SolicitacaoAcademicaRepositorio repositorio) {
        notNull(repositorio, "O repositório não pode ser nulo");
        this.repositorio = repositorio;
    }

    public SolicitacaoAcademica iniciarAnalise(SolicitacaoAcademicaId id, SecretariaId analistaId) {
        var solicitacao = repositorio.obter(id);
        solicitacao.iniciarAnalise(analistaId);
        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    public SolicitacaoAcademica deferir(SolicitacaoAcademicaId id, SecretariaId analistaId,
                                         String justificativa, boolean impactoAcademico) {
        var solicitacao = repositorio.obter(id);
        solicitacao.deferir(analistaId, justificativa, impactoAcademico);
        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    public SolicitacaoAcademica indeferir(SolicitacaoAcademicaId id, SecretariaId analistaId,
                                           String justificativa) {
        var solicitacao = repositorio.obter(id);
        solicitacao.indeferir(analistaId, justificativa);
        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    public void solicitarComplementacao(SolicitacaoAcademicaId id, SecretariaId analistaId) {
        var solicitacao = repositorio.obter(id);
        solicitacao.solicitarComplementacao(analistaId);
        repositorio.salvar(solicitacao);
    }

    // RN4: vincular alterações e concluir
    public SolicitacaoAcademica vincularAlteracoesEConcluir(SolicitacaoAcademicaId id) {
        var solicitacao = repositorio.obter(id);
        solicitacao.vincularAlteracoes();
        solicitacao.concluir();
        repositorio.salvar(solicitacao);
        return solicitacao;
    }

    public SolicitacaoAcademica concluir(SolicitacaoAcademicaId id) {
        var solicitacao = repositorio.obter(id);
        solicitacao.concluir();
        repositorio.salvar(solicitacao);
        return solicitacao;
    }
}
