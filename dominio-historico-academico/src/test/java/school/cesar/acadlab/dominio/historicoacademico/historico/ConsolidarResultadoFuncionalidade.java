package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class ConsolidarResultadoFuncionalidade {
    private final HistoricoFuncionalidade ctx;
    private HistoricoAcademico historico;

    public ConsolidarResultadoFuncionalidade(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um histórico acadêmico de estudante ativo para consolidação")
    public void historicoDeEstudanteAtivo() {
        historico = new HistoricoAcademico(
                ctx.repositorio.proximoId(),
                new EstudanteId(1),
                new MatrizCurricularId(1));
        ctx.repositorio.salvar(historico);
    }

    @Quando("a secretaria consolida o resultado de uma turma encerrada com situação {string}")
    public void consolidaResultadoTurmaEncerrada(String situacaoStr) {
        try {
            historico.consolidarRegistro(
                    ctx.repositorio.proximoRegistroId(),
                    new DisciplinaId(1),
                    new TurmaId(1),
                    new PeriodoLetivoId(1),
                    8.5, 85.0,
                    SituacaoAcademica.valueOf(situacaoStr),
                    true);
            ctx.repositorio.salvar(historico);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o registro é adicionado ao histórico com a situação {string}")
    public void registroAdicionadoComSituacao(String situacaoStr) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(1, historico.getRegistros().size());
        assertEquals(SituacaoAcademica.valueOf(situacaoStr),
                historico.getRegistros().get(0).getSituacao());
    }

    @Quando("a secretaria tenta consolidar resultado de turma não encerrada")
    public void tentaConsolidarTurmaNaoEncerrada() {
        try {
            historico.consolidarRegistro(
                    ctx.repositorio.proximoRegistroId(),
                    new DisciplinaId(1),
                    new TurmaId(1),
                    new PeriodoLetivoId(1),
                    8.5, 85.0,
                    SituacaoAcademica.APROVADO,
                    false);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("a secretaria tenta consolidar resultado sem informar situação acadêmica")
    public void tentaConsolidarSemSituacao() {
        try {
            historico.consolidarRegistro(
                    ctx.repositorio.proximoRegistroId(),
                    new DisciplinaId(1),
                    new TurmaId(1),
                    new PeriodoLetivoId(1),
                    8.5, 85.0,
                    null,
                    true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
