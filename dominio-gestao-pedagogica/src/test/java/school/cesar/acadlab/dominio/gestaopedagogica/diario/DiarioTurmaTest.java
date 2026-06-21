package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DiarioTurmaTest {

    private static final LocalDate INICIO = LocalDate.of(2025, 2, 1);
    private static final LocalDate FIM = LocalDate.of(2025, 7, 31);
    private static final LocalDate DENTRO_PERIODO = LocalDate.of(2025, 4, 10);
    private static final LocalDate FORA_PERIODO = LocalDate.of(2025, 9, 1);

    private final DiarioTurmaId diarioId = new DiarioTurmaId(1);
    private final TurmaId turmaId = new TurmaId(1);
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(1);
    private final ProfessorId professor = new ProfessorId(1);
    private final ProfessorId outroProfesor = new ProfessorId(2);
    private final EstudanteId estudante = new EstudanteId(10);
    private final RegistroAulaId aulaId = new RegistroAulaId(1);
    private final AvaliacaoId avaliacaoId = new AvaliacaoId(1);

    private DiarioTurma diario;

    @BeforeEach
    void setUp() {
        diario = new DiarioTurma(diarioId, turmaId, periodoId, professor, INICIO, FIM, 6.0, 75.0);
    }

    @Test
    void rn1_professorNaoResponsavelNaoPodeRegistrarAula() {
        var e = assertThrows(IllegalStateException.class,
                () -> diario.registrarAula(aulaId, outroProfesor, DENTRO_PERIODO, "Conteúdo"));
        assertTrue(e.getMessage().contains("professor não é o responsável pelo diário"));
    }

    @Test
    void rn2_aulaForaDoPeriodoEhRejeitada() {
        var e = assertThrows(IllegalStateException.class,
                () -> diario.registrarAula(aulaId, professor, FORA_PERIODO, "Conteúdo"));
        assertTrue(e.getMessage().contains("aula deve ser registrada dentro do período letivo"));
    }

    @Test
    void rn2_aulaDentroDoPeriodoEhRegistrada() {
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        assertEquals(1, diario.getAulas().size());
    }

    @Test
    void rn3_estudanteNaoMatriculadoNaoPodeTerFrequenciaRegistrada() {
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        var e = assertThrows(IllegalStateException.class,
                () -> diario.registrarFrequencia(professor, aulaId, estudante, true));
        assertTrue(e.getMessage().contains("estudante não está matriculado na turma"));
    }

    @Test
    void rn4_professorNaoResponsavelNaoPodeRegistrarFrequencia() {
        diario.adicionarEstudanteAtivo(estudante);
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        var e = assertThrows(IllegalStateException.class,
                () -> diario.registrarFrequencia(outroProfesor, aulaId, estudante, true));
        assertTrue(e.getMessage().contains("professor não é o responsável pelo diário"));
    }

    @Test
    void rn5_somaPesosUltrapassaCem() {
        var id2 = new AvaliacaoId(2);
        diario.adicionarAvaliacao(avaliacaoId, "P1", 60.0, DENTRO_PERIODO);
        var e = assertThrows(IllegalStateException.class,
                () -> diario.adicionarAvaliacao(id2, "P2", 50.0, DENTRO_PERIODO));
        assertTrue(e.getMessage().contains("soma dos pesos das avaliações ultrapassa 100%"));
    }

    @Test
    void rn6_prazoAvaliacaoForaDoPeriodo() {
        var e = assertThrows(IllegalStateException.class,
                () -> diario.adicionarAvaliacao(avaliacaoId, "P1", 30.0, FORA_PERIODO));
        assertTrue(e.getMessage().contains("prazo da avaliação está fora do período letivo"));
    }

    @Test
    void rn7_fecharResultadoAprovado() {
        diario.adicionarEstudanteAtivo(estudante);
        diario.adicionarAvaliacao(avaliacaoId, "P1", 100.0, DENTRO_PERIODO);
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        diario.registrarFrequencia(professor, aulaId, estudante, true);
        diario.lancarNota(estudante, avaliacaoId, 8.0);
        diario.fecharResultado(estudante);
        assertEquals(SituacaoResultado.APROVADO, diario.getResultados().get(0).getSituacao());
    }

    @Test
    void rn7_fecharResultadoReprovadoPorNota() {
        diario.adicionarEstudanteAtivo(estudante);
        diario.adicionarAvaliacao(avaliacaoId, "P1", 100.0, DENTRO_PERIODO);
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        diario.registrarFrequencia(professor, aulaId, estudante, true);
        diario.lancarNota(estudante, avaliacaoId, 2.0);
        diario.fecharResultado(estudante);
        assertEquals(SituacaoResultado.REPROVADO_NOTA, diario.getResultados().get(0).getSituacao());
    }

    @Test
    void rn9_revisaoForaDaJanelaRejeitada() {
        var fimJanela = LocalDate.of(2025, 8, 10);
        var hoje = LocalDate.of(2025, 8, 20);
        diario.adicionarEstudanteAtivo(estudante);
        var e = assertThrows(IllegalStateException.class,
                () -> diario.solicitarRevisaoNota(estudante, hoje, fimJanela));
        assertTrue(e.getMessage().contains("janela de revisão de nota encerrada"));
    }

    @Test
    void rn11_professorDiferenteNaoPodeCorrigirAula() {
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        var e = assertThrows(IllegalStateException.class,
                () -> diario.corrigirAula(aulaId, outroProfesor, "Conteúdo alterado"));
        assertTrue(e.getMessage().contains("apenas o professor responsável pela aula pode corrigi-la"));
    }

    @Test
    void rn12_notaRecuperacaoParaEstudanteNaoEmRecuperacaoRejeitada() {
        diario.adicionarEstudanteAtivo(estudante);
        diario.adicionarAvaliacao(avaliacaoId, "P1", 100.0, DENTRO_PERIODO);
        diario.registrarAula(aulaId, professor, DENTRO_PERIODO, "Conteúdo");
        diario.registrarFrequencia(professor, aulaId, estudante, true);
        diario.lancarNota(estudante, avaliacaoId, 8.0);
        diario.fecharResultado(estudante);
        var e = assertThrows(IllegalStateException.class,
                () -> diario.lancarNotaRecuperacao(estudante, 9.0, DENTRO_PERIODO));
        assertTrue(e.getMessage().contains("estudante não está em situação de recuperação"));
    }

    @Test
    void rn13_notaRecuperacaoAposEncerramentoPeriodoRejeitada() {
        diario.adicionarEstudanteAtivo(estudante);
        var e = assertThrows(IllegalStateException.class,
                () -> diario.lancarNotaRecuperacao(estudante, 7.0, FORA_PERIODO));
        assertTrue(e.getMessage().contains("período letivo já encerrado"));
    }
}
