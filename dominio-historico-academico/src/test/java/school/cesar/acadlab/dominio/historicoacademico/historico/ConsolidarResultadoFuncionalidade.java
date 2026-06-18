package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class ConsolidarResultadoFuncionalidade extends HistoricoFuncionalidade {
    private HistoricoAcademico historico;
    private RuntimeException excecao;

    @Dado("um histórico acadêmico de estudante ativo para consolidação")
    public void historicoDeEstudanteAtivo() {
        historico = new HistoricoAcademico(
                repositorio.proximoId(),
                new EstudanteId(1),
                new MatrizCurricularId(1));
        repositorio.salvar(historico);
    }

    @Quando("a secretaria consolida o resultado de uma turma encerrada com situação {string}")
    public void consolidaResultadoTurmaEncerrada(String situacaoStr) {
        try {
            historico.consolidarRegistro(
                    repositorio.proximoRegistroId(),
                    new DisciplinaId(1),
                    new TurmaId(1),
                    new PeriodoLetivoId(1),
                    8.5, 85.0,
                    SituacaoAcademica.valueOf(situacaoStr),
                    true);
            repositorio.salvar(historico);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o registro é adicionado ao histórico com a situação {string}")
    public void registroAdicionadoComSituacao(String situacaoStr) {
        assertNull(excecao, "Não deveria ter lançado exceção");
        assertEquals(1, historico.getRegistros().size());
        assertEquals(SituacaoAcademica.valueOf(situacaoStr),
                historico.getRegistros().get(0).getSituacao());
    }

    @Quando("a secretaria tenta consolidar resultado de turma não encerrada")
    public void tentaConsolidarTurmaNaoEncerrada() {
        try {
            historico.consolidarRegistro(
                    repositorio.proximoRegistroId(),
                    new DisciplinaId(1),
                    new TurmaId(1),
                    new PeriodoLetivoId(1),
                    8.5, 85.0,
                    SituacaoAcademica.APROVADO,
                    false);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a consolidação informando RN-1")
    public void sistemaRejeitaRN1() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-1"));
    }

    @Quando("a secretaria tenta consolidar resultado sem informar situação acadêmica")
    public void tentaConsolidarSemSituacao() {
        try {
            historico.consolidarRegistro(
                    repositorio.proximoRegistroId(),
                    new DisciplinaId(1),
                    new TurmaId(1),
                    new PeriodoLetivoId(1),
                    8.5, 85.0,
                    null,
                    true);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a consolidação informando RN-2")
    public void sistemaRejeitaRN2() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-2"));
    }
}
