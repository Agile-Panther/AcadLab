package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeFuncionalidade;

public class CancelarMobilidadeFuncionalidade {

    private final MobilidadeFuncionalidade ctx;
    private MobilidadeAcademica mobilidade;

    public CancelarMobilidadeFuncionalidade(MobilidadeFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma mobilidade solicitada para estudante id {int} sem período iniciado")
    public void uma_mobilidade_solicitada_sem_periodo_iniciado(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Tokyo University");
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o estudante solicita cancelamento com justificativa {string} em {string}")
    public void o_estudante_solicita_cancelamento_com_justificativa_em(String justificativa, String dataStr) {
        LocalDate hoje = LocalDate.parse(dataStr);
        try {
            mobilidade.solicitarCancelamento(justificativa, hoje);
            ctx.repositorio.salvar(mobilidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @E("o coordenador confirma o cancelamento da mobilidade")
    public void o_coordenador_confirma_o_cancelamento_da_mobilidade() {
        mobilidade.confirmarCancelamento(new CoordenadorId(1));
        ctx.repositorio.salvar(mobilidade);
    }

    @Entao("a mobilidade tem status CANCELADA")
    public void a_mobilidade_tem_status_cancelada() {
        assertEquals(StatusMobilidade.CANCELADA, mobilidade.getStatus());
    }

    @Dado("uma mobilidade em andamento para estudante id {int} iniciada em {string}")
    public void uma_mobilidade_em_andamento_iniciada_em(int estudanteId, String dataStr) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "Seoul National University");
        mobilidade.autorizar(new CoordenadorId(1));
        mobilidade.iniciarPeriodoExterno(LocalDate.parse(dataStr));
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o estudante tenta cancelar a mobilidade em andamento em {string}")
    public void o_estudante_tenta_cancelar_mobilidade_em_andamento_em(String dataStr) {
        LocalDate hoje = LocalDate.parse(dataStr);
        try {
            mobilidade.solicitarCancelamento("Desistência", hoje);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("uma mobilidade solicitada para estudante id {int} sem justificativa de cancelamento")
    public void uma_mobilidade_solicitada_sem_justificativa_cancelamento(int estudanteId) {
        var mobilidadeId = ctx.repositorio.proximaMobilidadeId();
        mobilidade = new MobilidadeAcademica(mobilidadeId, new EstudanteId(estudanteId), "UCL");
        ctx.repositorio.salvar(mobilidade);
    }

    @Quando("o coordenador tenta confirmar cancelamento sem justificativa prévia")
    public void o_coordenador_tenta_confirmar_cancelamento_sem_justificativa() {
        try {
            mobilidade.confirmarCancelamento(new CoordenadorId(1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
