package school.cesar.acadlab.aplicacao.apoiopsicopedagogico;

import java.time.LocalDateTime;

public record AgendamentoResumo(LocalDateTime dataHora, String status,
                                 String justificativaContestacao, LocalDateTime horarioSugerido) {
}
