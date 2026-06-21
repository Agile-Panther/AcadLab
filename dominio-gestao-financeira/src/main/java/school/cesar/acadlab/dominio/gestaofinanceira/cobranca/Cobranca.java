package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Cobranca {
    private final CobrancaId id;
    private final ContratoId contratoId;
    private final EstudanteId estudanteId;
    private final PeriodoLetivoId periodoLetivoId;
    private final BigDecimal valorBase;
    private BigDecimal valorAtual;
    private final LocalDate vencimento;
    private int versao;
    private StatusCobranca status;
    private final List<HistoricoVersao> historico = new ArrayList<>();
    private Pagamento pagamento;
    private Contestacao contestacao;
    private final List<AplicacaoDesconto> descontos = new ArrayList<>();

    public Cobranca(CobrancaId id, ContratoId contratoId, EstudanteId estudanteId,
            PeriodoLetivoId periodoLetivoId, BigDecimal valorBase, LocalDate vencimento) {
        notNull(id, "id obrigatório");
        notNull(contratoId, "contratoId obrigatório");
        notNull(estudanteId, "estudanteId obrigatório");
        notNull(periodoLetivoId, "periodoLetivoId obrigatório");
        notNull(valorBase, "valorBase obrigatório");
        isTrue(valorBase.compareTo(BigDecimal.ZERO) > 0, "valorBase deve ser positivo");
        notNull(vencimento, "vencimento obrigatório");
        this.id = id;
        this.contratoId = contratoId;
        this.estudanteId = estudanteId;
        this.periodoLetivoId = periodoLetivoId;
        this.valorBase = valorBase;
        this.valorAtual = valorBase;
        this.vencimento = vencimento;
        this.versao = 1;
        this.status = StatusCobranca.ABERTA;
    }

    public ContestacaoRegistradaEvento contestar(EstudanteId requerente, String justificativa, LocalDate data) {
        notNull(requerente, "requerente obrigatório");
        if (!this.estudanteId.equals(requerente))
            throw new IllegalStateException("estudante não é titular desta cobrança");
        if (contestacao != null && contestacao.getStatus() == StatusContestacao.PENDENTE)
            throw new IllegalStateException("já existe uma contestação em aberto para esta cobrança");
        this.contestacao = new Contestacao(requerente, justificativa, data);
        this.status = StatusCobranca.CONTESTADA;
        return new ContestacaoRegistradaEvento(this);
    }

    public ContestacaoResolvidaEvento resolverContestacao(String parecer) {
        if (contestacao == null)
            throw new IllegalStateException("não há contestação registrada");
        contestacao.resolver(parecer);
        this.status = StatusCobranca.ABERTA;
        return new ContestacaoResolvidaEvento(this);
    }

    public DescontoAplicadoEvento aplicarDesconto(BigDecimal percentual, String autorizacaoId, LocalDate data) {
        notNull(percentual, "percentual obrigatório");
        isTrue(percentual.compareTo(BigDecimal.ZERO) > 0 && percentual.compareTo(new BigDecimal("100")) < 0,
                "percentual deve estar entre 0 e 100");
        var fator = BigDecimal.ONE.subtract(percentual.divide(new BigDecimal("100"), MathContext.DECIMAL64));
        this.valorAtual = this.valorAtual.multiply(fator).setScale(2, java.math.RoundingMode.HALF_UP);
        this.descontos.add(new AplicacaoDesconto(percentual, autorizacaoId, data));
        return new DescontoAplicadoEvento(this);
    }

    public PagamentoRegistradoEvento registrarPagamento(BigDecimal valor, LocalDate data, String referencia) {
        if (status == StatusCobranca.CANCELADA || status == StatusCobranca.PAGA)
            throw new IllegalStateException("pagamento não permitido para cobrança com status " + status);
        this.pagamento = new Pagamento(valor, data, referencia);
        this.status = StatusCobranca.PAGA;
        return new PagamentoRegistradoEvento(this);
    }

    public PagamentoCanceladoEvento cancelarPagamento(String justificativa, String responsavel) {
        if (pagamento == null)
            throw new IllegalStateException("não há pagamento registrado");
        pagamento.cancelar(justificativa, responsavel);
        this.status = StatusCobranca.ABERTA;
        return new PagamentoCanceladoEvento(this);
    }

    public NovaVersaoGeradaEvento gerarNovaVersao(String motivo, BigDecimal novoValor) {
        notNull(motivo, "motivo obrigatório");
        isTrue(novoValor.compareTo(BigDecimal.ZERO) > 0, "novoValor deve ser positivo");
        this.historico.add(new HistoricoVersao(this.versao, this.valorAtual, motivo, LocalDate.now()));
        this.versao++;
        this.valorAtual = novoValor;
        return new NovaVersaoGeradaEvento(this);
    }

    public static Cobranca reconstituir(CobrancaId id, ContratoId contratoId, EstudanteId estudanteId,
            PeriodoLetivoId periodoLetivoId, BigDecimal valorBase, BigDecimal valorAtual,
            LocalDate vencimento, int versao, StatusCobranca status,
            Pagamento pagamento, Contestacao contestacao,
            List<HistoricoVersao> historico, List<AplicacaoDesconto> descontos) {
        var c = new Cobranca(id, contratoId, estudanteId, periodoLetivoId, valorBase, vencimento);
        c.valorAtual = valorAtual;
        c.versao = versao;
        c.status = status;
        c.pagamento = pagamento;
        c.contestacao = contestacao;
        c.historico.addAll(historico);
        c.descontos.addAll(descontos);
        return c;
    }

    public CobrancaCanceladaEvento cancelar(String motivo) {
        if (status == StatusCobranca.PAGA)
            throw new IllegalStateException("não é possível cancelar uma cobrança já paga");
        this.status = StatusCobranca.CANCELADA;
        return new CobrancaCanceladaEvento(this);
    }

    public CobrancaId getId() { return id; }
    public ContratoId getContratoId() { return contratoId; }
    public EstudanteId getEstudanteId() { return estudanteId; }
    public PeriodoLetivoId getPeriodoLetivoId() { return periodoLetivoId; }
    public BigDecimal getValorBase() { return valorBase; }
    public BigDecimal getValorAtual() { return valorAtual; }
    public LocalDate getVencimento() { return vencimento; }
    public int getVersao() { return versao; }
    public StatusCobranca getStatus() { return status; }
    public List<HistoricoVersao> getHistorico() { return Collections.unmodifiableList(historico); }
    public Pagamento getPagamento() { return pagamento; }
    public Contestacao getContestacao() { return contestacao; }
    public List<AplicacaoDesconto> getDescontos() { return Collections.unmodifiableList(descontos); }

    public abstract static class CobrancaEvento {
        private final Cobranca cobranca;
        protected CobrancaEvento(Cobranca cobranca) { this.cobranca = cobranca; }
        public Cobranca getCobranca() { return cobranca; }
    }
    public static class ContestacaoRegistradaEvento extends CobrancaEvento {
        private ContestacaoRegistradaEvento(Cobranca c) { super(c); }
    }
    public static class ContestacaoResolvidaEvento extends CobrancaEvento {
        private ContestacaoResolvidaEvento(Cobranca c) { super(c); }
    }
    public static class DescontoAplicadoEvento extends CobrancaEvento {
        private DescontoAplicadoEvento(Cobranca c) { super(c); }
    }
    public static class PagamentoRegistradoEvento extends CobrancaEvento {
        private PagamentoRegistradoEvento(Cobranca c) { super(c); }
    }
    public static class PagamentoCanceladoEvento extends CobrancaEvento {
        private PagamentoCanceladoEvento(Cobranca c) { super(c); }
    }
    public static class NovaVersaoGeradaEvento extends CobrancaEvento {
        private NovaVersaoGeradaEvento(Cobranca c) { super(c); }
    }
    public static class CobrancaCanceladaEvento extends CobrancaEvento {
        private CobrancaCanceladaEvento(Cobranca c) { super(c); }
    }
}
