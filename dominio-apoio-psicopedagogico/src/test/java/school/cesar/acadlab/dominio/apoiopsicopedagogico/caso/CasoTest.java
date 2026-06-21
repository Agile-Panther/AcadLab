package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.agendamento.StatusAgendamento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.atendimento.Atendimento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.AgendamentoContestadoEvento;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.AgendamentoMarcadoEvento;
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
    void realizarTriagem_deveRegistrarTriagemDefinirResponsavelEIniciarAcompanhamento() {
        var caso = new Caso(casoId, estudanteId);
        var triagem = criarTriagem();

        var evento = caso.realizarTriagem(triagem);

        assertNotNull(evento);
        assertInstanceOf(TriagemRealizadaEvento.class, evento);
        assertEquals(triagem, caso.getTriagem());
        assertEquals(psicopedagogoId, caso.getResponsavelId());
        assertEquals(StatusCaso.EM_ATENDIMENTO, caso.getStatus());
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

    /* ===== Agendamento de horário e contestação ===== */

    private final LocalDateTime agora = LocalDateTime.of(2026, 6, 21, 12, 0);
    private final LocalDateTime horario = LocalDateTime.of(2026, 7, 20, 14, 0);
    private final LocalDateTime passado = LocalDateTime.of(2006, 1, 1, 10, 0);

    @Test
    void agendar_emCasoAtivo_deveMarcarHorarioComoAgendado() {
        var caso = new Caso(casoId, estudanteId);

        var evento = caso.agendar(horario, agora);

        assertInstanceOf(AgendamentoMarcadoEvento.class, evento);
        assertNotNull(caso.getAgendamento());
        assertEquals(horario, caso.getAgendamento().getDataHora());
        assertEquals(StatusAgendamento.AGENDADO, caso.getAgendamento().getStatus());
    }

    @Test
    void agendar_comDataHoraNula_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        assertThrows(NullPointerException.class, () -> caso.agendar(null, agora));
    }

    @Test
    void agendar_noPassado_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);

        var excecao = assertThrows(IllegalStateException.class, () -> caso.agendar(passado, agora));
        assertTrue(excecao.getMessage().toLowerCase().contains("futuro"));
        assertNull(caso.getAgendamento());
    }

    @Test
    void agendar_noMesmoInstante_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        assertThrows(IllegalStateException.class, () -> caso.agendar(agora, agora));
    }

    @Test
    void agendar_emCasoEncerrado_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        caso.registrarAtendimento(criarAtendimentoComConclusao());
        caso.encerrar();

        assertThrows(IllegalStateException.class, () -> caso.agendar(horario, agora));
    }

    @Test
    void contestarAgendamento_deveRegistrarContestacaoComJustificativaEHorarioSugerido() {
        var caso = new Caso(casoId, estudanteId);
        caso.agendar(horario, agora);
        var sugerido = LocalDateTime.of(2026, 7, 21, 10, 0);

        var evento = caso.contestarAgendamento("Tenho aula nesse horário", sugerido, agora);

        assertInstanceOf(AgendamentoContestadoEvento.class, evento);
        assertTrue(caso.getAgendamento().estaContestado());
        assertEquals(StatusAgendamento.CONTESTADO, caso.getAgendamento().getStatus());
        assertEquals("Tenho aula nesse horário", caso.getAgendamento().getJustificativaContestacao());
        assertEquals(sugerido, caso.getAgendamento().getHorarioSugerido());
    }

    @Test
    void contestarAgendamento_comHorarioSugeridoNoPassado_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        caso.agendar(horario, agora);

        var excecao = assertThrows(IllegalStateException.class,
                () -> caso.contestarAgendamento("prefiro antes", passado, agora));
        assertTrue(excecao.getMessage().toLowerCase().contains("futuro"));
    }

    @Test
    void contestarAgendamento_semHorarioAgendado_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);

        assertThrows(IllegalStateException.class,
                () -> caso.contestarAgendamento("qualquer motivo", null, agora));
    }

    @Test
    void contestarAgendamento_comJustificativaEmBranco_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        caso.agendar(horario, agora);

        assertThrows(IllegalArgumentException.class,
                () -> caso.contestarAgendamento("   ", null, agora));
    }

    @Test
    void contestarAgendamento_jaContestado_deveLancarExcecao() {
        var caso = new Caso(casoId, estudanteId);
        caso.agendar(horario, agora);
        caso.contestarAgendamento("primeiro motivo", null, agora);

        assertThrows(IllegalStateException.class,
                () -> caso.contestarAgendamento("segundo motivo", null, agora));
    }

    @Test
    void reagendar_aposContestacao_deveVoltarParaAgendadoLimpandoContestacao() {
        var caso = new Caso(casoId, estudanteId);
        caso.agendar(horario, agora);
        caso.contestarAgendamento("não posso", null, agora);
        var novoHorario = LocalDateTime.of(2026, 7, 22, 9, 0);

        caso.agendar(novoHorario, agora);

        assertEquals(StatusAgendamento.AGENDADO, caso.getAgendamento().getStatus());
        assertEquals(novoHorario, caso.getAgendamento().getDataHora());
        assertNull(caso.getAgendamento().getJustificativaContestacao());
    }

    @Test
    void reabrir_deveLimparAgendamento() {
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(criarTriagem());
        caso.agendar(horario, agora);
        caso.registrarAtendimento(criarAtendimentoComConclusao());
        caso.encerrar();

        caso.reabrir();

        assertNull(caso.getAgendamento());
    }
}
