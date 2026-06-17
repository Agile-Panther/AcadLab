package school.cesar.acadlab.aplicacao.secretariavirtual;

import java.time.LocalDate;

public record SolicitacaoAcademicaResumo(
        int id,
        int estudanteId,
        int periodoLetivoId,
        String tipo,
        String status,
        String descricao,
        int protocoloId,
        LocalDate dataAbertura,
        String justificativaAnalise,
        LocalDate dataAnalise) {
}
