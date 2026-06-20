package school.cesar.acadlab.aplicacao.matricula;

import java.util.List;

public record MatriculaResumo(
        int id,
        int estudanteId,
        int periodoLetivoId,
        String status,
        List<ItemResumo> itens
) {}
