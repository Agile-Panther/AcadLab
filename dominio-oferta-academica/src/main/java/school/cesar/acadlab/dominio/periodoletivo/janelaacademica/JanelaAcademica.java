package school.cesar.acadlab.dominio.periodoletivo.janelaacademica;

import static org.apache.commons.lang3.Validate.notNull;
import java.time.LocalDate;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;

public class JanelaAcademica {
    private final TipoJanela tipo;
    private final LocalDate inicio;
    private final LocalDate fim;

    public JanelaAcademica(TipoJanela tipo, LocalDate inicio, LocalDate fim) {
        notNull(tipo, "O tipo da janela não pode ser nulo");
        notNull(inicio, "A data de início não pode ser nula");
        notNull(fim, "A data de fim não pode ser nula");
        if (!fim.isAfter(inicio)) {
            throw new IllegalArgumentException("A data de fim deve ser posterior ao início");
        }
        this.tipo = tipo;
        this.inicio = inicio;
        this.fim = fim;
    }

    // RN2: verifica se a janela está ativa na data informada
    public boolean estaAtiva(LocalDate data) {
        notNull(data, "A data não pode ser nula");
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }

    public TipoJanela getTipo() { return tipo; }
    public LocalDate getInicio() { return inicio; }
    public LocalDate getFim() { return fim; }
}
