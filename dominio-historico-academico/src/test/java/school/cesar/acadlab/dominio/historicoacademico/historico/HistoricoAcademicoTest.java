package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HistoricoAcademicoTest {

    private static final LocalDate HOJE = LocalDate.of(2025, 6, 1);

    private final HistoricoAcademicoId id = new HistoricoAcademicoId(1);
    private final EstudanteId estudante = new EstudanteId(1);
    private final MatrizCurricularId matriz = new MatrizCurricularId(1);
    private final SecretariaId secretaria = new SecretariaId(1);
    private final RegistroDisciplinaId registroId = new RegistroDisciplinaId(1);
    private final DisciplinaId disciplina = new DisciplinaId(10);
    private final TurmaId turma = new TurmaId(5);
    private final PeriodoLetivoId periodo = new PeriodoLetivoId(3);
    private final RetificacaoId retificacaoId = new RetificacaoId(1);
    private final AproveitamentoId aproveitamentoId = new AproveitamentoId(1);
    private final AcompanhamentoId acompanhamentoId = new AcompanhamentoId(1);

    private HistoricoAcademico historico;

    @BeforeEach
    void setUp() {
        historico = new HistoricoAcademico(id, estudante, matriz);
    }

    @Test
    void rn1_consolidarTurmaEncerrada_adicionaRegistro() {
        historico.consolidarRegistro(registroId, disciplina, turma, periodo, 8.0, 90.0, SituacaoAcademica.APROVADO, true);
        assertEquals(1, historico.getRegistros().size());
        assertEquals(SituacaoAcademica.APROVADO, historico.getRegistros().get(0).getSituacao());
    }

    @Test
    void rn1_consolidarTurmaNaoEncerrada_lancaExcecao() {
        var e = assertThrows(IllegalStateException.class,
                () -> historico.consolidarRegistro(registroId, disciplina, turma, periodo, 8.0, 90.0, SituacaoAcademica.APROVADO, false));
        assertTrue(e.getMessage().contains("a turma ainda não foi encerrada"));
    }

    @Test
    void rn2_consolidarSemSituacao_lancaExcecao() {
        var e = assertThrows(IllegalStateException.class,
                () -> historico.consolidarRegistro(registroId, disciplina, turma, periodo, 8.0, 90.0, null, true));
        assertTrue(e.getMessage().contains("situação acadêmica não informada"));
    }

    @Test
    void rn4_acompanhamentoSemVinculoAtivo_lancaExcecao() {
        var e = assertThrows(IllegalStateException.class,
                () -> historico.registrarAcompanhamento(acompanhamentoId, "Observação", HOJE, false));
        assertTrue(e.getMessage().contains("estudante não possui vínculo ativo"));
    }

    @Test
    void rn4_acompanhamentoComVinculoAtivo_adicionaAcompanhamento() {
        historico.registrarAcompanhamento(acompanhamentoId, "Observação", HOJE, true);
        assertEquals(1, historico.getAcompanhamentos().size());
    }

    @Test
    void rn5_atualizarSituacaoComAuditoria() {
        historico.atualizarSituacaoDiscente(SituacaoDiscente.FORMANDO, secretaria, "Conclusão iminente", HOJE);
        assertEquals(SituacaoDiscente.FORMANDO, historico.getSituacaoDiscente());
        assertEquals(1, historico.getTrilhaAuditoria().size());
    }

    @Test
    void rn5_atualizarSituacaoSemJustificativa_lancaExcecao() {
        assertThrows(Exception.class,
                () -> historico.atualizarSituacaoDiscente(SituacaoDiscente.FORMANDO, secretaria, "   ", HOJE));
    }

    @Test
    void rn7_aproveitamentoCargaInsuficiente_lancaExcecao() {
        var e = assertThrows(IllegalStateException.class,
                () -> historico.registrarAproveitamento(aproveitamentoId, disciplina, 30, 60, "UFPE", "Cálculo I"));
        assertTrue(e.getMessage().contains("carga horária externa insuficiente para aproveitamento"));
    }

    @Test
    void rn7_aproveitamentoCargaSuficiente_adicionaAproveitamento() {
        historico.registrarAproveitamento(aproveitamentoId, disciplina, 60, 60, "UFPE", "Cálculo I");
        assertEquals(1, historico.getAproveitamentos().size());
    }

    @Test
    void rn8_retificarRegistroExistente_preservaSituacaoAnterior() {
        historico.consolidarRegistro(registroId, disciplina, turma, periodo, 4.0, 90.0, SituacaoAcademica.REPROVADO_NOTA, true);
        historico.retificarRegistro(retificacaoId, registroId, SituacaoAcademica.APROVADO, secretaria, "Erro na nota", HOJE);
        assertEquals(SituacaoAcademica.APROVADO, historico.getRegistros().get(0).getSituacao());
        assertEquals(1, historico.getRetificacoes().size());
        assertEquals(SituacaoAcademica.REPROVADO_NOTA, historico.getRetificacoes().get(0).getSituacaoAnterior());
    }

    @Test
    void rn8_retificarRegistroInexistente_lancaExcecao() {
        var registroInexistente = new RegistroDisciplinaId(999);
        var e = assertThrows(IllegalArgumentException.class,
                () -> historico.retificarRegistro(retificacaoId, registroInexistente, SituacaoAcademica.APROVADO, secretaria, "Motivo", HOJE));
        assertTrue(e.getMessage().contains("registro não encontrado no histórico"));
    }
}
