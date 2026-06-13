package school.cesar.acadlab.dominio.ofertaturmas;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.*;
import school.cesar.acadlab.dominio.ofertaturmas.professor.Professor;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;

public class ProfessorRepositorioTest implements ProfessorRepositorio {
    private int proximoSeq = 1;
    private final Map<ProfessorId, Professor> professores = new HashMap<>();

    @Override
    public ProfessorId proximoId() { return new ProfessorId(proximoSeq++); }

    @Override
    public void salvar(Professor professor) {
        notNull(professor, "O professor não pode ser nulo");
        professores.put(professor.getId(), professor);
    }

    @Override
    public Professor obter(ProfessorId id) {
        notNull(id, "O id do professor não pode ser nulo");
        return Optional.ofNullable(professores.get(id)).orElseThrow();
    }

    @Override
    public List<Professor> pesquisarAtivos() {
        var resultado = new ArrayList<Professor>();
        for (var p : professores.values())
            if (p.isAtivo()) resultado.add(p);
        return resultado;
    }
}
