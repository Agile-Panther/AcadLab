package school.cesar.acadlab.aplicacao.apoiopsicopedagogico;

import java.time.LocalDate;
import java.util.List;

public record CasoResumo(int id, int estudanteId, Integer responsavelId, String status,
                          String motivo, LocalDate abertura, String prioridadeTriagem,
                          String triagemObservacoes, AgendamentoResumo agendamento,
                          List<AtendimentoResumo> atendimentos) {
}
