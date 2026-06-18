package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.Protocolo;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.ProtocoloId;

class SolicitacaoAcademicaTest {

    private final SolicitacaoAcademicaId solicitacaoId = new SolicitacaoAcademicaId(1);
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(1);
    private final Protocolo protocolo = new Protocolo(new ProtocoloId(1));
    private final SecretariaId secretariaId = new SecretariaId(1);

    private SolicitacaoAcademica criarSolicitacao(TipoSolicitacao tipo) {
        return new SolicitacaoAcademica(solicitacaoId, estudanteId, periodoLetivoId,
                tipo, protocolo, "Solicitação de teste");
    }

    @Test
    void novaSolicitacao_deveIniciarComStatusPendenteAnalise() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        assertEquals(StatusSolicitacao.PENDENTE_ANALISE, solicitacao.getStatus());
    }

    @Test
    void anexarDocumento_deveAdicionarDocumentoALista() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        var documento = new Documento("comprovante", "comprovante.pdf");

        solicitacao.anexarDocumento(documento);

        assertEquals(1, solicitacao.getDocumentos().size());
    }

    // RN3: documentos obrigatórios
    @Test
    void validarDocumentosObrigatorios_comDocumentosFaltando_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.APROVEITAMENTO_DISCIPLINA);

        assertThrows(IllegalStateException.class, solicitacao::validarDocumentosObrigatorios);
    }

    @Test
    void validarDocumentosObrigatorios_comTodosDocumentos_naoDeveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.APROVEITAMENTO_DISCIPLINA);
        solicitacao.anexarDocumento(new Documento("historico_origem", "historico.pdf"));
        solicitacao.anexarDocumento(new Documento("ementa_disciplina", "ementa.pdf"));

        assertDoesNotThrow(solicitacao::validarDocumentosObrigatorios);
    }

    @Test
    void validarDocumentosObrigatorios_tipoSemObrigatorios_naoDeveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);

        assertDoesNotThrow(solicitacao::validarDocumentosObrigatorios);
    }

    // RN5: complementação não permitida em concluídas ou indeferidas
    @Test
    void complementar_emSolicitacaoConcluida_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.deferir(secretariaId, "Aprovado", false);
        solicitacao.concluir();

        var documento = new Documento("extra", "extra.pdf");
        assertThrows(IllegalStateException.class, () -> solicitacao.complementar(documento));
    }

    @Test
    void complementar_emSolicitacaoIndeferida_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.indeferir(secretariaId, "Documentação insuficiente");

        var documento = new Documento("extra", "extra.pdf");
        assertThrows(IllegalStateException.class, () -> solicitacao.complementar(documento));
    }

    @Test
    void complementar_emPendenteComplementacao_deveVoltarParaPendenteAnalise() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.solicitarComplementacao(secretariaId);
        assertEquals(StatusSolicitacao.PENDENTE_COMPLEMENTACAO, solicitacao.getStatus());

        solicitacao.complementar(new Documento("complemento", "complemento.pdf"));

        assertEquals(StatusSolicitacao.PENDENTE_ANALISE, solicitacao.getStatus());
    }

    // Análise
    @Test
    void iniciarAnalise_emPendenteAnalise_deveAlterarStatusParaEmAnalise() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);

        var evento = solicitacao.iniciarAnalise(secretariaId);

        assertNotNull(evento);
        assertEquals(StatusSolicitacao.EM_ANALISE, solicitacao.getStatus());
        assertEquals(secretariaId, solicitacao.getAnalistaId());
    }

    @Test
    void iniciarAnalise_emStatusDiferenteDePendente_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);

        assertThrows(IllegalStateException.class, () -> solicitacao.iniciarAnalise(secretariaId));
    }

    // Deferimento
    @Test
    void deferir_emAnalise_deveAlterarStatusParaDeferida() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);

        var evento = solicitacao.deferir(secretariaId, "Documentação correta", false);

        assertNotNull(evento);
        assertEquals(StatusSolicitacao.DEFERIDA, solicitacao.getStatus());
        assertNotNull(solicitacao.getDataAnalise());
    }

    @Test
    void deferir_foraDaAnalise_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);

        assertThrows(IllegalStateException.class,
                () -> solicitacao.deferir(secretariaId, "Justificativa", false));
    }

    // Indeferimento
    @Test
    void indeferir_emAnalise_deveAlterarStatusParaIndeferida() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);

        var evento = solicitacao.indeferir(secretariaId, "Documentação insuficiente");

        assertNotNull(evento);
        assertEquals(StatusSolicitacao.INDEFERIDA, solicitacao.getStatus());
    }

    // RN4: conclusão com impacto acadêmico
    @Test
    void concluir_comImpactoSemAlteracoesVinculadas_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.CORRECAO_HISTORICO);
        solicitacao.anexarDocumento(new Documento("documento_comprobatorio", "doc.pdf"));
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.deferir(secretariaId, "Correção necessária", true);

        assertThrows(IllegalStateException.class, solicitacao::concluir);
    }

    @Test
    void concluir_comImpactoComAlteracoesVinculadas_deveAlterarStatusParaConcluida() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.CORRECAO_HISTORICO);
        solicitacao.anexarDocumento(new Documento("documento_comprobatorio", "doc.pdf"));
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.deferir(secretariaId, "Correção necessária", true);
        solicitacao.vincularAlteracoes();

        var evento = solicitacao.concluir();

        assertNotNull(evento);
        assertEquals(StatusSolicitacao.CONCLUIDA, solicitacao.getStatus());
    }

    @Test
    void concluir_semImpacto_deveAlterarStatusParaConcluida() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.deferir(secretariaId, "Aprovado", false);

        var evento = solicitacao.concluir();

        assertNotNull(evento);
        assertEquals(StatusSolicitacao.CONCLUIDA, solicitacao.getStatus());
    }

    // RN6: cancelamento
    @Test
    void cancelar_emPendenteAnalise_deveAlterarStatusParaCancelada() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);

        var evento = solicitacao.cancelar();

        assertNotNull(evento);
        assertEquals(StatusSolicitacao.CANCELADA, solicitacao.getStatus());
    }

    @Test
    void cancelar_emAnalise_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);

        assertThrows(IllegalStateException.class, solicitacao::cancelar);
    }

    @Test
    void cancelar_emDeferida_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);
        solicitacao.deferir(secretariaId, "Ok", false);

        assertThrows(IllegalStateException.class, solicitacao::cancelar);
    }

    // Vinculação de alterações
    @Test
    void vincularAlteracoes_foraDeDeferida_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);

        assertThrows(IllegalStateException.class, solicitacao::vincularAlteracoes);
    }

    // Solicitação de complementação
    @Test
    void solicitarComplementacao_emAnalise_deveAlterarStatusParaPendenteComplementacao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);
        solicitacao.iniciarAnalise(secretariaId);

        solicitacao.solicitarComplementacao(secretariaId);

        assertEquals(StatusSolicitacao.PENDENTE_COMPLEMENTACAO, solicitacao.getStatus());
    }

    @Test
    void solicitarComplementacao_foraDaAnalise_deveLancarExcecao() {
        var solicitacao = criarSolicitacao(TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO);

        assertThrows(IllegalStateException.class,
                () -> solicitacao.solicitarComplementacao(secretariaId));
    }
}
