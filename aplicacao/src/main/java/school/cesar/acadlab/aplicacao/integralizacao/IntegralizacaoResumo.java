package school.cesar.acadlab.aplicacao.integralizacao;

public record IntegralizacaoResumo(
        int id,
        int estudanteId,
        int matrizCurricularId,
        String status,
        Integer aprovadorId,
        String dataAprovacao) {
}
