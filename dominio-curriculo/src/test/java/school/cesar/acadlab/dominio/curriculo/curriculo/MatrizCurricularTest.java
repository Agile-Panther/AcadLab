package school.cesar.acadlab.dominio.curriculo.curriculo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.curriculo.CursoId;
import school.cesar.acadlab.dominio.curriculo.CurriculoRepositorioTest;
import school.cesar.acadlab.dominio.curriculo.DisciplinaId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularId;
import school.cesar.acadlab.dominio.curriculo.StatusMatriz;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;

class MatrizCurricularTest {

    private MatrizCurricular matriz;
    private CurriculoRepositorioTest repositorio;

    private final MatrizCurricularId matrizId = new MatrizCurricularId(1);
    private final CursoId cursoId = new CursoId(1);
    private final DisciplinaId disc1 = new DisciplinaId(1);
    private final DisciplinaId disc2 = new DisciplinaId(2);
    private final DisciplinaId disc3 = new DisciplinaId(3);

    @BeforeEach
    void setUp() {
        repositorio = new CurriculoRepositorioTest();
        matriz = new MatrizCurricular(matrizId, cursoId, "Engenharia de Software", 2400, 160, 3);
    }

    @Test
    void adicionarDisciplina_duplicada_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);

        var excecao = assertThrows(IllegalStateException.class,
                () -> matriz.adicionarDisciplina(disc1, TipoDisciplina.OPTATIVA, 30, 2));
        assertTrue(excecao.getMessage().contains("RN-1"));
    }

    @Test
    void adicionarDisciplina_nova_deveFuncionar() {
        assertDoesNotThrow(() -> matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4));
        assertEquals(1, matriz.getItens().size());
    }

    @Test
    void ativar_comCargaInsuficiente_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);

        var excecao = assertThrows(IllegalStateException.class, () -> matriz.ativar(repositorio));
        assertTrue(excecao.getMessage().contains("RN-2"));
    }

    @Test
    void ativar_comCargaSuficiente_deveFuncionar() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);

        assertDoesNotThrow(() -> matriz.ativar(repositorio));
        assertEquals(StatusMatriz.ATIVA, matriz.getStatus());
    }

    @Test
    void adicionarPreRequisito_ciclicoDireto_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(disc2, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarPreRequisito(disc2, disc1);

        var excecao = assertThrows(IllegalStateException.class,
                () -> matriz.adicionarPreRequisito(disc1, disc2));
        assertTrue(excecao.getMessage().contains("RN-3"));
    }

    @Test
    void adicionarPreRequisito_semCiclo_deveFuncionar() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(disc2, TipoDisciplina.OBRIGATORIA, 60, 4);

        assertDoesNotThrow(() -> matriz.adicionarPreRequisito(disc2, disc1));
        assertEquals(1, matriz.getPreRequisitos().get(disc2).size());
    }

    @Test
    void adicionarCorrequisito_foraMatriz_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        DisciplinaId disciplinaForaMatriz = new DisciplinaId(99);

        var excecao = assertThrows(IllegalArgumentException.class,
                () -> matriz.adicionarCorrequisito(disc1, disciplinaForaMatriz));
        assertTrue(excecao.getMessage().contains("RN-4"));
    }

    @Test
    void adicionarCorrequisito_naMesmaMatriz_deveFuncionar() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(disc2, TipoDisciplina.OBRIGATORIA, 60, 4);

        assertDoesNotThrow(() -> matriz.adicionarCorrequisito(disc1, disc2));
        assertEquals(1, matriz.getCorrequisitos().get(disc1).size());
    }

    @Test
    void ativar_quandoJaExisteAtivaParaCurso_deveLancarExcecao() {
        MatrizCurricular outraMatriz = new MatrizCurricular(
                new MatrizCurricularId(2), cursoId, "Outra Matriz", 2400, 160, 3);
        outraMatriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);
        outraMatriz.ativar(repositorio);
        repositorio.salvar(outraMatriz);

        matriz.adicionarDisciplina(disc2, TipoDisciplina.OBRIGATORIA, 2400, 160);

        var excecao = assertThrows(IllegalStateException.class, () -> matriz.ativar(repositorio));
        assertTrue(excecao.getMessage().contains("RN-5"));
    }

    @Test
    void desativar_matrizAtiva_deveMudarParaInativa() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);
        matriz.ativar(repositorio);
        assertEquals(StatusMatriz.ATIVA, matriz.getStatus());

        matriz.desativar();
        assertEquals(StatusMatriz.INATIVA, matriz.getStatus());
    }

    @Test
    void adicionarDisciplina_emMatrizAtiva_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);
        matriz.ativar(repositorio);

        var excecao = assertThrows(IllegalStateException.class,
                () -> matriz.adicionarDisciplina(disc2, TipoDisciplina.OPTATIVA, 60, 4));
        assertTrue(excecao.getMessage().contains("RN-8"));
    }

    @Test
    void removerDisciplina_emMatrizAtiva_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 2400, 160);
        matriz.ativar(repositorio);

        var excecao = assertThrows(IllegalStateException.class,
                () -> matriz.removerDisciplina(disc1, repositorio));
        assertTrue(excecao.getMessage().contains("RN-8"));
    }

    @Test
    void removerDisciplina_vinculadaATurma_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        repositorio.adicionarDisciplinaComTurma(disc1);

        var excecao = assertThrows(IllegalStateException.class,
                () -> matriz.removerDisciplina(disc1, repositorio));
        assertTrue(excecao.getMessage().contains("RN-9"));
    }

    @Test
    void removerDisciplina_semTurma_deveFuncionar() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);

        assertDoesNotThrow(() -> matriz.removerDisciplina(disc1, repositorio));
        assertTrue(matriz.getItens().isEmpty());
    }

    @Test
    void adicionarPreRequisito_ciclicoTransitivo_deveLancarExcecao() {
        matriz.adicionarDisciplina(disc1, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(disc2, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarDisciplina(disc3, TipoDisciplina.OBRIGATORIA, 60, 4);
        matriz.adicionarPreRequisito(disc2, disc1);
        matriz.adicionarPreRequisito(disc3, disc2);

        var excecao = assertThrows(IllegalStateException.class,
                () -> matriz.adicionarPreRequisito(disc1, disc3));
        assertTrue(excecao.getMessage().contains("RN-3"));
    }
}
