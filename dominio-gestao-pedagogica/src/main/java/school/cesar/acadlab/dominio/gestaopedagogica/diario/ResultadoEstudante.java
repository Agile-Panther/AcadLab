package school.cesar.acadlab.dominio.gestaopedagogica.diario;

import java.util.HashMap;
import java.util.Map;

public class ResultadoEstudante {
    private final EstudanteId estudanteId;
    private final Map<AvaliacaoId, Double> notas;
    private Double notaRecuperacao;
    private SituacaoResultado situacao;
    private boolean fechado;
    private boolean revisaoSolicitada;

    public ResultadoEstudante(EstudanteId estudanteId) {
        this.estudanteId = estudanteId;
        this.notas = new HashMap<>();
        this.fechado = false;
        this.revisaoSolicitada = false;
    }

    public static ResultadoEstudante reconstituir(EstudanteId estudanteId,
                                                   Map<AvaliacaoId, Double> notas,
                                                   Double notaRecuperacao,
                                                   SituacaoResultado situacao,
                                                   boolean fechado, boolean revisaoSolicitada) {
        var resultado = new ResultadoEstudante(estudanteId);
        resultado.notas.putAll(notas);
        resultado.notaRecuperacao = notaRecuperacao;
        resultado.situacao = situacao;
        resultado.fechado = fechado;
        resultado.revisaoSolicitada = revisaoSolicitada;
        return resultado;
    }

    public EstudanteId getEstudanteId() { return estudanteId; }
    public Map<AvaliacaoId, Double> getNotas() { return notas; }
    public Double getNotaRecuperacao() { return notaRecuperacao; }
    public SituacaoResultado getSituacao() { return situacao; }
    public boolean isFechado() { return fechado; }
    public boolean isRevisaoSolicitada() { return revisaoSolicitada; }

    // RN-8: resultado fechado exige revisão formal para alteração.
    public void adicionarNota(AvaliacaoId avaliacaoId, double nota) {
        if (fechado) {
            throw new IllegalStateException("resultado já está fechado");
        }
        notas.put(avaliacaoId, nota);
    }

    // RN-7: calcula situação final do estudante com base em média e frequência.
    public void fechar(double mediaMinima, double frequenciaMinima, int totalAulas, long presencas, double mediaPonderada) {
        double frequencia = totalAulas == 0 ? 0.0 : (presencas * 100.0) / totalAulas;

        if (frequencia < frequenciaMinima) {
            this.situacao = SituacaoResultado.REPROVADO_FALTA;
        } else if (mediaPonderada < mediaMinima) {
            double limiteRecuperacao = mediaMinima * 0.6;
            if (mediaPonderada >= limiteRecuperacao) {
                this.situacao = SituacaoResultado.RECUPERACAO;
            } else {
                this.situacao = SituacaoResultado.REPROVADO_NOTA;
            }
        } else {
            this.situacao = SituacaoResultado.APROVADO;
        }
        this.fechado = true;
    }

    public void lancarRecuperacao(double nota) {
        this.notaRecuperacao = nota;
    }

    public void atualizarSituacaoRecuperacao(SituacaoResultado situacao) {
        this.situacao = situacao;
    }

    public void solicitarRevisao() {
        this.revisaoSolicitada = true;
    }
}
