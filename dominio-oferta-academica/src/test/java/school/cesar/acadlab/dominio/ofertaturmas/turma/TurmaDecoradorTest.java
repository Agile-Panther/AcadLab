package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.EstudanteId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaComListaEspera;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaOnline;

class TurmaDecoradorTest {

    private Turma criarTurmaPresencial() {
        return new Turma(new TurmaId(1), new PeriodoLetivoId(1),
                new DisciplinaId(1), ModalidadeTurma.PRESENCIAL, 30);
    }

    private Turma criarTurmaEAD() {
        return new Turma(new TurmaId(2), new PeriodoLetivoId(1),
                new DisciplinaId(1), ModalidadeTurma.EAD, 50);
    }

    @Test
    void entrarListaEspera_comSucesso() {
        var decorada = new TurmaComListaEspera(criarTurmaPresencial());
        decorada.entrarListaEspera(new EstudanteId(1));
        assertEquals(1, decorada.getListaEspera().size());
    }

    @Test
    void entrarListaEspera_duplicado_lanca() {
        var decorada = new TurmaComListaEspera(criarTurmaPresencial());
        decorada.entrarListaEspera(new EstudanteId(1));
        assertThrows(IllegalStateException.class, () -> decorada.entrarListaEspera(new EstudanteId(1)));
    }

    @Test
    void sairListaEspera_comSucesso() {
        var decorada = new TurmaComListaEspera(criarTurmaPresencial());
        decorada.entrarListaEspera(new EstudanteId(1));
        decorada.sairListaEspera(new EstudanteId(1));
        assertTrue(decorada.getListaEspera().isEmpty());
    }

    @Test
    void sairListaEspera_naoEstaLista_lanca() {
        var decorada = new TurmaComListaEspera(criarTurmaPresencial());
        assertThrows(IllegalStateException.class, () -> decorada.sairListaEspera(new EstudanteId(99)));
    }

    @Test
    void proximoDaEspera_retornaPrimeiro() {
        var decorada = new TurmaComListaEspera(criarTurmaPresencial());
        decorada.entrarListaEspera(new EstudanteId(1));
        decorada.entrarListaEspera(new EstudanteId(2));
        assertEquals(new EstudanteId(1), decorada.proximoDaEspera().orElseThrow());
    }

    @Test
    void turmaOnline_comEAD_comSucesso() {
        var decorada = new TurmaOnline(criarTurmaEAD());
        assertNotNull(decorada);
    }

    @Test
    void turmaOnline_comPresencial_lanca() {
        assertThrows(IllegalArgumentException.class, () -> new TurmaOnline(criarTurmaPresencial()));
    }

    @Test
    void turmaOnline_definirLink_comSucesso() {
        var decorada = new TurmaOnline(criarTurmaEAD());
        decorada.definirLinkAcesso("https://meet.google.com/abc");
        assertEquals("https://meet.google.com/abc", decorada.getLinkAcesso());
    }
}
