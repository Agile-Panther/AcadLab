package school.cesar.acadlab.aplicacao.historicoacademico;

import java.time.LocalDate;

public record RetificacaoResumo(
        int id,
        int registroId,
        String situacaoAnterior,
        String novaSituacao,
        int responsavelId,
        String justificativa,
        LocalDate data) {
}
