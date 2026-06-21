package school.cesar.acadlab.dominio.apoiopsicopedagogico.agendamento;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

public class AgendarHorarioFuncionalidade {
    private final ApoioPsicopedagogicoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId psicopedagogoId = new PsicopedagogoId(1);
    private final LocalDateTime agora = LocalDateTime.of(2026, 6, 21, 12, 0);
    private final LocalDateTime horario = LocalDateTime.of(2026, 7, 20, 14, 0);
    private CasoId casoId;

    public AgendarHorarioFuncionalidade(ApoioPsicopedagogicoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private void criarCasoEmAcompanhamento() {
        casoId = ctx.repositorio.proximoId();
        var caso = new Caso(casoId, estudanteId);
        caso.realizarTriagem(new Triagem(PrioridadeTriagem.MEDIA, "Ansiedade", psicopedagogoId, LocalDate.now()));
        ctx.repositorio.salvar(caso);
    }

    @Dado("um caso psicopedagógico em acompanhamento")
    public void um_caso_em_acompanhamento() {
        criarCasoEmAcompanhamento();
    }

    @Dado("um caso psicopedagógico com horário agendado")
    public void um_caso_com_horario_agendado() {
        criarCasoEmAcompanhamento();
        ctx.agendamentoServico.agendar(casoId, horario, agora);
    }

    @Dado("um caso psicopedagógico com horário contestado pelo aluno")
    public void um_caso_com_horario_contestado() {
        criarCasoEmAcompanhamento();
        ctx.agendamentoServico.agendar(casoId, horario, agora);
        ctx.agendamentoServico.contestar(casoId, "Conflito com aula", LocalDateTime.of(2026, 7, 21, 10, 0), agora);
    }

    @Quando("o psicopedagogo marca um horário de atendimento")
    public void o_psicopedagogo_marca_horario() {
        ctx.agendamentoServico.agendar(casoId, horario, agora);
    }

    @Quando("o psicopedagogo tenta marcar um horário no passado")
    public void o_psicopedagogo_tenta_marcar_no_passado() {
        try {
            ctx.agendamentoServico.agendar(casoId, LocalDateTime.of(2006, 1, 1, 10, 0), agora);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o aluno contesta o horário sugerindo outro")
    public void o_aluno_contesta_horario() {
        ctx.agendamentoServico.contestar(casoId, "Tenho aula nesse horário", LocalDateTime.of(2026, 7, 21, 10, 0), agora);
    }

    @Quando("o psicopedagogo reagenda um novo horário")
    public void o_psicopedagogo_reagenda() {
        ctx.agendamentoServico.agendar(casoId, LocalDateTime.of(2026, 7, 22, 9, 0), agora);
    }

    @Quando("o aluno tenta contestar um horário inexistente")
    public void o_aluno_tenta_contestar_inexistente() {
        try {
            ctx.agendamentoServico.contestar(casoId, "qualquer motivo", null, agora);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra o horário como agendado")
    public void o_sistema_registra_agendado() {
        var agendamento = ctx.repositorio.obter(casoId).getAgendamento();
        assertNotNull(agendamento);
        assertEquals(StatusAgendamento.AGENDADO, agendamento.getStatus());
    }

    @Entao("o sistema registra o horário como contestado com a justificativa do aluno")
    public void o_sistema_registra_contestado() {
        var agendamento = ctx.repositorio.obter(casoId).getAgendamento();
        assertNotNull(agendamento);
        assertEquals(StatusAgendamento.CONTESTADO, agendamento.getStatus());
        assertNotNull(agendamento.getJustificativaContestacao());
        assertNotNull(agendamento.getHorarioSugerido());
    }

    @Entao("nenhum horário é registrado no caso")
    public void nenhum_horario_registrado() {
        assertNull(ctx.repositorio.obter(casoId).getAgendamento());
    }
}
