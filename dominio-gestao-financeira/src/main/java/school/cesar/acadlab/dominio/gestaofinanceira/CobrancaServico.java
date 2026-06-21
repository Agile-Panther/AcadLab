package school.cesar.acadlab.dominio.gestaofinanceira;

import static org.apache.commons.lang3.Validate.notNull;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class CobrancaServico {
    private final CobrancaRepositorio repositorio;
    private final VerificadorMatriculaConfirmada verificadorMatricula;
    private final VerificadorAutorizacaoDesconto verificadorAutorizacao;
    private final EventoBarramento barramento;

    public CobrancaServico(CobrancaRepositorio repositorio,
            VerificadorMatriculaConfirmada verificadorMatricula,
            VerificadorAutorizacaoDesconto verificadorAutorizacao,
            EventoBarramento barramento) {
        notNull(repositorio, "repositório obrigatório");
        notNull(verificadorMatricula, "verificadorMatricula obrigatório");
        notNull(verificadorAutorizacao, "verificadorAutorizacao obrigatório");
        notNull(barramento, "barramento obrigatório");
        this.repositorio = repositorio;
        this.verificadorMatricula = verificadorMatricula;
        this.verificadorAutorizacao = verificadorAutorizacao;
        this.barramento = barramento;
    }

    public Cobranca gerarCobranca(ContratoId contratoId, EstudanteId estudanteId,
            PeriodoLetivoId periodoLetivoId, BigDecimal valor, LocalDate vencimento) {
        notNull(estudanteId, "estudanteId obrigatório");
        notNull(periodoLetivoId, "periodoLetivoId obrigatório");
        if (!verificadorMatricula.possuiMatricula(estudanteId, periodoLetivoId))
            throw new IllegalStateException("estudante não possui matrícula confirmada no período");
        var id = repositorio.proximoId();
        var cobranca = new Cobranca(id, contratoId, estudanteId, periodoLetivoId, valor, vencimento);
        repositorio.salvar(cobranca);
        return cobranca;
    }

    public void gerarNovaVersao(CobrancaId id, String motivo, BigDecimal novoValor) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.gerarNovaVersao(motivo, novoValor);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void aplicarDesconto(CobrancaId id, BigDecimal percentual, String autorizacaoId) {
        notNull(id, "id obrigatório");
        if (!verificadorAutorizacao.autorizacaoValida(autorizacaoId))
            throw new IllegalStateException("autorização inválida para aplicação de desconto");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.aplicarDesconto(percentual, autorizacaoId, LocalDate.now());
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void contestar(CobrancaId id, EstudanteId estudanteId, String justificativa) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.contestar(estudanteId, justificativa, LocalDate.now());
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void deferirContestacao(CobrancaId id, ModoAjuste modo, BigDecimal valor, String parecer) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.deferirContestacao(modo, valor, parecer);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void indeferirContestacao(CobrancaId id, String parecer) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.indeferirContestacao(parecer);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void registrarPagamento(CobrancaId id, BigDecimal valor, LocalDate data, String referencia) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.registrarPagamento(valor, data, referencia);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void cancelarPagamento(CobrancaId id, String justificativa, String responsavel) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.cancelarPagamento(justificativa, responsavel);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void cancelarCobranca(CobrancaId id, String motivo) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.cancelar(motivo);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public Pagamento emitirComprovante(CobrancaId id) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var pagamento = cobranca.getPagamento();
        if (pagamento == null || pagamento.getStatus() != StatusPagamento.CONFIRMADO)
            throw new IllegalStateException("comprovante indisponível sem pagamento confirmado");
        return pagamento;
    }

    public List<Cobranca> consultarExtrato(ContratoId contratoId) {
        notNull(contratoId, "contratoId obrigatório");
        return repositorio.pesquisarPorContrato(contratoId);
    }
}
