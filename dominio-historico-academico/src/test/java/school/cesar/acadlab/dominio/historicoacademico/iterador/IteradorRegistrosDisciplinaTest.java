package school.cesar.acadlab.dominio.historicoacademico.iterador;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.historicoacademico.historico.DisciplinaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.EstudanteId;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademico;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademicoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.MatrizCurricularId;
import school.cesar.acadlab.dominio.historicoacademico.historico.PeriodoLetivoId;
import school.cesar.acadlab.dominio.historicoacademico.historico.RegistroDisciplina;
import school.cesar.acadlab.dominio.historicoacademico.historico.RegistroDisciplinaId;
import school.cesar.acadlab.dominio.historicoacademico.historico.SituacaoAcademica;
import school.cesar.acadlab.dominio.historicoacademico.historico.TurmaId;

class IteradorRegistrosDisciplinaTest {

    private RegistroDisciplina registro(int id) {
        return new RegistroDisciplina(new RegistroDisciplinaId(id), new DisciplinaId(id),
                new TurmaId(id), new PeriodoLetivoId(1), 8.0, 90.0, SituacaoAcademica.APROVADO);
    }

    @Test
    void iteraNaOrdemDeInsercao() {
        var r1 = registro(1);
        var r2 = registro(2);
        var r3 = registro(3);
        var iterador = new IteradorRegistrosDisciplina(List.of(r1, r2, r3));

        assertTrue(iterador.temProximo());
        assertSame(r1, iterador.proximo());
        assertSame(r2, iterador.proximo());
        assertSame(r3, iterador.proximo());
        assertFalse(iterador.temProximo());
    }

    @Test
    void proximo_aposEsgotar_lancaExcecao() {
        var iterador = new IteradorRegistrosDisciplina(List.of(registro(1)));
        iterador.proximo();

        assertFalse(iterador.temProximo());
        var erro = assertThrows(IllegalStateException.class, iterador::proximo);
        assertTrue(erro.getMessage().contains("Não há mais registros"));
    }

    @Test
    void colecaoVazia_naoTemProximo() {
        var iterador = new IteradorRegistrosDisciplina(List.of());

        assertFalse(iterador.temProximo());
        assertThrows(IllegalStateException.class, iterador::proximo);
    }

    @Test
    void iteradorDoHistorico_protegeColecaoInterna() {
        var historico = new HistoricoAcademico(new HistoricoAcademicoId(1),
                new EstudanteId(1), new MatrizCurricularId(1));
        historico.consolidarRegistro(new RegistroDisciplinaId(1), new DisciplinaId(1),
                new TurmaId(1), new PeriodoLetivoId(1), 8.0, 90.0, SituacaoAcademica.APROVADO, true);

        // a coleção exposta pelo agregado é imutável: a iteração não permite mutar o estado interno
        assertThrows(UnsupportedOperationException.class,
                () -> historico.getRegistros().add(registro(99)));

        int total = 0;
        var iterador = historico.iteradorRegistros();
        while (iterador.temProximo()) {
            iterador.proximo();
            total++;
        }
        assertEquals(1, total);
    }
}
