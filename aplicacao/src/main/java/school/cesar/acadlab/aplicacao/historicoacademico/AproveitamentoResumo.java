package school.cesar.acadlab.aplicacao.historicoacademico;

public record AproveitamentoResumo(
        int id,
        int disciplinaEquivalenteId,
        int cargaHorariaExterna,
        int cargaHorariaRequerida,
        String instituicaoOrigem,
        String disciplinaOrigem) {
}
