package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class RegistrarAproveitamentoFuncionalidade extends HistoricoFuncionalidade {
    private HistoricoAcademico historico;
    private RuntimeException excecao;

    @Dado("um histórico de estudante para registro de aproveitamento")
    public void historicoParaAproveitamento() {
        historico = new HistoricoAcademico(
                repositorio.proximoId(),
                new EstudanteId(4),
                new MatrizCurricularId(1));
        repositorio.salvar(historico);
    }

    @Quando("a secretaria registra aproveitamento com carga horária externa igual à requerida")
    public void registraAproveitamentoCargaCompativel() {
        try {
            historico.registrarAproveitamento(
                    repositorio.proximoAproveitamentoId(),
                    new DisciplinaId(10),
                    60, 60,
                    "Universidade Federal",
                    "Cálculo I");
            repositorio.salvar(historico);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o aproveitamento é adicionado ao histórico")
    public void aproveitamentoAdicionado() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        assertEquals(1, historico.getAproveitamentos().size());
    }

    @Quando("a secretaria tenta registrar aproveitamento com carga horária insuficiente")
    public void tentaAproveitamentoCargaInsuficiente() {
        try {
            historico.registrarAproveitamento(
                    repositorio.proximoAproveitamentoId(),
                    new DisciplinaId(10),
                    40, 60,
                    "Universidade Federal",
                    "Cálculo I");
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita o aproveitamento informando RN-7")
    public void sistemaRejeitaRN7() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-7"));
    }
}
