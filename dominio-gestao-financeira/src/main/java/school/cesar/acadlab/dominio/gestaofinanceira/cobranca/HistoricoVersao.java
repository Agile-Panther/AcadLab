package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import java.math.BigDecimal;
import java.time.LocalDate;

public record HistoricoVersao(int versao, BigDecimal valorAnterior, String motivo, LocalDate dataAlteracao) {}
