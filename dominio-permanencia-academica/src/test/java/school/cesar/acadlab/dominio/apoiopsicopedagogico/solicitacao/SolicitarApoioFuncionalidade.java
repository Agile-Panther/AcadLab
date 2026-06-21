package school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.StatusCaso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

public class SolicitarApoioFuncionalidade {
    private final ApoioPsicopedagogicoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId psicopedagogoId = new PsicopedagogoId(1);

    public SolicitarApoioFuncionalidade(ApoioPsicopedagogicoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estudante sem caso psicopedagógico ativo")
    public void um_estudante_sem_caso_ativo() {
        // repositório começa vazio
    }

    @Dado("um estudante com caso psicopedagógico ativo")
    public void um_estudante_com_caso_ativo() {
        var caso = new Caso(ctx.repositorio.proximoId(), estudanteId);
        ctx.repositorio.salvar(caso);
    }

    @Dado("um estudante com caso psicopedagógico encerrado")
    public void um_estudante_com_caso_encerrado() {
        var casoId = ctx.repositorio.proximoId();
        var caso = new Caso(casoId, estudanteId);
        var triagem = new Triagem(PrioridadeTriagem.MEDIA, "Triagem inicial", psicopedagogoId, LocalDate.now());
        caso.realizarTriagem(triagem);
        var atendimento = new Atendimento("Sessão final", null, true, LocalDate.now());
        caso.registrarAtendimento(atendimento);
        caso.encerrar();
        ctx.repositorio.salvar(caso);
    }

    @Quando("o estudante solicita apoio psicopedagógico")
    public void o_estudante_solicita_apoio() {
        try {
            ctx.apoioServico.solicitar(estudanteId, "Tenho dificuldades com concentração");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra a solicitação com sucesso")
    public void o_sistema_registra_solicitacao() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
    }

    @Entao("um caso psicopedagógico é aberto para o estudante")
    public void um_caso_e_aberto() {
        var caso = ctx.repositorio.pesquisarCasoAbertoPorEstudante(estudanteId);
        assertTrue(caso.isPresent());
        assertEquals(StatusCaso.ABERTO, caso.get().getStatus());
    }

    @Entao("o sistema informa que o estudante já possui um caso ativo")
    public void o_sistema_informa_caso_ativo() {
        assertNotNull(ctx.excecao);
        assertInstanceOf(IllegalStateException.class, ctx.excecao);
    }

    @Entao("o sistema reabre o caso psicopedagógico do estudante")
    public void o_sistema_reabre_o_caso() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        var caso = ctx.repositorio.pesquisarCasoAbertoPorEstudante(estudanteId);
        assertTrue(caso.isPresent());
        assertEquals(StatusCaso.ABERTO, caso.get().getStatus());
    }
}
