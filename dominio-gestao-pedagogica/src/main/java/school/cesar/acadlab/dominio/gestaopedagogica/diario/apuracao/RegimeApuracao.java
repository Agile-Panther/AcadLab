package school.cesar.acadlab.dominio.gestaopedagogica.diario.apuracao;

/**
 * Regimes de apuração disponíveis. Cada valor instancia a implementação concreta
 * do Template Method correspondente, permitindo selecionar o regime na borda
 * (serviço/controlador) sem acoplá-la às classes concretas.
 */
public enum RegimeApuracao {
    PONDERADA {
        @Override
        public ApuracaoResultado apuracao() {
            return new ApuracaoMediaPonderada();
        }
    },
    ARITMETICA {
        @Override
        public ApuracaoResultado apuracao() {
            return new ApuracaoMediaAritmetica();
        }
    };

    public abstract ApuracaoResultado apuracao();
}
