package school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao;

import static org.apache.commons.lang3.Validate.notNull;

import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurma;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.ResultadoEstudante;

/**
 * Template Method da apuração do resultado do estudante.
 *
 * <p>O método {@link #apurar} define a sequência fixa do algoritmo de fechamento:
 * <ol>
 *   <li>contar o total de aulas (passo fixo);</li>
 *   <li>contar as presenças do estudante (passo fixo);</li>
 *   <li>calcular a média — <strong>passo variável</strong> especializado pelas subclasses;</li>
 *   <li>registrar a situação final aplicando os mínimos de média e frequência (passo fixo).</li>
 * </ol>
 *
 * Subclasses só podem redefinir {@link #calcularMedia}; a ordem das etapas é
 * garantida pelo método-template, que é {@code final}.
 */
public abstract class ApuracaoResultado {

    /** Método-template: ordem fixa e não sobrescrevível das etapas de apuração. */
    public final void apurar(DiarioTurma diario, ResultadoEstudante resultado) {
        notNull(diario, "O diário não pode ser nulo");
        notNull(resultado, "O resultado do estudante não pode ser nulo");

        int totalAulas = contarAulas(diario);
        long presencas = contarPresencas(diario, resultado);
        double media = calcularMedia(diario, resultado);
        registrarSituacao(diario, resultado, totalAulas, presencas, media);
    }

    /** Passo fixo: total de aulas registradas no diário. */
    protected int contarAulas(DiarioTurma diario) {
        return diario.getAulas().size();
    }

    /** Passo fixo: presenças do estudante. */
    protected long contarPresencas(DiarioTurma diario, ResultadoEstudante resultado) {
        return diario.getFrequencias().stream()
                .filter(f -> f.getEstudanteId().equals(resultado.getEstudanteId()) && f.isPresente())
                .count();
    }

    /** Passo variável: cada regime de apuração calcula a média à sua maneira. */
    protected abstract double calcularMedia(DiarioTurma diario, ResultadoEstudante resultado);

    /** Passo fixo: aplica os mínimos de média e frequência e fecha o resultado. */
    protected void registrarSituacao(DiarioTurma diario, ResultadoEstudante resultado,
                                     int totalAulas, long presencas, double media) {
        resultado.fechar(diario.getMediaMinima(), diario.getFrequenciaMinima(),
                totalAulas, presencas, media);
    }
}
