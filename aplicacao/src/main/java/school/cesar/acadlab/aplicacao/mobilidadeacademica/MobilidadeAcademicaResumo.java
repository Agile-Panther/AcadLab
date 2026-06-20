package school.cesar.acadlab.aplicacao.mobilidadeacademica;

import java.util.List;

public record MobilidadeAcademicaResumo(
        int id,
        int estudanteId,
        String instituicaoDestino,
        String status,
        String dataInicioPeriodoExterno,
        String justificativaCancelamento,
        List<ItemPlanoResumo> plano) {
}
