package school.cesar.acadlab.aplicacao.estagios;

public record EstagioResumo(
        int id,
        int oportunidadeId,
        int estudanteId,
        int empresaId,
        String status) {
}
