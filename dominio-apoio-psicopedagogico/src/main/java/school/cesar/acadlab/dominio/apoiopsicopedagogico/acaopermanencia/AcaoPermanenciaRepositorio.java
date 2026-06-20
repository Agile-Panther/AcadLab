package school.cesar.acadlab.dominio.apoiopsicopedagogico.acaopermanencia;

public interface AcaoPermanenciaRepositorio {
    AcaoPermanenciaId proximaAcaoId();
    void salvar(AcaoPermanencia acao);
    AcaoPermanencia obter(AcaoPermanenciaId id);
}
