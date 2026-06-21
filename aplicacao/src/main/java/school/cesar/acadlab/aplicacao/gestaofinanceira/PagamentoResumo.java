package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PagamentoResumo(BigDecimal valor, LocalDate data, String referencia, String status) {}
