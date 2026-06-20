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
                "fora da janela de matrícula");
        isTrue(cumpriuPreRequisitos,
                "pré-requisitos não cumpridos");
        isTrue(correquisitosNoPlano,
                "correquisitos ausentes no plano");
        isTrue(creditosExistentes + creditos <= limiteCreditos,
                "limite máximo de créditos atingido");
        isTrue(!temPendencias,
                "estudante possui pendências acadêmicas impeditivas");
    }
}
