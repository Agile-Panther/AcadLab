package school.cesar.acadlab.aplicacao.atividadescomplementares;

public record AtividadeComplementarResumo(
        int id,
        int estudanteId,
        int categoriaId,
        String descricao,
        int horasSubmetidas,
        int horasAprovadas,
        String status
) {}
