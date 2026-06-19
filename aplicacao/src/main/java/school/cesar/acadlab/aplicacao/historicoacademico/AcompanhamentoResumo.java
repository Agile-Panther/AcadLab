package school.cesar.acadlab.aplicacao.historicoacademico;

import java.time.LocalDate;

public record AcompanhamentoResumo(
        int id,
        String observacao,
        LocalDate data) {
}
