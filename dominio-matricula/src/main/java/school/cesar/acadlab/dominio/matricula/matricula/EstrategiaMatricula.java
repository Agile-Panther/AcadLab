package school.cesar.acadlab.dominio.matricula.matricula;

import java.time.LocalDate;

public interface EstrategiaMatricula {
    void validarAdicao(int creditosExistentes, int creditos, int limiteCreditos,
                        boolean cumpriuPreRequisitos, boolean correquisitosNoPlano,
                        boolean temPendencias, LocalDate hoje,
                        LocalDate inicioJanela, LocalDate fimJanela);
}
