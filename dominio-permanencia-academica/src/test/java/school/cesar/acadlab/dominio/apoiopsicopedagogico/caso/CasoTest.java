package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.AtendimentoRegistradoEvento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.CasoEncerradoEvento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.CasoReabertoEvento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.TriagemRealizadaEvento;

class CasoTest {

    private final CasoId casoId = new CasoId(1);
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId psicopedagogoId = new PsicopedagogoId(1);

    private Triagem criarTriagem() {
        return new Triagem(PrioridadeTriagem.MEDIA, "Dificuldade de concentração", psicopedagogoId, LocalDate.now());
    }

    private Atendimento criarAtendimentoSemConclusao() {
        return new Atendimento("Sessão realizada", null, false, LocalDate.now());
    }

    private Atendimento criarAtendimentoComConclusao() {
        return new Atendimento("Sessão final", null, true, LocalDate.now());
    }

    private Atendimento criarAtendimentoComEncaminhamento() {
        return new Atendimento("Sessão realizada", "Encaminhado para especialista", false, LocalDate.now());
    }

    @Test
    void novoCaso_deveIniciarComStatusAberto() {
        var caso = new Caso(casoId, estudanteId);
        assertEquals(StatusCaso.ABERTO, caso.getStatus());
    }

    @Test
    void realizarTriagem_deveRegistrarTriagemEDefinirResponsavel() {
        var caso = new Caso(casoId, estudanteId);
        var triagem = criarTriagem();

        var evento = caso.realizarTriagem(triagem);

        assertNotNull(evento);
        assertInstanceOf(TriagemRealizadaEvento.class, evento);
        assertEquals(triagem, caso.getTriagem());
        assertEquals(psicopedagogoId, caso.getResponsavelId());
    }

    @Test
    void realizarTriagem_comTriagemNula_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        assertThrows(NullPointerException.class, () -> caso.realizarTriagem(null));
    }

    @Test
    void registrarAtendimento_semTriagemPrevia_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        var atendimento = criarAtendimentoSemConclusao();

        var excecao = assertThrows(IllegalStateException.class, () -> caso.registrarAtendimento(atendimento));
        assertNotNull(excecao.getMessage());
    }

    @Test
    void registrarAtendimento_comTriagemPrevia_deveAdicionarAtendimentoEAlterarStatus() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        var atendimento = criarAtendimentoSemConclusao();

        var evento = caso.registrarAtendimento(atendimento);

        assertNotNull(evento);
        assertInstanceOf(AtendimentoRegistradoEvento.class, evento);
        assertEquals(1, caso.getAtendimentos().size());
        assertEquals(StatusCaso.EM_ATENDIMENTO, caso.getStatus());
    }

    @Test
    void encerrar_semAtendimentoComConclusaoOuEncaminhamento_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        caso.registrarAtendimento(criarAtendimentoSemConclusao());

        var excecao = assertThrows(IllegalStateException.class, caso::encerrar);
        assertNotNull(excecao.getMessage());
    }

    @Test
    void encerrar_comAtendimentoComConclusaoFinal_deveAlterarStatusParaEncerrado() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        caso.registrarAtendimento(criarAtendimentoComConclusao());

        var evento = caso.encerrar();

        assertNotNull(evento);
        assertInstanceOf(CasoEncerradoEvento.class, evento);
        assertEquals(StatusCaso.ENCERRADO, caso.getStatus());
    }

    @Test
    void encerrar_comAtendimentoComEncaminhamento_deveAlterarStatusParaEncerrado() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        caso.registrarAtendimento(criarAtendimentoComEncaminhamento());

        var evento = caso.encerrar();

        assertNotNull(evento);
        assertEquals(StatusCaso.ENCERRADO, caso.getStatus());
    }

    @Test
    void reabrir_comCasoEncerrado_deveVoltarParaAbertoEResetarCampos() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        caso.registrarAtendimento(criarAtendimentoComConclusao());
        caso.encerrar();

        var evento = caso.reabrir();

        assertNotNull(evento);
        assertInstanceOf(CasoReabertoEvento.class, evento);
        assertEquals(StatusCaso.ABERTO, caso.getStatus());
        assertNull(caso.getTriagem());
        assertNull(caso.getResponsavelId());
        assertTrue(caso.getAtendimentos().isEmpty());
    }

    @Test
    void reabrir_comCasoAberto_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);

        assertThrows(IllegalStateException.class, caso::reabrir);
    }
}
