package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class RegistrarAproveitamentoFuncionalidade {
    private final HistoricoFuncionalidade ctx;
    private HistoricoAcademico historico;

    public RegistrarAproveitamentoFuncionalidade(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um histórico de estudante para registro de aproveitamento")
    public void historicoParaAproveitamento() {
        historico = new HistoricoAcademico(
                ctx.repositorio.proximoId(),
                new EstudanteId(4),
                new MatrizCurricularId(1));
        ctx.repositorio.salvar(historico);
    }

    @Quando("a secretaria registra aproveitamento com carga horária externa igual à requerida")
    public void registraAproveitamentoCargaCompativel() {
        try {
            historico.registrarAproveitamento(
                    ctx.repositorio.proximoAproveitamentoId(),
                    new DisciplinaId(10),
                    60, 60,
                    "Universidade Federal",
                    "Cálculo I");
            ctx.repositorio.salvar(historico);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o aproveitamento é adicionado ao histórico")
    public void aproveitamentoAdicionado() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(1, historico.getAproveitamentos().size());
    }

    @Quando("a secretaria tenta registrar aproveitamento com carga horária insuficiente")
    public void tentaAproveitamentoCargaInsuficiente() {
        try {
            historico.registrarAproveitamento(
                    ctx.repositorio.proximoAproveitamentoId(),
                    new DisciplinaId(10),
                    40, 60,
                    "Universidade Federal",
                    "Cálculo I");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
