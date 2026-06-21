package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class RetificarRegistroFuncionalidade {
    private final HistoricoFuncionalidade ctx;
    private HistoricoAcademico historico;
    private RegistroDisciplinaId registroId;
    private final SecretariaId secretaria = new SecretariaId(1);

    public RetificarRegistroFuncionalidade(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um histórico com registro de disciplina consolidado para retificação")
    public void historicoComRegistroConsolidado() {
        historico = new HistoricoAcademico(
                ctx.repositorio.proximoId(),
                new EstudanteId(5),
                new MatrizCurricularId(1));
        registroId = ctx.repositorio.proximoRegistroId();
        historico.consolidarRegistro(registroId, new DisciplinaId(1), new TurmaId(1),
                new PeriodoLetivoId(1), 4.0, 80.0, SituacaoAcademica.REPROVADO_NOTA, true);
        ctx.repositorio.salvar(historico);
    }

    @Quando("a secretaria retifica o registro para situação {string}")
    public void retificaRegistro(String novaSituacao) {
        try {
            historico.retificarRegistro(
                    ctx.repositorio.proximoRetificacaoId(),
                    registroId,
                    SituacaoAcademica.valueOf(novaSituacao),
                    secretaria,
                    "Revisão da nota após recurso deferido",
                    LocalDate.of(2025, 8, 1));
            ctx.repositorio.salvar(historico);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a situação do registro é atualizada para {string}")
    public void situacaoAtualizada(String situacaoEsperada) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var registro = historico.getRegistros().stream()
                .filter(r -> r.getId().equals(registroId))
                .findFirst().orElseThrow();
        assertEquals(SituacaoAcademica.valueOf(situacaoEsperada), registro.getSituacao());
    }

    @E("a retificação preserva a situação anterior no histórico de retificações")
    public void retificacaoPreservaSituacaoAnterior() {
        assertEquals(1, historico.getRetificacoes().size());
        var retificacao = historico.getRetificacoes().get(0);
        assertEquals(SituacaoAcademica.REPROVADO_NOTA, retificacao.getSituacaoAnterior());
        assertEquals(SituacaoAcademica.APROVADO, retificacao.getNovaSituacao());
    }

    @Quando("a secretaria tenta retificar um registro inexistente")
    public void tentaRetificarRegistroInexistente() {
        try {
            historico.retificarRegistro(
                    ctx.repositorio.proximoRetificacaoId(),
                    new RegistroDisciplinaId(999),
                    SituacaoAcademica.APROVADO,
                    secretaria,
                    "Justificativa",
                    LocalDate.of(2025, 8, 1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
