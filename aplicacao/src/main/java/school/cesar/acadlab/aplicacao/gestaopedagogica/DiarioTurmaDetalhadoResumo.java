package school.cesar.acadlab.aplicacao.gestaopedagogica;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public record DiarioTurmaDetalhadoResumo(
        int id,
        int turmaId,
        int periodoLetivoId,
        int professorResponsavelId,
        LocalDate dataInicioPeriodo,
        LocalDate dataFimPeriodo,
        double mediaMinima,
        double frequenciaMinima,
        String status,
        List<AulaResumo> aulas,
        List<AvaliacaoResumo> avaliacoes,
        List<FrequenciaResumo> frequencias,
        List<ResultadoResumo> resultados,
        Set<Integer> estudantesAtivos) {

    public record AulaResumo(int id, int professorId, LocalDate data, String conteudo, boolean corrigido) {}

    public record AvaliacaoResumo(int id, String nome, double peso, LocalDate prazo) {}

    public record FrequenciaResumo(int aulaId, int estudanteId, boolean presente) {}

    public record ResultadoResumo(
            int estudanteId,
            String situacao,
            boolean fechado,
            boolean revisaoSolicitada,
            Double notaRecuperacao,
            Map<Integer, Double> notas) {}
}