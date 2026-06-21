package school.cesar.acadlab.aplicacao.atividadescomplementares;

import java.time.LocalDate;

public record AtividadeComplementarResumo(
        int id,
        int estudanteId,
        int categoriaId,
        String descricao,
        int horasSubmetidas,
        int horasAprovadas,
        String status,
        LocalDate dataRealizacao,
        String identificadorCertificado
) {}
