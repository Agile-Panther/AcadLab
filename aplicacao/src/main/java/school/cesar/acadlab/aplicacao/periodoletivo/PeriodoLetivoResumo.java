package school.cesar.acadlab.aplicacao.periodoletivo;

import java.time.LocalDate;
import java.util.List;

public record PeriodoLetivoResumo(
        int id,
        int cursoId,
        int ano,
        int semestre,
        LocalDate dataInicio,
        LocalDate dataFim,
        String status,
        List<JanelaResumo> janelas
) {
    public record JanelaResumo(String tipo, LocalDate inicio, LocalDate fim) {}
}
