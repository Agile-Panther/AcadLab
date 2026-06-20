package school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao;

public interface SolicitacaoApoioRepositorio {
    SolicitacaoApoioId proximaSolicitacaoId();
    void salvar(SolicitacaoApoio solicitacao);
    SolicitacaoApoio obter(SolicitacaoApoioId id);
}
