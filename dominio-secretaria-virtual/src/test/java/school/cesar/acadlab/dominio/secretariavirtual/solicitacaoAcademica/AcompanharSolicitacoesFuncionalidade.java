package school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.secretariavirtual.SecretariaVirtualFuncionalidade;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;

public class AcompanharSolicitacoesFuncionalidade {
    private final SecretariaVirtualFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(30);
    private final PeriodoLetivoId periodoLetivoId = new PeriodoLetivoId(30);
    private List<SolicitacaoAcademica> resultado;

    public AcompanharSolicitacoesFuncionalidade(SecretariaVirtualFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estudante com solicitações cadastradas")
    public void um_estudante_com_solicitacoes() {
        ctx.calendarioDentroDoPrazo = true;
        ctx.solicitacaoServico.abrirSolicitacao(estudanteId, periodoLetivoId,
                TipoSolicitacao.SEGUNDA_VIA_DOCUMENTO, "Solicitação 1", List.of());
        ctx.solicitacaoServico.abrirSolicitacao(estudanteId, periodoLetivoId,
                TipoSolicitacao.REVISAO_DE_NOTA,
                "Solicitação 2", List.of(new Documento("comprovante_avaliacao", "prova.pdf")));
    }

    @Dado("um estudante sem solicitações cadastradas")
    public void um_estudante_sem_solicitacoes() {
        // repositório começa vazio
    }

    @Quando("o estudante consulta suas solicitações")
    public void o_estudante_consulta() {
        resultado = ctx.consultaServico.listarPorEstudante(estudanteId);
    }

    @Entao("o sistema retorna a lista de solicitações do estudante")
    public void o_sistema_retorna_lista() {
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
    }

    @Entao("o sistema retorna uma lista vazia")
    public void o_sistema_retorna_lista_vazia() {
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
