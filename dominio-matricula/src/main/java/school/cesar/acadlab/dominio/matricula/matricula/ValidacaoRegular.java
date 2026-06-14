package school.cesar.acadlab.dominio.matricula.matricula;

import static org.apache.commons.lang3.Validate.isTrue;

import java.time.LocalDate;

public class ValidacaoRegular implements EstrategiaMatricula {

    @Override
    public void validarAdicao(int creditosExistentes, int creditos, int limiteCreditos,
                               boolean cumpriuPreRequisitos, boolean correquisitosNoPlano,
                               boolean temPendencias, LocalDate hoje,
                               LocalDate inicioJanela, LocalDate fimJanela) {
        isTrue(!hoje.isBefore(inicioJanela) && !hoje.isAfter(fimJanela),
                "A montagem do plano só é permitida dentro da janela de matrícula (RN-1)");
        isTrue(cumpriuPreRequisitos,
                "O estudante não cumpriu os pré-requisitos da disciplina (RN-2)");
        isTrue(correquisitosNoPlano,
                "Os correquisitos da disciplina devem estar incluídos no plano do mesmo período (RN-3)");
        isTrue(creditosExistentes + creditos <= limiteCreditos,
                "A adição da disciplina excede o limite máximo de créditos por período (RN-4)");
        isTrue(!temPendencias,
                "O estudante possui pendências acadêmicas impeditivas (RN-5)");
    }
}
