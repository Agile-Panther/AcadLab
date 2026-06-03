package school.cesar.acadlab.dominio.ofertaturmas.professor;

import java.util.List;

public interface ProfessorRepositorio {
    ProfessorId proximoId();
    void salvar(Professor professor);
    Professor obter(ProfessorId id);
    List<Professor> pesquisarAtivos();
}
