package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.EstudanteId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaComListaEspera;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaOferecida;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaOnline;

/**
 * Verifica a composição de múltiplos decorators de turma: empilhar TurmaOnline
 * sobre TurmaComListaEspera mantém os dois comportamentos adicionais e continua
 * delegando os atributos da turma base.
 */
class TurmaDecoradorComposicaoTest {

    private Turma turmaEAD() {
        return new Turma(new TurmaId(7), new PeriodoLetivoId(1),
                new DisciplinaId(3), ModalidadeTurma.EAD, 40);
    }

    @Test
    void empilharOnlineSobreListaEspera_preservaAmbosComportamentos() {
        var comListaEspera = new TurmaComListaEspera(turmaEAD());
        var online = new TurmaOnline(comListaEspera);

        // comportamento do decorator interno (lista de espera) continua acessível
        comListaEspera.entrarListaEspera(new EstudanteId(1));
        comListaEspera.entrarListaEspera(new EstudanteId(2));
        assertEquals(2, comListaEspera.getListaEspera().size());

        // comportamento do decorator externo (online)
        online.definirLinkAcesso("https://meet.exemplo/abc");
        assertEquals("https://meet.exemplo/abc", online.getLinkAcesso());
    }

    @Test
    void composicao_delegaAtributosDaTurmaBase() {
        TurmaOferecida decorada = new TurmaOnline(new TurmaComListaEspera(turmaEAD()));

        assertEquals(new TurmaId(7), decorada.getId());
        assertEquals(ModalidadeTurma.EAD, decorada.getModalidade());
        assertEquals(40, decorada.getCapacidade());
        assertEquals(StatusTurma.PLANEJADA, decorada.getStatus());
        assertTrue(decorada.getHorarios().isEmpty());
    }
}
