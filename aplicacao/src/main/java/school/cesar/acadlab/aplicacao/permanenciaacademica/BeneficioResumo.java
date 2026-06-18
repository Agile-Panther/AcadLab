package school.cesar.acadlab.aplicacao.permanenciaacademica;

import java.time.LocalDate;

public record BeneficioResumo(int id, int inscricaoId, int estudanteId,
                               int editalId, String status,
                               LocalDate dataAtivacao, LocalDate prazoRenovacao) {
}
