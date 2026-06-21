package school.cesar.acadlab.dominio.atividadescomplementares;

import java.util.Objects;

import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementar;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementar.AtividadeComplementarEvento;
import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.ObservadorDeEvento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

/**
 * Observador concreto do contexto de Atividades Complementares: ao analisar uma
 * atividade (deferir, indeferir, solicitar revisão ou cancelar), notifica o
 * estudante registrando uma {@link Notificacao}.
 */
public class NotificacaoAtividadeObservador extends ObservadorDeEvento<AtividadeComplementarEvento> {

    private final RegistroNotificacoes registro;

    public NotificacaoAtividadeObservador(RegistroNotificacoes registro) {
        super(AtividadeComplementarEvento.class);
        this.registro = Objects.requireNonNull(registro, "O registro de notificações não pode ser nulo");
    }

    @Override
    protected void reagir(AtividadeComplementarEvento evento) {
        var atividade = evento.getAtividade();
        registro.registrar(new Notificacao(
                atividade.getEstudanteId().valor(),
                tipoDe(evento),
                mensagemDe(evento, atividade)));
    }

    private String tipoDe(AtividadeComplementarEvento evento) {
        if (evento instanceof AtividadeComplementar.DeferidaEvento) return "ATIVIDADE_DEFERIDA";
        if (evento instanceof AtividadeComplementar.IndeferidaEvento) return "ATIVIDADE_INDEFERIDA";
        if (evento instanceof AtividadeComplementar.RevisaoSolicitadaEvento) return "ATIVIDADE_EM_REVISAO";
        return "ATIVIDADE_CANCELADA";
    }

    private String mensagemDe(AtividadeComplementarEvento evento, AtividadeComplementar atividade) {
        if (evento instanceof AtividadeComplementar.DeferidaEvento) {
            return "Sua atividade complementar foi deferida com "
                    + atividade.getHorasAprovadas() + " horas aprovadas.";
        }
        if (evento instanceof AtividadeComplementar.IndeferidaEvento) {
            return "Sua atividade complementar foi indeferida.";
        }
        if (evento instanceof AtividadeComplementar.RevisaoSolicitadaEvento) {
            return "Foi solicitada a revisão da sua atividade complementar.";
        }
        return "Sua atividade complementar foi cancelada.";
    }
}
