package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DescontoResumo(BigDecimal percentual, String autorizacaoId, LocalDate dataAplicacao) {}
