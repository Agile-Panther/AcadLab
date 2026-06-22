package school.cesar.acadlab.dominio.curriculo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MatrizCurricularServicoTest {

    private CurriculoRepositorioTest repositorio;
    private MatrizCurricularServico servico;

    private final CursoId cursoId = new CursoId(1);
    private final DisciplinaId disc1 = new DisciplinaId(1);
    private final DisciplinaId disc2 = new DisciplinaId(2);

    @BeforeEach
    void setUp() {
        repositorio = new CurriculoRepositorioTest();
        servico = new MatrizCurricularServico(repositorio, repositorio, repositorio);
    }

    @Test
    void criar_devePersistirMatrizComIdGerado() {
        MatrizCurricular matriz = servico.criar(cursoId, "Engenharia de Software", 2400, 160, 3);

        assertEquals(StatusMatriz.RASCUNHO, matriz.getStatus());
        assertTrue(repositorio.buscarPorId(matriz.getId()).isPresent());
    }

    @Test
    void adicionarDisciplina_devePersistirAlteracao() {
        MatrizCurricular matriz = servico.criar(cursoId, "Engenharia de Software", 2400, 160, 3);

        servico.adicionarDisciplina(matriz.getId(), disc1, TipoDisciplina.OBRIGATORIA, 60, 4);

        assertEquals(1, repositorio.buscarPorId(matriz.getId()).orElseThrow().getItens().size());
    }

    @Test
    void ativar_deveOrquestrarDominioEPersistir() {
        MatrizCurricular matriz = servico.criar(cursoId, "Engenharia de Software", 2400, 160, 3);
        servico.adicionarDisciplina(matriz.getId(), disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);

        servico.ativar(matriz.getId());

        assertEquals(StatusMatriz.ATIVA, repositorio.buscarPorId(matriz.getId()).orElseThrow().getStatus());
    }

    @Test
    void desativar_deveOrquestrarDominioEPersistir() {
        MatrizCurricular matriz = servico.criar(cursoId, "Engenharia de Software", 2400, 160, 3);
        servico.adicionarDisciplina(matriz.getId(), disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);
        servico.ativar(matriz.getId());

        servico.desativar(matriz.getId());

        assertEquals(StatusMatriz.INATIVA, repositorio.buscarPorId(matriz.getId()).orElseThrow().getStatus());
    }

    @Test
    void adicionarPreRequisito_devePersistirRelacao() {
        MatrizCurricular matriz = servico.criar(cursoId, "Engenharia de Software", 2400, 160, 3);
        servico.adicionarDisciplina(matriz.getId(), disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        servico.adicionarDisciplina(matriz.getId(), disc2, TipoDisciplina.OBRIGATORIA, 60, 4);

        servico.adicionarPreRequisito(matriz.getId(), disc2, disc1);

        assertEquals(1, repositorio.buscarPorId(matriz.getId()).orElseThrow()
                .getPreRequisitos().get(disc2).size());
    }

    @Test
    void operarSobreMatrizInexistente_deveLancarExcecao() {
        var excecao = assertThrows(IllegalArgumentException.class,
                () -> servico.ativar(new MatrizCurricularId(999)));
        assertTrue(excecao.getMessage().contains("não encontrada"));
    }
}
