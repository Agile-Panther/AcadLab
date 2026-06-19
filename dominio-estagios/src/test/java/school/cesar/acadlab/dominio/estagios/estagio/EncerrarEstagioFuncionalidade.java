package school.cesar.acadlab.dominio.estagios.estagio;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;

public class EncerrarEstagioFuncionalidade {

    private final EstagiosFuncionalidade ctx;

    public EncerrarEstagioFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estágio em andamento para o estudante de id {int}")
    public void um_estagio_em_andamento(int estudanteId) {
        ctx.criarEstagioEmAndamento(estudanteId);
    }

    @Quando("o estudante solicita o encerramento do estágio")
    public void o_estudante_solicita_encerramento() {
        ctx.servico.solicitarEncerramento(ctx.estagioId);
    }

    @Então("o estágio possui status ENCERRAMENTO_SOLICITADO")
    public void o_estagio_possui_status_encerramento_solicitado() {
        var estagio = ctx.estagioRepositorio.buscarPorId(ctx.estagioId).orElseThrow();
        assertEquals(StatusEstagio.ENCERRAMENTO_SOLICITADO, estagio.getStatus());
    }

    @Quando("o coordenador de id {int} homologa o encerramento")
    public void o_coordenador_homologa(int coordenadorId) {
        ctx.servico.homologarEncerramento(ctx.estagioId, new CoordenadorId(coordenadorId));
    }

    @Então("o estágio possui status ENCERRADO")
    public void o_estagio_possui_status_encerrado() {
        var estagio = ctx.estagioRepositorio.buscarPorId(ctx.estagioId).orElseThrow();
        assertEquals(StatusEstagio.ENCERRADO, estagio.getStatus());
    }

    @Dado("um estágio com encerramento já solicitado")
    public void um_estagio_com_encerramento_solicitado() {
        ctx.criarEstagioEmAndamento(20);
        ctx.servico.solicitarEncerramento(ctx.estagioId);
    }

    @Quando("o estudante tenta solicitar encerramento novamente")
    public void o_estudante_tenta_solicitar_encerramento_novamente() {
        try {
            ctx.servico.solicitarEncerramento(ctx.estagioId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o coordenador de id {int} tenta homologar sem solicitação")
    public void o_coordenador_tenta_homologar_sem_solicitacao(int coordenadorId) {
        try {
            ctx.servico.homologarEncerramento(ctx.estagioId, new CoordenadorId(coordenadorId));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
