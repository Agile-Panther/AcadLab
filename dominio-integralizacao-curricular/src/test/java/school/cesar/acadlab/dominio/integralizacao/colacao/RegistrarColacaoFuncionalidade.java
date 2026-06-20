package school.cesar.acadlab.dominio.integralizacao.colacao;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoFuncionalidade;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

public class RegistrarColacaoFuncionalidade {

    private final IntegralizacaoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final MatrizCurricularId matrizId = new MatrizCurricularId(1);
    private final CoordenadorId coordenadorId = new CoordenadorId(1);
    private IntegralizacaoId integralizacaoId;
    private ColacaoDeGrau colacaoRegistrada;

    public RegistrarColacaoFuncionalidade(IntegralizacaoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um estudante com aptidão formalmente aprovada")
    public void estudante_com_aptidao_aprovada() {
        var integralizacao = ctx.integralizacaoServico.iniciarAnalise(estudanteId, matrizId);
        integralizacaoId = integralizacao.getId();
        var itens = List.of(
                new ItemChecklist(TipoItemChecklist.DISCIPLINAS_OBRIGATORIAS, "Cumpridas", true),
                new ItemChecklist(TipoItemChecklist.HORAS_COMPLEMENTARES, "Cumpridas", true)
        );
        ctx.integralizacaoServico.gerarChecklist(integralizacaoId, itens);
        ctx.integralizacaoServico.registrarResultado(integralizacaoId, StatusIntegralizacao.APTO);
        ctx.integralizacaoServico.aprovarAptidao(integralizacaoId, coordenadorId);
    }

    @Dado("um estudante sem aptidão aprovada")
    public void estudante_sem_aptidao() {
        var integralizacao = ctx.integralizacaoServico.iniciarAnalise(estudanteId, matrizId);
        integralizacaoId = integralizacao.getId();
    }

    @Quando("a secretaria registra a colação de grau com data válida")
    public void registrar_colacao_data_valida() {
        try {
            colacaoRegistrada = ctx.colacaoServico.registrar(
                    integralizacaoId, LocalDate.now().plusMonths(1), "Auditório Central");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("a secretaria tenta registrar colação sem aptidão aprovada")
    public void registrar_colacao_sem_aptidao() {
        try {
            colacaoRegistrada = ctx.colacaoServico.registrar(
                    integralizacaoId, LocalDate.now().plusMonths(1), "Auditório Central");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a colação de grau é registrada com sucesso")
    public void colacao_registrada() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertNotNull(colacaoRegistrada);
        assertNotNull(colacaoRegistrada.getDataCerimonia());
    }
}
