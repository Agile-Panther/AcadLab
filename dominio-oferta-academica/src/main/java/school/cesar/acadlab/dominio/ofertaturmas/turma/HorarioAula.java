package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class HorarioAula {
    private final DayOfWeek diaSemana;
    private final LocalTime horaInicio;
    private final LocalTime horaFim;

    public HorarioAula(DayOfWeek diaSemana, LocalTime horaInicio, LocalTime horaFim) {
        notNull(diaSemana, "O dia da semana não pode ser nulo");
        notNull(horaInicio, "A hora de início não pode ser nula");
        notNull(horaFim, "A hora de fim não pode ser nula");
        if (!horaFim.isAfter(horaInicio)) {
            throw new IllegalArgumentException("A hora de fim deve ser posterior ao início");
        }
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }

    // RN6/RN7: detecta conflito de horário com outro horário
    public boolean conflitaCom(HorarioAula outro) {
        if (this.diaSemana != outro.diaSemana) return false;
        return !this.horaFim.isBefore(outro.horaInicio) && !this.horaInicio.isAfter(outro.horaFim);
    }

    public DayOfWeek getDiaSemana() { return diaSemana; }
    public LocalTime getHoraInicio() { return horaInicio; }
    public LocalTime getHoraFim() { return horaFim; }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof HorarioAula) {
            var h = (HorarioAula) obj;
            return diaSemana == h.diaSemana
                    && horaInicio.equals(h.horaInicio)
                    && horaFim.equals(h.horaFim);
        }
        return false;
    }

    @Override
    public int hashCode() { return Objects.hash(diaSemana, horaInicio, horaFim); }
}
