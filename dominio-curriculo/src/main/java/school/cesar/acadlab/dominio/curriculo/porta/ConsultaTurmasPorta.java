package school.cesar.acadlab.dominio.curriculo.porta;

import school.cesar.acadlab.dominio.curriculo.DisciplinaId;

public interface ConsultaTurmasPorta {
    boolean existeTurmaParaDisciplina(DisciplinaId disciplinaId);
}
