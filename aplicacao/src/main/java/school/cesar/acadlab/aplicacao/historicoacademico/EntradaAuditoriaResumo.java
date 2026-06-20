package school.cesar.acadlab.aplicacao.historicoacademico;

import java.time.LocalDate;

public record EntradaAuditoriaResumo(
        String situacaoAnterior,
        String novaSituacao,
        int responsavelId,
        String justificativa,
        LocalDate data) {
}
