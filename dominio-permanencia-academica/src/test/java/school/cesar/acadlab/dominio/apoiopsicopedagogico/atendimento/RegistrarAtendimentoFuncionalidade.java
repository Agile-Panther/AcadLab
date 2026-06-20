package school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.StatusCaso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

public class RegistrarAtendimentoFuncionalidade {
    private final ApoioPsicopedagogicoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId psicopedagogoId = new PsicopedagogoId(1);
    private CasoId casoId;
    private CasoId casoInexistenteId;

    public RegistrarAtendimentoFuncionalidade(ApoioPsicopedagogicoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um caso psicopedagógico com triagem realizada")
    public void um_caso_com_triagem() {
        casoId = ctx.repositorio.proximoId();
        var caso = new Caso(casoId, estudanteId);
        var triagem = new Triagem(PrioridadeTriagem.MEDIA, "Triagem realizada", psicopedagogoId, LocalDate.now());
        caso.realizarTriagem(triagem);
        ctx.repositorio.salvar(caso);
    }

    @Dado("um caso psicopedagógico que não existe no sistema")
    public void um_caso_inexistente() {
        casoInexistenteId = new CasoId(999);
    }

    @Quando("o psicopedagogo registra um atendimento no caso")
    public void o_psicopedagogo_registra_atendimento() {
        var atendimento = new Atendimento("Sessão realizada com êxito", null, false, LocalDate.now());
        ctx.atendimentoServico.registrarAtendimento(casoId, atendimento);
    }

    @Quando("o psicopedagogo tenta registrar um atendimento no caso inexistente")
    public void o_psicopedagogo_registra_atendimento_caso_inexistente() {
        try {
            var atendimento = new Atendimento("Sessão", null, false, LocalDate.now());
            ctx.atendimentoServico.registrarAtendimento(casoInexistenteId, atendimento);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra o atendimento e atualiza o status do caso")
    public void o_sistema_registra_atendimento() {
        var caso = ctx.repositorio.obter(casoId);
        assertEquals(1, caso.getAtendimentos().size());
        assertEquals(StatusCaso.EM_ATENDIMENTO, caso.getStatus());
    }

    @Entao("o sistema informa que o caso não foi encontrado")
    public void o_sistema_informa_caso_nao_encontrado() {
        assertNotNull(ctx.excecao);
    }
}
