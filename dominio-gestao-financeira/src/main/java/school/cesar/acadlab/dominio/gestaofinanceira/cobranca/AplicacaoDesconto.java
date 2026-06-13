package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AplicacaoDesconto(BigDecimal percentual, String autorizacaoId, LocalDate dataAplicacao) {}
