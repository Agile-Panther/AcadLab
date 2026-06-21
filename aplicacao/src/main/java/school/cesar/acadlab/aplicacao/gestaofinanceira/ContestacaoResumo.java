package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.time.LocalDate;

public record ContestacaoResumo(Integer requerenteId, String justificativa, LocalDate data,
        String status, String parecer) {}
