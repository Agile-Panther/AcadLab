package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaResumo;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.PagamentoResumo;
import school.cesar.acadlab.aplicacao.gestaofinanceira.ContestacaoResumo;
import school.cesar.acadlab.aplicacao.gestaofinanceira.DescontoResumo;
import school.cesar.acadlab.dominio.gestaofinanceira.CobrancaId;
import school.cesar.acadlab.dominio.gestaofinanceira.ContratoId;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaofinanceira.StatusCobranca;
import school.cesar.acadlab.dominio.gestaofinanceira.StatusContestacao;
import school.cesar.acadlab.dominio.gestaofinanceira.StatusPagamento;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.AplicacaoDesconto;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Cobranca;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.CobrancaRepositorio;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Contestacao;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.HistoricoVersao;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Pagamento;

@Entity
@Table(name = "COBRANCA")
class CobrancaJpa {
    @Id
    int id;
    int contratoId;
    int estudanteId;
    int periodoLetivoId;
    BigDecimal valorBase;
    BigDecimal valorAtual;
    LocalDate vencimento;
    int versao;

    @Enumerated(EnumType.STRING)
    StatusCobranca status;

    BigDecimal pagamentoValor;
    LocalDate pagamentoData;
    String pagamentoReferencia;
    @Enumerated(EnumType.STRING)
    StatusPagamento pagamentoStatus;

    Integer contestacaoRequerente;
    String contestacaoJustificativa;
    LocalDate contestacaoData;
    @Enumerated(EnumType.STRING)
    StatusContestacao contestacaoStatus;
    String contestacaoParecer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "COBRANCA_HISTORICO", joinColumns = @JoinColumn(name = "cobranca_id"))
    List<HistoricoVersaoJpa> historico = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "COBRANCA_DESCONTO", joinColumns = @JoinColumn(name = "cobranca_id"))
    List<AplicacaoDescontoJpa> descontos = new ArrayList<>();
}

@Embeddable
class HistoricoVersaoJpa {
    int versao;
    BigDecimal valorAnterior;
    @Column(length = 500)
    String motivo;
    LocalDate dataAlteracao;
}

@Embeddable
class AplicacaoDescontoJpa {
    BigDecimal percentual;
    String autorizacaoId;
    LocalDate dataAplicacao;
}

interface CobrancaJpaRepository extends JpaRepository<CobrancaJpa, Integer> {
    List<CobrancaJpa> findByContratoId(int contratoId);

    List<CobrancaJpa> findByContestacaoStatus(StatusContestacao status);

    @Query("SELECT COALESCE(MAX(c.id), 0) + 1 FROM CobrancaJpa c")
    int proximoId();
}

@Repository
class CobrancaRepositorioImpl implements CobrancaRepositorio, CobrancaRepositorioAplicacao {
    @Autowired
    CobrancaJpaRepository repositorio;

    @Override
    public CobrancaId proximoId() {
        return new CobrancaId(repositorio.proximoId());
    }

    @Override
    public void salvar(Cobranca cobranca) {
        repositorio.save(toJpa(cobranca));
    }

    @Override
    public Cobranca obter(CobrancaId id) {
        return toDomain(repositorio.findById(id.valor()).orElseThrow());
    }

    @Override
    public List<Cobranca> pesquisarPorContrato(ContratoId contratoId) {
        return repositorio.findByContratoId(contratoId.valor()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<CobrancaResumo> pesquisarPorContrato(int contratoId) {
        return repositorio.findByContratoId(contratoId).stream()
                .map(this::toResumo)
                .toList();
    }

    @Override
    public List<CobrancaResumo> pesquisarContestacoesAbertas() {
        return repositorio.findByContestacaoStatus(StatusContestacao.PENDENTE).stream()
                .map(this::toResumo)
                .toList();
    }

    private CobrancaResumo toResumo(CobrancaJpa jpa) {
        PagamentoResumo pagamento = jpa.pagamentoValor == null ? null
                : new PagamentoResumo(jpa.pagamentoValor, jpa.pagamentoData, jpa.pagamentoReferencia,
                        jpa.pagamentoStatus == null ? null : jpa.pagamentoStatus.name());
        ContestacaoResumo contestacao = jpa.contestacaoRequerente == null ? null
                : new ContestacaoResumo(jpa.contestacaoRequerente, jpa.contestacaoJustificativa,
                        jpa.contestacaoData,
                        jpa.contestacaoStatus == null ? null : jpa.contestacaoStatus.name(),
                        jpa.contestacaoParecer);
        List<DescontoResumo> descontos = jpa.descontos.stream()
                .map(d -> new DescontoResumo(d.percentual, d.autorizacaoId, d.dataAplicacao))
                .toList();
        return new CobrancaResumo(jpa.id, jpa.contratoId, jpa.estudanteId, jpa.periodoLetivoId,
                jpa.valorBase, jpa.valorAtual, jpa.vencimento, jpa.versao, jpa.status.name(),
                pagamento, contestacao, descontos);
    }

    private CobrancaJpa toJpa(Cobranca c) {
        var jpa = new CobrancaJpa();
        jpa.id = c.getId().valor();
        jpa.contratoId = c.getContratoId().valor();
        jpa.estudanteId = c.getEstudanteId().valor();
        jpa.periodoLetivoId = c.getPeriodoLetivoId().valor();
        jpa.valorBase = c.getValorBase();
        jpa.valorAtual = c.getValorAtual();
        jpa.vencimento = c.getVencimento();
        jpa.versao = c.getVersao();
        jpa.status = c.getStatus();

        if (c.getPagamento() != null) {
            jpa.pagamentoValor = c.getPagamento().getValor();
            jpa.pagamentoData = c.getPagamento().getDataPagamento();
            jpa.pagamentoReferencia = c.getPagamento().getReferencia();
            jpa.pagamentoStatus = c.getPagamento().getStatus();
        }

        if (c.getContestacao() != null) {
            jpa.contestacaoRequerente = c.getContestacao().getRequerente().valor();
            jpa.contestacaoJustificativa = c.getContestacao().getJustificativa();
            jpa.contestacaoData = c.getContestacao().getDataContestacao();
            jpa.contestacaoStatus = c.getContestacao().getStatus();
            jpa.contestacaoParecer = c.getContestacao().getParecer();
        }

        c.getHistorico().forEach(h -> {
            var hjpa = new HistoricoVersaoJpa();
            hjpa.versao = h.versao();
            hjpa.valorAnterior = h.valorAnterior();
            hjpa.motivo = h.motivo();
            hjpa.dataAlteracao = h.dataAlteracao();
            jpa.historico.add(hjpa);
        });

        c.getDescontos().forEach(d -> {
            var djpa = new AplicacaoDescontoJpa();
            djpa.percentual = d.percentual();
            djpa.autorizacaoId = d.autorizacaoId();
            djpa.dataAplicacao = d.dataAplicacao();
            jpa.descontos.add(djpa);
        });

        return jpa;
    }

    private Cobranca toDomain(CobrancaJpa jpa) {
        Pagamento pagamento = null;
        if (jpa.pagamentoValor != null) {
            pagamento = new Pagamento(jpa.pagamentoValor, jpa.pagamentoData, jpa.pagamentoReferencia);
            if (jpa.pagamentoStatus == StatusPagamento.CANCELADO)
                pagamento.cancelar("restaurado", "sistema");
        }

        Contestacao contestacao = null;
        if (jpa.contestacaoRequerente != null) {
            contestacao = new Contestacao(new EstudanteId(jpa.contestacaoRequerente),
                    jpa.contestacaoJustificativa, jpa.contestacaoData);
            var parecer = jpa.contestacaoParecer != null ? jpa.contestacaoParecer : "";
            if (jpa.contestacaoStatus == StatusContestacao.DEFERIDA)
                contestacao.deferir(parecer);
            else if (jpa.contestacaoStatus == StatusContestacao.INDEFERIDA)
                contestacao.indeferir(parecer);
        }

        var historico = jpa.historico.stream()
                .map(h -> new HistoricoVersao(h.versao, h.valorAnterior, h.motivo, h.dataAlteracao))
                .toList();

        var descontos = jpa.descontos.stream()
                .map(d -> new AplicacaoDesconto(d.percentual, d.autorizacaoId, d.dataAplicacao))
                .toList();

        return Cobranca.reconstituir(
                new CobrancaId(jpa.id),
                new ContratoId(jpa.contratoId),
                new EstudanteId(jpa.estudanteId),
                new PeriodoLetivoId(jpa.periodoLetivoId),
                jpa.valorBase,
                jpa.valorAtual,
                jpa.vencimento,
                jpa.versao,
                jpa.status,
                pagamento,
                contestacao,
                historico,
                descontos);
    }
}
