package school.cesar.acadlab.dominio.apoiopsicopedagogico.agendamento;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;

/**
 * Horário vigente marcado para um atendimento psicopedagógico. Objeto de valor imutável:
 * a contestação do aluno produz uma nova instância em estado CONTESTADO.
 */
public class Agendamento {
    private final LocalDateTime dataHora;
    private final StatusAgendamento status;
    private final String justificativaContestacao;
    private final LocalDateTime horarioSugerido;

    public Agendamento(LocalDateTime dataHora) {
        this(dataHora, StatusAgendamento.AGENDADO, null, null);
    }

    public Agendamento(LocalDateTime dataHora, StatusAgendamento status,
                       String justificativaContestacao, LocalDateTime horarioSugerido) {
        notNull(dataHora, "A data e hora do agendamento não podem ser nulas");
        notNull(status, "O status do agendamento não pode ser nulo");
        this.dataHora = dataHora;
        this.status = status;
        this.justificativaContestacao = justificativaContestacao;
        this.horarioSugerido = horarioSugerido;
    }

    /**
     * Aluno contesta o horário, pedindo troca. Justificativa obrigatória; horário sugerido opcional.
     */
    public Agendamento contestar(String justificativa, LocalDateTime horarioSugerido) {
        notNull(justificativa, "A justificativa da contestação não pode ser nula");
        notBlank(justificativa, "A justificativa da contestação não pode estar em branco");
        return new Agendamento(dataHora, StatusAgendamento.CONTESTADO, justificativa, horarioSugerido);
    }

    public boolean estaContestado() {
        return status == StatusAgendamento.CONTESTADO;
    }

    public LocalDateTime getDataHora() { return dataHora; }
    public StatusAgendamento getStatus() { return status; }
    public String getJustificativaContestacao() { return justificativaContestacao; }
    public LocalDateTime getHorarioSugerido() { return horarioSugerido; }
}
