package school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao;

import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ResultadoEstudante;

/**
 * Regime padrão (RN-7): média ponderada das avaliações pelos respectivos pesos.
 */
public class ApuracaoMediaPonderada extends ApuracaoResultado {

    @Override
    protected double calcularMedia(DiarioTurma diario, ResultadoEstudante resultado) {
        double somaPonderada = 0.0;
        double somaPesos = 0.0;
        for (var avaliacao : diario.getAvaliacoes()) {
            Double nota = resultado.getNotas().get(avaliacao.getId());
            if (nota != null) {
                somaPonderada += nota * avaliacao.getPeso();
                somaPesos += avaliacao.getPeso();
            }
        }
        return somaPesos > 0 ? somaPonderada / somaPesos : 0.0;
    }
}
