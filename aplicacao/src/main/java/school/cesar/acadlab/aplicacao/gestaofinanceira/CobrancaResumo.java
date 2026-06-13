package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CobrancaResumo(
        int id,
        int contratoId,
        int estudanteId,
        int periodoLetivoId,
        BigDecimal valorBase,
        BigDecimal valorAtual,
        LocalDate vencimento,
        int versao,
        String status) {}
