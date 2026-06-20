package school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.gestaopedagogica.diario.AvaliacaoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.EstudanteId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ProfessorId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.RegistroAulaId;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ResultadoEstudante;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.SituacaoResultado;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.TurmaId;

class ApuracaoResultadoTest {

    private static final LocalDate INICIO = LocalDate.of(2026, 3, 1);
    private static final LocalDate FIM = LocalDate.of(2026, 6, 30);
    private static final LocalDate DENTRO = LocalDate.of(2026, 4, 1);

    private final ProfessorId professor = new ProfessorId(1);
    private final EstudanteId estudante = new EstudanteId(10);

    private DiarioTurma diarioComNotas(double notaP1, double pesoP1, double notaP2, double pesoP2) {
        var diario = new DiarioTurma(new DiarioTurmaId(1), new TurmaId(1), new PeriodoLetivoId(1),
                professor, INICIO, FIM, 6.0, 75.0);
        diario.adicionarEstudanteAtivo(estudante);
        var aulaId = new RegistroAulaId(1);
        diario.registrarAula(aulaId, professor, DENTRO, "Aula 1");
        diario.registrarFrequencia(professor, aulaId, estudante, true);
        var p1 = new AvaliacaoId(1);
        var p2 = new AvaliacaoId(2);
        diario.adicionarAvaliacao(p1, "P1", pesoP1, DENTRO);
        diario.adicionarAvaliacao(p2, "P2", pesoP2, DENTRO);
        diario.lancarNota(estudante, p1, notaP1);
        diario.lancarNota(estudante, p2, notaP2);
        return diario;
    }

    private SituacaoResultado situacao(DiarioTurma diario) {
        return diario.getResultados().get(0).getSituacao();
    }

    @Test
    void templateMethod_executaEtapasNaOrdemFixa() {
        var espia = new ApuracaoEspia();
        var diario = diarioComNotas(7.0, 80.0, 7.0, 20.0);

        diario.fecharResultado(estudante, espia);

        assertEquals(List.of("aulas", "presencas", "media", "situacao"), espia.ordem);
    }

    @Test
    void mesmaEntrada_ponderadaEAritmeticaProduzemSituacoesDiferentes() {
        // P1 peso 80 nota 5.0; P2 peso 20 nota 9.0 — frequência 100%.
        // Ponderada: (5*80 + 9*20)/100 = 5.8 -> abaixo de 6.0 -> RECUPERACAO.
        // Aritmética: (5 + 9)/2 = 7.0 -> APROVADO.
        var diarioPonderada = diarioComNotas(5.0, 80.0, 9.0, 20.0);
        var diarioAritmetica = diarioComNotas(5.0, 80.0, 9.0, 20.0);

        diarioPonderada.fecharResultado(estudante, new ApuracaoMediaPonderada());
        diarioAritmetica.fecharResultado(estudante, new ApuracaoMediaAritmetica());

        assertEquals(SituacaoResultado.RECUPERACAO, situacao(diarioPonderada));
        assertEquals(SituacaoResultado.APROVADO, situacao(diarioAritmetica));
    }

    @Test
    void regimePadrao_fecharResultado_usaMediaPonderada() {
        var diarioPadrao = diarioComNotas(5.0, 80.0, 9.0, 20.0);
        var diarioPonderada = diarioComNotas(5.0, 80.0, 9.0, 20.0);

        diarioPadrao.fecharResultado(estudante);
        diarioPonderada.fecharResultado(estudante, new ApuracaoMediaPonderada());

        assertEquals(situacao(diarioPonderada), situacao(diarioPadrao));
    }

    /** Espião que registra a ordem das etapas do método-template. */
    private static class ApuracaoEspia extends ApuracaoResultado {
        final List<String> ordem = new ArrayList<>();

        @Override
        protected int contarAulas(DiarioTurma diario) {
            ordem.add("aulas");
            return super.contarAulas(diario);
        }

        @Override
        protected long contarPresencas(DiarioTurma diario, ResultadoEstudante resultado) {
            ordem.add("presencas");
            return super.contarPresencas(diario, resultado);
        }

        @Override
        protected double calcularMedia(DiarioTurma diario, ResultadoEstudante resultado) {
            ordem.add("media");
            return 7.0;
        }

        @Override
        protected void registrarSituacao(DiarioTurma diario, ResultadoEstudante resultado,
                                         int totalAulas, long presencas, double media) {
            ordem.add("situacao");
            super.registrarSituacao(diario, resultado, totalAulas, presencas, media);
        }
    }
}
