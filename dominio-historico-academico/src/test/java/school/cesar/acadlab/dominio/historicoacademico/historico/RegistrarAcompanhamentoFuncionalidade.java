package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class RegistrarAcompanhamentoFuncionalidade {
    private final HistoricoFuncionalidade ctx;
    private HistoricoAcademico historico;

    public RegistrarAcompanhamentoFuncionalidade(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um histórico de estudante para acompanhamento acadêmico")
    public void historicoParaAcompanhamento() {
        historico = new HistoricoAcademico(
                ctx.repositorio.proximoId(),
                new EstudanteId(2),
                new MatrizCurricularId(1));
        ctx.repositorio.salvar(historico);
    }

    @Quando("o coordenador registra acompanhamento para estudante com vínculo ativo")
    public void registraAcompanhamentoVinculoAtivo() {
        try {
            historico.registrarAcompanhamento(
                    ctx.repositorio.proximoAcompanhamentoId(),
                    "Estudante apresenta dificuldades em cálculo",
                    LocalDate.of(2025, 5, 10),
                    true);
            ctx.repositorio.salvar(historico);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o acompanhamento é adicionado ao histórico")
    public void acompanhamentoAdicionado() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(1, historico.getAcompanhamentos().size());
    }

    @Quando("o coordenador tenta registrar acompanhamento para estudante sem vínculo")
    public void tentaRegistrarSemVinculo() {
        try {
            historico.registrarAcompanhamento(
                    ctx.repositorio.proximoAcompanhamentoId(),
                    "Observação",
                    LocalDate.of(2025, 5, 10),
                    false);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
