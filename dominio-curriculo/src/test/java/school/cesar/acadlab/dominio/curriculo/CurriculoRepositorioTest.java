package school.cesar.acadlab.dominio.curriculo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import school.cesar.acadlab.dominio.curriculo.porta.ConsultaMatrizAtivaPorta;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaTurmasPorta;

public class CurriculoRepositorioTest implements MatrizCurricularRepositorio, ConsultaMatrizAtivaPorta, ConsultaTurmasPorta {

    private int proximaMatrizIdSeq = 1;
    private final Map<MatrizCurricularId, MatrizCurricular> matrizes = new HashMap<>();
    private final Set<DisciplinaId> disciplinasComTurma = new HashSet<>();

    @Override
    public MatrizCurricularId proximaMatrizId() {
        return new MatrizCurricularId(proximaMatrizIdSeq++);
    }

    @Override
    public void salvar(MatrizCurricular matriz) {
        matrizes.put(matriz.getId(), matriz);
    }

    @Override
    public Optional<MatrizCurricular> buscarPorId(MatrizCurricularId id) {
        return Optional.ofNullable(matrizes.get(id));
    }

    @Override
    public List<MatrizCurricular> buscarPorCurso(CursoId cursoId) {
        List<MatrizCurricular> resultado = new ArrayList<>();
        for (MatrizCurricular m : matrizes.values()) {
            if (m.getCursoId().equals(cursoId)) {
                resultado.add(m);
            }
        }
        return resultado;
    }

    @Override
    public boolean existeMatrizAtivaParaCurso(CursoId cursoId) {
        return matrizes.values().stream()
                .anyMatch(m -> m.getCursoId().equals(cursoId) && m.getStatus() == StatusMatriz.ATIVA);
    }

    @Override
    public boolean existeTurmaParaDisciplina(DisciplinaId disciplinaId) {
        return disciplinasComTurma.contains(disciplinaId);
    }

    public void adicionarDisciplinaComTurma(DisciplinaId disciplinaId) {
        disciplinasComTurma.add(disciplinaId);
    }
}
