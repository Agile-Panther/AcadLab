package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class AtualizarSituacaoFuncionalidade {
    private final HistoricoFuncionalidade ctx;
    private HistoricoAcademico historico;
    private final SecretariaId secretaria = new SecretariaId(1);

    public AtualizarSituacaoFuncionalidade(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um histórico de estudante para atualização de situação discente")
    public void historicoParaAtualizacao() {
        historico = new HistoricoAcademico(
                ctx.repositorio.proximoId(),
                new EstudanteId(3),
                new MatrizCurricularId(1));
        ctx.repositorio.salvar(historico);
    }

    @Quando("a secretaria atualiza a situação do estudante para {string}")
    public void atualizaSituacao(String novaSituacao) {
        try {
            historico.atualizarSituacaoDiscente(
                    SituacaoDiscente.valueOf(novaSituacao),
                    secretaria,
                    "Estudante cumpriu todos os requisitos",
                    LocalDate.of(2025, 12, 1));
            ctx.repositorio.salvar(historico);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a situação do estudante é {string}")
    public void situacaoAtualizada(String situacaoEsperada) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(SituacaoDiscente.valueOf(situacaoEsperada), historico.getSituacaoDiscente());
    }

    @E("a trilha de auditoria registra a mudança com responsável e justificativa")
    public void trilhaAuditoriaRegistrada() {
        assertEquals(1, historico.getTrilhaAuditoria().size());
        var entrada = historico.getTrilhaAuditoria().get(0);
        assertEquals(SituacaoDiscente.ATIVO, entrada.getSituacaoAnterior());
        assertEquals(SituacaoDiscente.FORMANDO, entrada.getNovaSituacao());
        assertEquals(secretaria, entrada.getResponsavel());
        assertNotNull(entrada.getJustificativa());
    }

    @Quando("a secretaria tenta atualizar a situação sem informar justificativa")
    public void tentaAtualizarSemJustificativa() {
        try {
            historico.atualizarSituacaoDiscente(
                    SituacaoDiscente.FORMANDO,
                    secretaria,
                    "",
                    LocalDate.of(2025, 12, 1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
