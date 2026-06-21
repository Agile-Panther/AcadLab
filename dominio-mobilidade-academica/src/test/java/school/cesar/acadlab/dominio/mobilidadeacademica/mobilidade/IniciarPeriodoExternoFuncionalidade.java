package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class IniciarPeriodoExternoFuncionalidade {

    private final MobilidadeFuncionalidade ctx;
    private MobilidadeAcademica mobilidade;

    public IniciarPeriodoExternoFuncionalidade(MobilidadeFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma mobilidade autorizada para iniciar período do estudante id {int}")
    public void uma_mobilidade_autorizada_para_iniciar_periodo(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Universidade de Lisboa");
        mobilidade.autorizar(new CoordenadorId(1));
        ctx.repositorio.salvar(mobilidade);
    }

    @Dado("uma mobilidade apenas solicitada do estudante id {int}")
    public void uma_mobilidade_apenas_solicitada(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Universidade do Porto");
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o período externo é iniciado em {string}")
    public void o_periodo_externo_e_iniciado_em(String dataStr) {
        try {
            mobilidade.iniciarPeriodoExterno(LocalDate.parse(dataStr));
            ctx.repositorio.salvar(mobilidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a mobilidade tem status EM_ANDAMENTO")
    public void a_mobilidade_tem_status_em_andamento() {
        assertEquals(StatusMobilidade.EM_ANDAMENTO, mobilidade.getStatus());
    }
}
