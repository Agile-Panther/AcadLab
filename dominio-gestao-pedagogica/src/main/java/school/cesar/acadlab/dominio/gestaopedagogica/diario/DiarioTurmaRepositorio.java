package school.cesar.acadlab.dominio.gestaopedagogica.diario;

public interface DiarioTurmaRepositorio {
    DiarioTurmaId proximoId();
    void salvar(DiarioTurma diario);
    DiarioTurma obter(DiarioTurmaId id);
    RegistroAulaId proximoAulaId();
    AvaliacaoId proximaAvaliacaoId();
}
