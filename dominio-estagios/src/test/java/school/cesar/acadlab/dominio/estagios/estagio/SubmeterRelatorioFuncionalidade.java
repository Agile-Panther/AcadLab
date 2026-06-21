package school.cesar.acadlab.dominio.estagios.estagio;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;

public class SubmeterRelatorioFuncionalidade {

    private final EstagiosFuncionalidade ctx;

    public SubmeterRelatorioFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estágio ativo para o estudante de id {int}")
    public void um_estagio_ativo(int estudanteId) {
        ctx.criarEstagioEmAndamento(estudanteId);
    }

    @Quando("o estudante submete o relatório número {int} com descrição {string}")
    public void o_estudante_submete_relatorio(int numero, String descricao) {
        ctx.servico.submeterRelatorio(ctx.estagioId, numero, descricao);
    }

    @Então("o estágio possui {int} relatório com status PENDENTE")
    public void o_estagio_possui_relatorio_pendente(int quantidade) {
        var estagio = ctx.estagioRepositorio.buscarPorId(ctx.estagioId).orElseThrow();
        assertEquals(quantidade, estagio.getRelatorios().size());
        assertEquals(StatusRelatorio.PENDENTE, estagio.getRelatorios().get(0).getStatus());
    }

    @Dado("um estágio com relatório número {int} já submetido")
    public void um_estagio_com_relatorio_submetido(int numero) {
        ctx.criarEstagioEmAndamento(20);
        ctx.servico.submeterRelatorio(ctx.estagioId, numero, "Primeiro relatório");
    }

    @Quando("o estudante tenta submeter novamente o relatório número {int}")
    public void o_estudante_tenta_submeter_duplicado(int numero) {
        try {
            ctx.servico.submeterRelatorio(ctx.estagioId, numero, "Tentativa duplicada");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("um estágio com relatório número {int} pendente")
    public void um_estagio_com_relatorio_pendente(int numero) {
        ctx.criarEstagioEmAndamento(20);
        ctx.servico.submeterRelatorio(ctx.estagioId, numero, "Relatório mensal");
    }

    @Quando("o coordenador aprova o relatório número {int}")
    public void o_coordenador_aprova_relatorio(int numero) {
        ctx.servico.avaliarRelatorio(ctx.estagioId, numero, StatusRelatorio.APROVADO);
    }

    @Então("o relatório número {int} possui status APROVADO")
    public void o_relatorio_possui_status_aprovado(int numero) {
        var estagio = ctx.estagioRepositorio.buscarPorId(ctx.estagioId).orElseThrow();
        var relatorio = estagio.getRelatorios().stream()
                .filter(r -> r.getNumero() == numero)
                .findFirst().orElseThrow();
        assertEquals(StatusRelatorio.APROVADO, relatorio.getStatus());
    }
}
