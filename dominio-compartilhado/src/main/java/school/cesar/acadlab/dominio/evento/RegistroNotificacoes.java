package school.cesar.acadlab.dominio.evento;

import java.util.List;

/**
 * Porta de saída para o efeito observável de notificação. Observadores concretos
 * registram notificações aqui; a infraestrutura fornece a implementação.
 */
public interface RegistroNotificacoes {
    void registrar(Notificacao notificacao);

    List<Notificacao> todas();

    List<Notificacao> doDestinatario(int destinatarioId);
}
