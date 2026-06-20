package school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao;

import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ResultadoEstudante;

/**
 * Regime alternativo: média aritmética simples das notas lançadas, ignorando os
 * pesos das avaliações. Útil para componentes avaliados de forma uniforme.
 */
public class ApuracaoMediaAritmetica extends ApuracaoResultado {

    @Override
    protected double calcularMedia(DiarioTurma diario, ResultadoEstudante resultado) {
        var notas = resultado.getNotas().values();
        if (notas.isEmpty()) {
            return 0.0;
        }
        return notas.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
}
