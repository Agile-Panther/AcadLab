package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

public class AgendamentoServico {
    private final CasoRepositorio casoRepositorio;
    private final EventoBarramento eventoBarramento;

    public AgendamentoServico(CasoRepositorio casoRepositorio, EventoBarramento eventoBarramento) {
        notNull(casoRepositorio, "O repositório de casos não pode ser nulo");
        notNull(eventoBarramento, "O barramento de eventos não pode ser nulo");
        this.casoRepositorio = casoRepositorio;
        this.eventoBarramento = eventoBarramento;
    }

    // Psicopedagogo marca ou reagenda o horário do atendimento
    public void agendar(CasoId casoId, LocalDateTime dataHora, LocalDateTime agora) {
        notNull(casoId, "O id do caso não pode ser nulo");
        notNull(dataHora, "A data e hora do agendamento não podem ser nulas");
        notNull(agora, "O instante de referência não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        var evento = caso.agendar(dataHora, agora);
        casoRepositorio.salvar(caso);
        eventoBarramento.postar(evento);
    }

    // Aluno contesta o horário e solicita troca
    public void contestar(CasoId casoId, String justificativa, LocalDateTime horarioSugerido, LocalDateTime agora) {
        notNull(casoId, "O id do caso não pode ser nulo");
        notNull(agora, "O instante de referência não pode ser nulo");

        var caso = casoRepositorio.obter(casoId);
        var evento = caso.contestarAgendamento(justificativa, horarioSugerido, agora);
        casoRepositorio.salvar(caso);
        eventoBarramento.postar(evento);
    }
}
