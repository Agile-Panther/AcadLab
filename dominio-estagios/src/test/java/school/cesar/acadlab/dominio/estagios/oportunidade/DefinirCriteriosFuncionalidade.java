package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;

public class DefinirCriteriosFuncionalidade {

    private final EstagiosFuncionalidade ctx;
    private OportunidadeId oportunidadeId;
    private final SetorEstagiosId setorId = new SetorEstagiosId(1);
    private final CriterioElegibilidade criterio = new CriterioElegibilidade("Sistemas de Informação", 4, true);

    public DefinirCriteriosFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma oportunidade cadastrada pronta para receber critérios")
    public void uma_oportunidade_pronta_para_criterios() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
    }

    @Quando("o setor de estágios define os critérios de elegibilidade")
    public void o_setor_define_criterios() {
        ctx.servico.definirCriterios(oportunidadeId, setorId, criterio);
    }

    @Então("os critérios são registrados com sucesso")
    public void os_criterios_sao_registrados() {
        var oportunidade = ctx.oportunidadeRepositorio.buscarPorId(oportunidadeId).orElseThrow();
        assertNotNull(oportunidade.getCriterioElegibilidade());
    }

    @Dado("uma oportunidade publicada com critérios definidos")
    public void uma_oportunidade_publicada_com_criterios() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.definirCriterios(oportunidadeId, setorId, criterio);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
    }

    @Quando("o setor tenta alterar os critérios de elegibilidade")
    public void o_setor_tenta_alterar_criterios() {
        try {
            ctx.servico.definirCriterios(oportunidadeId, setorId,
                    new CriterioElegibilidade("Ciência da Computação", 6, false));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
