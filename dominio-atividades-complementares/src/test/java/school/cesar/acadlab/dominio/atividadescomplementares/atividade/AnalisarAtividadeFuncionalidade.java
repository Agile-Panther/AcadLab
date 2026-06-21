package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;

public class AnalisarAtividadeFuncionalidade {
    private final AtividadesComplementaresFuncionalidade ctx;
    private AtividadeComplementarId atividadeId;

    public AnalisarAtividadeFuncionalidade(AtividadesComplementaresFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma atividade complementar pendente cadastrada para análise")
    public void atividadePendenteCadastrada() {
        ctx.verificadorVinculo.setVinculo(true);
        var atividade = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-ANALISE", "Curso para análise");
        atividadeId = atividade.getId();
    }

    @Dado("o deferimento não excede o limite da categoria")
    public void deferimentoNaoExcedeLimite() {
        ctx.verificadorLimite.setExcede(false);
    }

    @Dado("o deferimento excede o limite da categoria")
    public void deferimentoExcedeLimite() {
        ctx.verificadorLimite.setExcede(true);
    }

    @Dado("uma atividade complementar em revisão solicitada")
    public void atividadeEmRevisaoSolicitada() {
        ctx.verificadorVinculo.setVinculo(true);
        ctx.verificadorLimite.setExcede(false);
        var atividade = ctx.servico.submeter(new EstudanteId(1), new CategoriaAtividadeId(1),
                40, LocalDate.of(2025, 3, 15), "CERT-REVISAO-ANALISE", "Curso em revisão");
        atividadeId = atividade.getId();
        ctx.servico.indeferir(atividadeId, "Documentação insuficiente");
        ctx.servico.solicitarRevisao(atividadeId, "Nova documentação enviada");
    }

    @Quando("o coordenador defere a atividade com {int} horas aprovadas")
    public void coordenadorDefereAtividade(int horas) {
        ctx.servico.deferir(atividadeId, horas);
    }

    @Quando("o coordenador tenta deferir a atividade com {int} horas")
    public void coordenadorTentaDeferirAtividade(int horas) {
        try {
            ctx.servico.deferir(atividadeId, horas);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o coordenador indefere a atividade com justificativa {string}")
    public void coordenadorIndefereAtividade(String justificativa) {
        ctx.servico.indeferir(atividadeId, justificativa);
    }

    @Quando("o coordenador tenta indeferir a atividade com justificativa {string}")
    public void coordenadorTentaIndefereAtividade(String justificativa) {
        try {
            ctx.servico.indeferir(atividadeId, justificativa);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a atividade deve ter status DEFERIDA com {int} horas aprovadas")
    public void atividadeDeveTerStatusDeferida(int horas) {
        var atividade = ctx.repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.DEFERIDA, atividade.getStatus());
        Assertions.assertEquals(horas, atividade.getHorasAprovadas());
    }

    @Entao("a atividade deve ter status INDEFERIDA")
    public void atividadeDeveTerStatusIndeferida() {
        var atividade = ctx.repositorio.obter(atividadeId);
        Assertions.assertEquals(StatusAtividade.INDEFERIDA, atividade.getStatus());
    }
}
