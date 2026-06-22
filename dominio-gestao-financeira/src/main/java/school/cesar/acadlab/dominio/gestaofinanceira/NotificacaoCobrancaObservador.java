package school.cesar.acadlab.dominio.gestaofinanceira;

import java.util.Objects;

import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.ObservadorDeEvento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Cobranca;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.Cobranca.CobrancaEvento;

/**
 * Observador concreto do contexto de Gestão Financeira: ao ocorrer um evento de
 * cobrança (pagamento, desconto, contestação, cancelamento ou nova versão),
 * notifica o estudante titular registrando uma {@link Notificacao}.
 */
public class NotificacaoCobrancaObservador extends ObservadorDeEvento<CobrancaEvento> {

    private final RegistroNotificacoes registro;

    public NotificacaoCobrancaObservador(RegistroNotificacoes registro) {
        super(CobrancaEvento.class);
        this.registro = Objects.requireNonNull(registro, "O registro de notificações não pode ser nulo");
    }

    @Override
    protected void reagir(CobrancaEvento evento) {
        var cobranca = evento.getCobranca();
        registro.registrar(new Notificacao(
                cobranca.getEstudanteId().valor(),
                tipoDe(evento),
                mensagemDe(evento)));
    }

    private String tipoDe(CobrancaEvento evento) {
        if (evento instanceof Cobranca.PagamentoRegistradoEvento) return "COBRANCA_PAGAMENTO_REGISTRADO";
        if (evento instanceof Cobranca.PagamentoCanceladoEvento) return "COBRANCA_PAGAMENTO_CANCELADO";
        if (evento instanceof Cobranca.DescontoAplicadoEvento) return "COBRANCA_DESCONTO_APLICADO";
        if (evento instanceof Cobranca.ContestacaoRegistradaEvento) return "COBRANCA_CONTESTACAO_REGISTRADA";
        if (evento instanceof Cobranca.ContestacaoResolvidaEvento) return "COBRANCA_CONTESTACAO_RESOLVIDA";
        if (evento instanceof Cobranca.NovaVersaoGeradaEvento) return "COBRANCA_ATUALIZADA";
        return "COBRANCA_CANCELADA";
    }

    private String mensagemDe(CobrancaEvento evento) {
        if (evento instanceof Cobranca.PagamentoRegistradoEvento) {
            return "Recebemos o pagamento da sua cobrança.";
        }
        if (evento instanceof Cobranca.PagamentoCanceladoEvento) {
            return "Um pagamento da sua cobrança foi cancelado.";
        }
        if (evento instanceof Cobranca.DescontoAplicadoEvento) {
            return "Um desconto foi aplicado à sua cobrança.";
        }
        if (evento instanceof Cobranca.ContestacaoRegistradaEvento) {
            return "Sua contestação de cobrança foi registrada.";
        }
        if (evento instanceof Cobranca.ContestacaoResolvidaEvento) {
            return "Sua contestação de cobrança foi avaliada.";
        }
        if (evento instanceof Cobranca.NovaVersaoGeradaEvento) {
            return "Sua cobrança foi atualizada.";
        }
        return "Sua cobrança foi cancelada.";
    }
}
