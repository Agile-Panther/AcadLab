package school.cesar.acadlab.dominio.curriculo;

import static org.apache.commons.lang3.Validate.notNull;

import school.cesar.acadlab.dominio.curriculo.porta.ConsultaMatrizAtivaPorta;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaTurmasPorta;

/**
 * Serviço de domínio do contexto de Currículo. Orquestra o ciclo de vida da
 * matriz curricular (carregar agregado, executar a regra de negócio e persistir),
 * mantendo o controlador livre de acesso direto ao repositório e às portas.
 */
public class MatrizCurricularServico {

    private final MatrizCurricularRepositorio repositorio;
    private final ConsultaMatrizAtivaPorta consultaMatrizAtiva;
    private final ConsultaTurmasPorta consultaTurmas;

    public MatrizCurricularServico(MatrizCurricularRepositorio repositorio,
                                   ConsultaMatrizAtivaPorta consultaMatrizAtiva,
                                   ConsultaTurmasPorta consultaTurmas) {
        notNull(repositorio, "O repositório não pode ser nulo");
        notNull(consultaMatrizAtiva, "A porta de consulta de matriz ativa não pode ser nula");
        notNull(consultaTurmas, "A porta de consulta de turmas não pode ser nula");
        this.repositorio = repositorio;
        this.consultaMatrizAtiva = consultaMatrizAtiva;
        this.consultaTurmas = consultaTurmas;
    }

    public MatrizCurricular criar(CursoId cursoId, String nome, int cargaHorariaMinima,
                                  int creditosExigidos, int maximoTrancamentos) {
        var id = repositorio.proximaMatrizId();
        var matriz = new MatrizCurricular(id, cursoId, nome, cargaHorariaMinima,
                creditosExigidos, maximoTrancamentos);
        repositorio.salvar(matriz);
        return matriz;
    }

    public void adicionarDisciplina(MatrizCurricularId matrizId, DisciplinaId disciplinaId,
                                    TipoDisciplina tipo, int cargaHoraria, int creditos) {
        var matriz = obter(matrizId);
        matriz.adicionarDisciplina(disciplinaId, tipo, cargaHoraria, creditos);
        repositorio.salvar(matriz);
    }

    public void removerDisciplina(MatrizCurricularId matrizId, DisciplinaId disciplinaId) {
        var matriz = obter(matrizId);
        matriz.removerDisciplina(disciplinaId, consultaTurmas);
        repositorio.salvar(matriz);
    }

    public void adicionarPreRequisito(MatrizCurricularId matrizId, DisciplinaId disciplina,
                                      DisciplinaId preRequisito) {
        var matriz = obter(matrizId);
        matriz.adicionarPreRequisito(disciplina, preRequisito);
        repositorio.salvar(matriz);
    }

    public void adicionarCorrequisito(MatrizCurricularId matrizId, DisciplinaId disciplina,
                                      DisciplinaId correquisito) {
        var matriz = obter(matrizId);
        matriz.adicionarCorrequisito(disciplina, correquisito);
        repositorio.salvar(matriz);
    }

    public void ativar(MatrizCurricularId matrizId) {
        var matriz = obter(matrizId);
        matriz.ativar(consultaMatrizAtiva);
        repositorio.salvar(matriz);
    }

    public void desativar(MatrizCurricularId matrizId) {
        var matriz = obter(matrizId);
        matriz.desativar();
        repositorio.salvar(matriz);
    }

    private MatrizCurricular obter(MatrizCurricularId matrizId) {
        notNull(matrizId, "O id da matriz não pode ser nulo");
        return repositorio.buscarPorId(matrizId)
                .orElseThrow(() -> new IllegalArgumentException("Matriz curricular não encontrada"));
    }
}
