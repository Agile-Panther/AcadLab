package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;

public class EncerrarOportunidadeFuncionalidade {

    private final EstagiosFuncionalidade ctx;
    private OportunidadeId oportunidadeId;
    private final SetorEstagiosId setorId = new SetorEstagiosId(1);

    public EncerrarOportunidadeFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma oportunidade publicada para encerramento")
    public void uma_oportunidade_publicada_para_encerramento() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
    }

    @Quando("o setor de estágios encerra a oportunidade por {string}")
    public void o_setor_encerra_a_oportunidade(String motivo) {
        ctx.servico.encerrarOportunidade(oportunidadeId, MotivoEncerramento.valueOf(motivo));
    }

    @Então("a oportunidade fica com status ENCERRADA")
    public void a_oportunidade_fica_encerrada() {
        var oportunidade = ctx.oportunidadeRepositorio.buscarPorId(oportunidadeId).orElseThrow();
        assertEquals(StatusOportunidade.ENCERRADA, oportunidade.getStatus());
    }

    @Dado("uma oportunidade ainda não publicada para encerramento")
    public void uma_oportunidade_nao_publicada_para_encerramento() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
    }

    @Quando("o setor de estágios tenta encerrar a oportunidade")
    public void o_setor_tenta_encerrar_a_oportunidade() {
        try {
            ctx.servico.encerrarOportunidade(oportunidadeId, MotivoEncerramento.DECISAO_ADMINISTRATIVA);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
