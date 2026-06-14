package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

import java.time.LocalDate;

public class ValidacaoExcecao implements EstrategiaMatricula {

    @Override
    public void validarAdicao(int creditosExistentes, int creditos, int limiteCreditos,
                               boolean cumpriuPreRequisitos, boolean correquisitosNoPlano,
                               boolean temPendencias, LocalDate hoje,
                               LocalDate inicioJanela, LocalDate fimJanela) {
        isTrue(!hoje.isBefore(inicioJanela) && !hoje.isAfter(fimJanela),
                "A montagem do plano só é permitida dentro da janela de matrícula (RN-1)");
        isTrue(creditosExistentes + creditos <= limiteCreditos,
                "A adição da disciplina excede o limite máximo de créditos por período (RN-4)");
        // pré-requisitos (RN-2), correquisitos (RN-3) e pendências (RN-5)
        // são ignorados pois a exceção foi deferida pela coordenação (RN-10)
    }
}
