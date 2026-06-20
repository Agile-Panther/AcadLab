package school.cesar.acadlab.aplicacao.mobilidadeacademica;

public record ItemPlanoResumo(
        int disciplinaExternaId,
        String nomeDisciplinaExterna,
        int cargaHorariaExterna,
        int disciplinaEquivalenteId,
        int cargaHorariaEquivalente,
        String status,
        boolean comprovanteAnexado,
        boolean resultadoRegistrado) {
}
