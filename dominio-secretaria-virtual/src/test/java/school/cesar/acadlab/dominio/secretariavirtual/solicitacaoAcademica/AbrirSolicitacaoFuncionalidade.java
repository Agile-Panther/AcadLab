package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.E;
import school.cesar.acadlab.dominio.secretariavirtual.SecretariaVirtualFuncionalidade;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.Protocolo;
import school.cesar.acadlab.dominio.secretariavirtual.protocolo.ProtocoloId;

public class AbrirSolicitacaoFuncionalidade {
    private final SecretariaVirtualFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(1);
    private TipoSolicitacao tipoSolicitacao;
    private List<Documento> documentos;
    private SolicitacaoAcademica solicitacaoCriada;

    public AbrirSolicitacaoFuncionalidade(SecretariaVirtualFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estudante sem solicitação aberta do tipo {string}")
    public void um_estudante_sem_solicitacao_aberta(String tipo) {
        this.tipoSolicitacao = TipoSolicitacao.valueOf(tipo);
    }

    @Dado("um estudante com solicitação aberta do tipo {string}")
    public void um_estudante_com_solicitacao_aberta(String tipo) {
        this.tipoSolicitacao = TipoSolicitacao.valueOf(tipo);
        ctx.calendarioDentroDoPrazo = true;
        var docs = criarDocumentosPara(tipoSolicitacao);
        ctx.solicitacaoServico.abrirSolicitacao(estudanteId, periodoLetivoId, tipoSolicitacao,
                "Solicitação existente", docs);
    }

    @Dado("os documentos obrigatórios estão anexados")
    public void os_documentos_obrigatorios_estao_anexados() {
        this.documentos = criarDocumentosPara(tipoSolicitacao);
    }

    @Dado("os documentos obrigatórios não estão anexados")
    public void os_documentos_obrigatorios_nao_estao_anexados() {
        this.documentos = List.of();
    }

    @Dado("o prazo do calendário acadêmico está vigente")
    public void o_prazo_esta_vigente() {
        ctx.calendarioDentroDoPrazo = true;
    }

    @Dado("o prazo do calendário acadêmico está encerrado")
    public void o_prazo_esta_encerrado() {
        ctx.calendarioDentroDoPrazo = false;
    }

    @Quando("o estudante abre a solicitação acadêmica")
    public void o_estudante_abre_solicitacao() {
        try {
            solicitacaoCriada = ctx.solicitacaoServico.abrirSolicitacao(
                    estudanteId, periodoLetivoId, tipoSolicitacao,
                    "Solicitação de teste", documentos);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o estudante abre outra solicitação de revisão de nota")
    public void o_estudante_abre_outra_revisao_de_nota() {
        try {
            documentos = criarDocumentosPara(TipoSolicitacao.REVISAO_DE_NOTA);
            solicitacaoCriada = ctx.solicitacaoServico.abrirSolicitacao(
                    estudanteId, periodoLetivoId, TipoSolicitacao.REVISAO_DE_NOTA,
                    "Nova revisão de nota", documentos);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o sistema registra a solicitação com sucesso")
    public void o_sistema_registra_com_sucesso() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertNotNull(solicitacaoCriada);
    }

    @E("a solicitação é criada com status {string}")
    public void a_solicitacao_tem_status(String statusEsperado) {
        assertEquals(StatusSolicitacao.valueOf(statusEsperado), solicitacaoCriada.getStatus());
    }

    @E("um protocolo é gerado para a solicitação")
    public void um_protocolo_e_gerado() {
        assertNotNull(solicitacaoCriada.getProtocolo());
    }

    private List<Documento> criarDocumentosPara(TipoSolicitacao tipo) {
        return tipo.getDocumentosObrigatorios().stream()
                .map(t -> new Documento(t, t + ".pdf"))
                .toList();
    }
}
