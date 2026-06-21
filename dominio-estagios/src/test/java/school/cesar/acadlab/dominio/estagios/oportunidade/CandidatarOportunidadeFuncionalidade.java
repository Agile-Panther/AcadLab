package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.estagios.EstagiosFuncionalidade;

public class CandidatarOportunidadeFuncionalidade {

    private final EstagiosFuncionalidade ctx;
    private OportunidadeId oportunidadeId;
    private final SetorEstagiosId setorId = new SetorEstagiosId(1);

    public CandidatarOportunidadeFuncionalidade(EstagiosFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma oportunidade publicada")
    public void uma_oportunidade_publicada() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
    }

    @Quando("o estudante se candidata à oportunidade")
    public void o_estudante_se_candidata() {
        ctx.candidaturaId = ctx.servico.registrarCandidatura(oportunidadeId, new EstudanteId(20), LocalDate.now());
    }

    @Então("a candidatura é registrada com sucesso")
    public void a_candidatura_e_registrada_com_sucesso() {
        var candidatura = ctx.candidaturaRepositorio.buscarPorId(ctx.candidaturaId).orElseThrow();
        assertNotNull(candidatura);
    }

    @Dado("uma oportunidade ainda não publicada")
    public void uma_oportunidade_nao_publicada() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
    }

    @Quando("o estudante tenta se candidatar à oportunidade")
    public void o_estudante_tenta_se_candidatar() {
        try {
            ctx.servico.registrarCandidatura(oportunidadeId, new EstudanteId(20), LocalDate.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("uma oportunidade publicada com prazo de inscrição encerrado")
    public void uma_oportunidade_publicada_com_prazo_encerrado() {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
    }

    @Quando("o estudante tenta se candidatar à oportunidade fora do prazo")
    public void o_estudante_tenta_candidatar_fora_do_prazo() {
        try {
            var prazoEncerrado = LocalDate.now().minusDays(1);
            ctx.servico.registrarCandidaturaComPrazo(oportunidadeId, new EstudanteId(20),
                    prazoEncerrado, LocalDate.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("uma oportunidade publicada com critério de elegibilidade exigindo curso {string}")
    public void uma_oportunidade_publicada_com_criterio(String curso) {
        oportunidadeId = ctx.servico.cadastrarOportunidade(new EmpresaId(10), "Estágio em TI", 480);
        ctx.servico.definirCriterios(oportunidadeId, setorId,
                new CriterioElegibilidade(curso, 4, true));
        ctx.servico.publicarOportunidade(oportunidadeId, setorId);
    }

    @Quando("estudante que não atende aos critérios tenta se candidatar")
    public void estudante_que_nao_atende_tenta_candidatar() {
        try {
            var servicoRestritivo = new EstagioServico(
                    ctx.oportunidadeRepositorio, ctx.candidaturaRepositorio, ctx.estagioRepositorio,
                    (estudante, criterio) -> false);
            servicoRestritivo.registrarCandidatura(oportunidadeId, new EstudanteId(99), LocalDate.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
