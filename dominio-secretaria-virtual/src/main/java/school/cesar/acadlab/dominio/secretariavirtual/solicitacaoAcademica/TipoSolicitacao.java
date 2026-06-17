package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import java.util.List;

public enum TipoSolicitacao {
    TRANCAMENTO_DISCIPLINA(List.of("comprovante_matricula")),
    TRANCAMENTO_PERIODO(List.of("comprovante_matricula", "justificativa_formal")),
    REVISAO_DE_NOTA(List.of("comprovante_avaliacao")),
    APROVEITAMENTO_DISCIPLINA(List.of("historico_origem", "ementa_disciplina")),
    SEGUNDA_VIA_DOCUMENTO(List.of()),
    CORRECAO_HISTORICO(List.of("documento_comprobatorio")),
    DECLARACAO_VINCULO(List.of()),
    OUTROS(List.of());

    private final List<String> documentosObrigatorios;

    TipoSolicitacao(List<String> documentosObrigatorios) {
        this.documentosObrigatorios = documentosObrigatorios;
    }

    public List<String> getDocumentosObrigatorios() {
        return documentosObrigatorios;
    }

    public boolean isPermiteMultiplasPorPeriodo() {
        return this == REVISAO_DE_NOTA;
    }
}
