package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BolsaResumo(int id, int estudanteId, String tipo, BigDecimal percentual,
        LocalDate validade, String status) {}
