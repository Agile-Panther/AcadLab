package school.cesar.acadlab.dominio.evento;

/**
 * Notificação gerada como efeito da reação de um observador a um evento de domínio.
 * Usa o identificador do destinatário como primitivo para permanecer neutra entre
 * bounded contexts (cada contexto possui seu próprio EstudanteId local).
 *
 * @param destinatarioId identificador do estudante destinatário
 * @param tipo           rótulo do tipo de notificação (ex.: "ATIVIDADE_DEFERIDA")
 * @param mensagem       texto legível da notificação
 */
public record Notificacao(int destinatarioId, String tipo, String mensagem) {
}
