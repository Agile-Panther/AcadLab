package school.cesar.acadlab.dominio.curriculo.porta;

import school.cesar.acadlab.dominio.curriculo.CursoId;

public interface ConsultaMatrizAtivaPorta {
    boolean existeMatrizAtivaParaCurso(CursoId cursoId);
}
