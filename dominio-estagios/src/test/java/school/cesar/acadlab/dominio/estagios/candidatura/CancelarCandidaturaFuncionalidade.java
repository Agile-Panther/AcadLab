package school.cesar.acadlab.dominio.estagios.candidatura;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.SetorEstagiosId;

public class CancelarCandidaturaFuncionalidade {

    private final EstagiosFuncionalidade ctx;
    private final SetorEstagiosId setorId = new SetorEstagiosId(1);

    public CancelarCandidaturaFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma candidatura em análise")
    public void uma_candidatura_em_analise() {
        var oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
        ctx.candidaturaId = ctx.servico.registrarCandidatura(oportunidadeId, new EstudanteId(20), LocalDate.now());
    }

    @Quando("o estudante cancela a candidatura")
    public void o_estudante_cancela_a_candidatura() {
        ctx.servico.cancelarCandidatura(ctx.candidaturaId);
    }

    @Então("a candidatura é cancelada com sucesso")
    public void a_candidatura_e_cancelada() {
        var candidatura = ctx.candidaturaRepositorio.buscarPorId(ctx.candidaturaId).orElseThrow();
        assertEquals(StatusCandidatura.CANCELADA, candidatura.getStatus());
    }

    @Dado("uma candidatura já deferida")
    public void uma_candidatura_ja_deferida() {
        var oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
        ctx.candidaturaId = ctx.servico.registrarCandidatura(oportunidadeId, new EstudanteId(20), LocalDate.now());
        ctx.servico.deferir(ctx.candidaturaId);
    }

    @Quando("o estudante tenta cancelar a candidatura")
    public void o_estudante_tenta_cancelar_a_candidatura() {
        try {
            ctx.servico.cancelarCandidatura(ctx.candidaturaId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
