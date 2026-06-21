package school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.StatusCaso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;

public class RealizarTriagemFuncionalidade {
    private final ApoioPsicopedagogicoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId psicopedagogoId = new PsicopedagogoId(1);
    private CasoId casoId;

    public RealizarTriagemFuncionalidade(ApoioPsicopedagogicoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um caso psicopedagógico aberto sem triagem")
    public void um_caso_aberto_sem_triagem() {
        casoId = ctx.repositorio.proximoId();
        var caso = new Caso(casoId, estudanteId);
        ctx.repositorio.salvar(caso);
    }

    @Quando("o psicopedagogo realiza a triagem do caso")
    public void o_psicopedagogo_realiza_triagem() {
        var triagem = new Triagem(PrioridadeTriagem.ALTA, "Dificuldades acadêmicas severas", psicopedagogoId, LocalDate.now());
        ctx.triagemServico.realizarTriagem(casoId, triagem);
    }

    @Quando("o psicopedagogo tenta registrar um atendimento sem realizar triagem")
    public void o_psicopedagogo_tenta_atendimento_sem_triagem() {
        try {
            var atendimento = new Atendimento("Observações", null, false, LocalDate.now());
            ctx.atendimentoServico.registrarAtendimento(casoId, atendimento);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra a triagem no caso")
    public void o_sistema_registra_triagem() {
        var caso = ctx.repositorio.obter(casoId);
        assertNotNull(caso.getTriagem());
        assertEquals(PrioridadeTriagem.ALTA, caso.getTriagem().getPrioridade());
    }

    @Entao("o caso passa para acompanhamento")
    public void o_caso_passa_para_acompanhamento() {
        assertEquals(StatusCaso.EM_ATENDIMENTO, ctx.repositorio.obter(casoId).getStatus());
    }

    @Entao("o sistema informa que o caso precisa passar por triagem antes do atendimento")
    public void o_sistema_informa_triagem_obrigatoria() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }
}
