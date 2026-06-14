package school.cesar.acadlab.aplicacao.estagios;

public record OportunidadeResumo(
        int id,
        int empresaId,
        String descricao,
        int cargaHorariaTotal,
        String status) {
}
