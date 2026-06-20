package school.cesar.acadlab.aplicacao.apoiopsicopedagogico;

import java.time.LocalDate;

public record AtendimentoResumo(String observacoes, String encaminhamento,
                                 boolean conclusaoFinal, LocalDate data) {
}
