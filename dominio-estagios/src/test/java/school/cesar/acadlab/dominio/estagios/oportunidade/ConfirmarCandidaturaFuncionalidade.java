package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;
import school.cesar.acadlab.dominio.estagios.candidatura.StatusCandidatura;
import school.cesar.acadlab.dominio.estagios.estagio.StatusEstagio;

public class ConfirmarCandidaturaFuncionalidade {

    private final EstagiosFuncionalidade ctx;
    private OportunidadeId oportunidadeId;
    private final SetorEstagiosId setorId = new SetorEstagiosId(1);

    public ConfirmarCandidaturaFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma candidatura em análise para o estudante de id {int}")
    public void uma_candidatura_em_analise(int estudanteId) {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
        ctx.candidaturaId = ctx.servico.registrarCandidatura(oportunidadeId, new EstudanteId(estudanteId),
                LocalDate.now());
    }

    @Quando("o setor de estágios defere a candidatura")
    public void o_setor_defere_a_candidatura() {
        ctx.servico.deferir(ctx.candidaturaId);
    }

    @Quando("a empresa confirma a candidatura deferida")
    public void a_empresa_confirma_candidatura_deferida() {
        ctx.estagioId = ctx.servico.encaminharEConfirmar(ctx.candidaturaId, new EmpresaId(10));
    }

    @Então("o estágio é criado com status EM_ANDAMENTO")
    public void o_estagio_e_criado_em_andamento() {
        var estagio = ctx.estagioRepositorio.buscarPorId(ctx.estagioId).orElseThrow();
        assertEquals(StatusEstagio.EM_ANDAMENTO, estagio.getStatus());
    }

    @Quando("a empresa tenta confirmar candidatura não deferida")
    public void a_empresa_tenta_confirmar_nao_deferida() {
        try {
            ctx.servico.encaminharEConfirmar(ctx.candidaturaId, new EmpresaId(10));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("uma candidatura deferida para o estudante de id {int}")
    public void uma_candidatura_deferida(int estudanteId) {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
        ctx.candidaturaId = ctx.servico.registrarCandidatura(oportunidadeId, new EstudanteId(estudanteId),
                LocalDate.now());
        ctx.servico.deferir(ctx.candidaturaId);
    }

    @Então("a candidatura possui status DEFERIDA")
    public void a_candidatura_possui_status_deferida() {
        var candidatura = ctx.candidaturaRepositorio.buscarPorId(ctx.candidaturaId).orElseThrow();
        assertEquals(StatusCandidatura.DEFERIDA, candidatura.getStatus());
    }
}
