package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.gestaofinanceira.StatusPagamento;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Pagamento {
    private final BigDecimal valor;
    private final LocalDate dataPagamento;
    private final String referencia;
    private StatusPagamento status;

    public Pagamento(BigDecimal valor, LocalDate dataPagamento, String referencia) {
        notNull(valor, "valor obrigatório");
        isTrue(valor.compareTo(BigDecimal.ZERO) > 0, "valor deve ser positivo");
        notNull(dataPagamento, "dataPagamento obrigatória");
        notNull(referencia, "referencia obrigatória");
        isTrue(!referencia.isBlank(), "referencia não pode ser vazia");
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.referencia = referencia;
        this.status = StatusPagamento.CONFIRMADO;
    }

    public void cancelar(String justificativa, String responsavel) {
        notNull(justificativa, "justificativa obrigatória");
        notNull(responsavel, "responsavel obrigatório");
        if (status != StatusPagamento.CONFIRMADO)
            throw new IllegalStateException("pagamento já cancelado não pode ser cancelado novamente");
        this.status = StatusPagamento.CANCELADO;
    }

    public BigDecimal getValor() { return valor; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public String getReferencia() { return referencia; }
    public StatusPagamento getStatus() { return status; }
}
