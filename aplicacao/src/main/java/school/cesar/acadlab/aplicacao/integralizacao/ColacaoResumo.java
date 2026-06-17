package school.cesar.acadlab.aplicacao.integralizacao;

public record ColacaoResumo(
        int id,
        int estudanteId,
        int integralizacaoId,
        String dataAptidaoAprovada,
        String dataCerimonia,
        String local) {
}
