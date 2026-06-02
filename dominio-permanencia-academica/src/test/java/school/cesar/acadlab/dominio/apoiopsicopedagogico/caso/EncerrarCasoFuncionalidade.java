package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

public class EncerrarCasoFuncionalidade extends ApoioPsicopedagogicoFuncionalidade {
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId psicopedagogoId = new PsicopedagogoId(1);
    private CasoId casoId;
    private RuntimeException excecao;

    private Caso criarCasoComTriagem() {
        casoId = repositorio.proximoId();
        var caso = new Caso(casoId, estudanteId);
        var triagem = new Triagem(PrioridadeTriagem.MEDIA, "Triagem", psicopedagogoId, LocalDate.now());
        caso.realizarTriagem(triagem);
        return caso;
    }

    @Dado("um caso psicopedagógico com atendimento de conclusão final registrado")
    public void um_caso_com_atendimento_conclusao() {
        var caso = criarCasoComTriagem();
        caso.registrarAtendimento(new Atendimento("Sessão final", null, true, LocalDate.now()));
        repositorio.salvar(caso);
    }

    @Dado("um caso psicopedagógico com atendimentos sem conclusão ou encaminhamento")
    public void um_caso_sem_conclusao_ou_encaminhamento() {
        var caso = criarCasoComTriagem();
        caso.registrarAtendimento(new Atendimento("Sessão parcial", null, false, LocalDate.now()));
        repositorio.salvar(caso);
    }

    @Dado("um caso psicopedagógico com atendimento de encaminhamento final registrado")
    public void um_caso_com_encaminhamento() {
        var caso = criarCasoComTriagem();
        caso.registrarAtendimento(new Atendimento("Sessão", "Encaminhado ao psiquiatra", false, LocalDate.now()));
        repositorio.salvar(caso);
    }

    @Quando("o psicopedagogo encerra o caso")
    public void o_psicopedagogo_encerra_o_caso() {
        try {
            atendimentoServico.encerrarCaso(casoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Quando("o psicopedagogo tenta encerrar o caso")
    public void o_psicopedagogo_tenta_encerrar_o_caso() {
        try {
            atendimentoServico.encerrarCaso(casoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema encerra o caso e atualiza o status para encerrado")
    public void o_sistema_encerra_o_caso() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var caso = repositorio.obter(casoId);
        assertEquals(StatusCaso.ENCERRADO, caso.getStatus());
    }

    @Entao("o sistema informa que é necessário registrar uma conclusão ou encaminhamento final antes de encerrar")
    public void o_sistema_informa_conclusao_obrigatoria() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
