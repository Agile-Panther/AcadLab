package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class RegistrarAcompanhamentoFuncionalidade extends HistoricoFuncionalidade {
    private HistoricoAcademico historico;
    private RuntimeException excecao;

    @Dado("um histórico de estudante para acompanhamento acadêmico")
    public void historicoParaAcompanhamento() {
        historico = new HistoricoAcademico(
                repositorio.proximoId(),
                new EstudanteId(2),
                new MatrizCurricularId(1));
        repositorio.salvar(historico);
    }

    @Quando("o coordenador registra acompanhamento para estudante com vínculo ativo")
    public void registraAcompanhamentoVinculoAtivo() {
        try {
            historico.registrarAcompanhamento(
                    repositorio.proximoAcompanhamentoId(),
                    "Estudante apresenta dificuldades em cálculo",
                    LocalDate.of(2025, 5, 10),
                    true);
            repositorio.salvar(historico);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o acompanhamento é adicionado ao histórico")
    public void acompanhamentoAdicionado() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        assertEquals(1, historico.getAcompanhamentos().size());
    }

    @Quando("o coordenador tenta registrar acompanhamento para estudante sem vínculo")
    public void tentaRegistrarSemVinculo() {
        try {
            historico.registrarAcompanhamento(
                    repositorio.proximoAcompanhamentoId(),
                    "Observação",
                    LocalDate.of(2025, 5, 10),
                    false);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita o acompanhamento informando RN-4")
    public void sistemaRejeitaRN4() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
        assertTrue(excecao.getMessage().contains("RN-4"));
    }
}
