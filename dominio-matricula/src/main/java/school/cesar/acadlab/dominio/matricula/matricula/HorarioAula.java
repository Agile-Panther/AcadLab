package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class HorarioAula {
    private final DayOfWeek dia;
    private final LocalTime inicio;
    private final LocalTime fim;

    public HorarioAula(DayOfWeek dia, LocalTime inicio, LocalTime fim) {
        notNull(dia, "O dia da semana não pode ser nulo");
        notNull(inicio, "O horário de início não pode ser nulo");
        notNull(fim, "O horário de fim não pode ser nulo");
        isTrue(inicio.isBefore(fim), "O horário de início deve ser anterior ao horário de fim");
        this.dia = dia;
        this.inicio = inicio;
        this.fim = fim;
    }

    public boolean conflitaCom(HorarioAula outro) {
        if (!this.dia.equals(outro.dia)) return false;
        return this.inicio.isBefore(outro.fim) && outro.inicio.isBefore(this.fim);
    }

    public DayOfWeek getDia() { return dia; }
    public LocalTime getInicio() { return inicio; }
    public LocalTime getFim() { return fim; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HorarioAula)) return false;
        HorarioAula h = (HorarioAula) o;
        return dia.equals(h.dia) && inicio.equals(h.inicio) && fim.equals(h.fim);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * dia.hashCode() + inicio.hashCode()) + fim.hashCode();
    }
}
