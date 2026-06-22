package school.cesar.acadlab.dominio.permanenciaacademica;

import java.util.Objects;

import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.ObservadorDeEvento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;
import school.cesar.acadlab.dominio.permanenciaacademica.Inscricao.InscricaoEvento;

/**
 * Observador concreto do contexto de Permanência Acadêmica: ao analisar uma
 * inscrição (deferir, indeferir ou interpor recurso), notifica o estudante
 * registrando uma {@link Notificacao}.
 */
public class NotificacaoInscricaoObservador extends ObservadorDeEvento<InscricaoEvento> {

    private final RegistroNotificacoes registro;

    public NotificacaoInscricaoObservador(RegistroNotificacoes registro) {
        super(InscricaoEvento.class);
        this.registro = Objects.requireNonNull(registro, "O registro de notificações não pode ser nulo");
    }

    @Override
    protected void reagir(InscricaoEvento evento) {
        var inscricao = evento.getInscricao();
        registro.registrar(new Notificacao(
                inscricao.getEstudanteId().getValor(),
                tipoDe(evento),
                mensagemDe(evento)));
    }

    private String tipoDe(InscricaoEvento evento) {
        if (evento instanceof Inscricao.InscricaoDeferidaEvento) return "INSCRICAO_DEFERIDA";
        if (evento instanceof Inscricao.InscricaoIndeferidaEvento) return "INSCRICAO_INDEFERIDA";
        return "RECURSO_INTERPOSTO";
    }

    private String mensagemDe(InscricaoEvento evento) {
        if (evento instanceof Inscricao.InscricaoDeferidaEvento) {
            return "Sua inscrição no programa de permanência foi deferida.";
        }
        if (evento instanceof Inscricao.InscricaoIndeferidaEvento) {
            return "Sua inscrição no programa de permanência foi indeferida.";
        }
        return "Seu recurso na inscrição do programa de permanência foi registrado.";
    }
}
