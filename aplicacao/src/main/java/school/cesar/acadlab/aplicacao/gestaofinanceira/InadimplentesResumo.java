package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.math.BigDecimal;

public record InadimplentesResumo(
        int matriculaId,
        int estudanteId,
        BigDecimal valorEmAtraso,
        int diasAtraso,
        String statusMatricula
) {}
