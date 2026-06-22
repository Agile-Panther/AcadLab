package school.cesar.acadlab.dominio.historicoacademico.historico;

/**
 * Porta de consulta (somente leitura) ao Planejamento do Período Letivo (F-02),
 * usada pela RN-10 para incluir no histórico oficial apenas registros de
 * períodos letivos encerrados.
 */
public interface ConsultaPeriodoEncerradoPorta {
    boolean estaEncerrado(PeriodoLetivoId periodoLetivoId);
}
