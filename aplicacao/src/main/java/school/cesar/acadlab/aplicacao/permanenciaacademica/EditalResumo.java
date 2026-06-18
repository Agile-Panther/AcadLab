package school.cesar.acadlab.aplicacao.permanenciaacademica;

import java.time.LocalDate;

public record EditalResumo(int id, String programa, int vagas,
                            LocalDate prazoInscricaoInicio, LocalDate prazoInscricaoFim,
                            LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim,
                            LocalDate prazoRenovacao, String status) {
}
