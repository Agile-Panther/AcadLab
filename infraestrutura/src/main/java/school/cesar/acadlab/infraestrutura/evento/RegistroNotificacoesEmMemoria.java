package school.cesar.acadlab.infraestrutura.evento;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.evento.Notificacao;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

/**
 * Registro de notificações em memória, alimentado pelos observadores concretos.
 * Thread-safe para suportar publicação concorrente de eventos.
 */
@Component
public class RegistroNotificacoesEmMemoria implements RegistroNotificacoes {

    private final List<Notificacao> notificacoes = new CopyOnWriteArrayList<>();

    @Override
    public void registrar(Notificacao notificacao) {
        notificacoes.add(notificacao);
    }

    @Override
    public List<Notificacao> todas() {
        return List.copyOf(notificacoes);
    }

    @Override
    public List<Notificacao> doDestinatario(int destinatarioId) {
        return notificacoes.stream()
                .filter(n -> n.destinatarioId() == destinatarioId)
                .toList();
    }
}
