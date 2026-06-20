package school.cesar.acadlab.dominio.apoiopsicopedagogico;

import java.util.Objects;

import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.Caso.CasoEvento;
import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.ObservadorDeEvento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

/**
 * Observador concreto do contexto de Apoio Psicopedagógico. Notifica o próprio
 * estudante (destinatário do caso) a cada evolução do acompanhamento. Respeita o
 * sigilo: as mensagens são neutras, sem expor detalhes clínicos do atendimento.
 */
public class NotificacaoCasoObservador extends ObservadorDeEvento<CasoEvento> {

    private final RegistroNotificacoes registro;

    public NotificacaoCasoObservador(RegistroNotificacoes registro) {
        super(CasoEvento.class);
        this.registro = Objects.requireNonNull(registro, "O registro de notificações não pode ser nulo");
    }

    @Override
    protected void reagir(CasoEvento evento) {
        var caso = evento.getCaso();
        registro.registrar(new Notificacao(
                caso.getEstudanteId().getId(),
                tipoDe(evento),
                mensagemDe(evento)));
    }

    private String tipoDe(CasoEvento evento) {
        if (evento instanceof Caso.TriagemRealizadaEvento) return "APOIO_TRIAGEM_REALIZADA";
        if (evento instanceof Caso.AtendimentoRegistradoEvento) return "APOIO_ATENDIMENTO_REGISTRADO";
        if (evento instanceof Caso.CasoEncerradoEvento) return "APOIO_CASO_ENCERRADO";
        if (evento instanceof Caso.CasoReabertoEvento) return "APOIO_CASO_REABERTO";
        if (evento instanceof Caso.AgendamentoMarcadoEvento) return "APOIO_AGENDAMENTO_MARCADO";
        return "APOIO_AGENDAMENTO_CONTESTADO";
    }

    private String mensagemDe(CasoEvento evento) {
        if (evento instanceof Caso.TriagemRealizadaEvento) {
            return "Sua triagem no apoio psicopedagógico foi realizada.";
        }
        if (evento instanceof Caso.AtendimentoRegistradoEvento) {
            return "Um atendimento do seu acompanhamento psicopedagógico foi registrado.";
        }
        if (evento instanceof Caso.CasoEncerradoEvento) {
            return "Seu acompanhamento psicopedagógico foi encerrado.";
        }
        if (evento instanceof Caso.CasoReabertoEvento) {
            return "Seu acompanhamento psicopedagógico foi reaberto.";
        }
        if (evento instanceof Caso.AgendamentoMarcadoEvento) {
            return "Um atendimento de apoio psicopedagógico foi agendado para você.";
        }
        return "Houve uma atualização no agendamento do seu atendimento.";
    }
}
