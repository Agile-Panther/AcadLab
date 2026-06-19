package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;

public class PublicarOportunidadeFuncionalidade {

    private final EstagiosFuncionalidade ctx;
    private OportunidadeId oportunidadeId;
    private final SetorEstagiosId setorId = new SetorEstagiosId(1);

    public PublicarOportunidadeFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma oportunidade cadastrada pela empresa")
    public void uma_oportunidade_cadastrada() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
    }

    @Quando("o setor de estágios publica a oportunidade")
    public void o_setor_publica_a_oportunidade() {
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
    }

    @Então("a oportunidade fica visível para os estudantes")
    public void a_oportunidade_fica_visivel() {
        var oportunidade = ctx.oportunidadeRepositorio.buscarPorId(oportunidadeId).orElseThrow();
        assertEquals(StatusOportunidade.PUBLICADA, oportunidade.getStatus());
    }

    @Quando("a empresa tenta publicar a oportunidade diretamente")
    public void a_empresa_tenta_publicar_diretamente() {
        try {
            ctx.oportunidadeRepositorio.buscarPorId(oportunidadeId).orElseThrow()
                    .publicar(null);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o setor tenta publicar a oportunidade novamente")
    public void o_setor_tenta_publicar_novamente() {
        try {
            ctx.servico.publicarOportunidade(oportunidadeId, setorId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
