package school.cesar.acadlab.aplicacao.mobilidadeacademica;

public record MobilidadeAcademicaResumo(
        int id,
        int estudanteId,
        String instituicaoDestino,
        String status) {
}
