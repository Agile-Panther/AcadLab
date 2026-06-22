package school.cesar.acadlab.aplicacao.gestaopedagogica;

import java.time.LocalDate;

public record DiarioTurmaResumo(
        int id,
        int turmaId,
        int periodoLetivoId,
        int professorResponsavelId,
        LocalDate dataInicioPeriodo,
        LocalDate dataFimPeriodo,
        double mediaMinima,
        double frequenciaMinima,
        String status,
        int aulasCount,
        int estudantesCount,
        int avaliacoesCount) {
}
