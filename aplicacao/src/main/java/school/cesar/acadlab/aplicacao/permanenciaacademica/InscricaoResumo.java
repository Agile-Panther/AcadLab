package school.cesar.acadlab.aplicacao.permanenciaacademica;

import java.time.LocalDate;

public record InscricaoResumo(int id, int editalId, int estudanteId,
                               String status, int pontuacao, LocalDate dataInscricao) {
}
