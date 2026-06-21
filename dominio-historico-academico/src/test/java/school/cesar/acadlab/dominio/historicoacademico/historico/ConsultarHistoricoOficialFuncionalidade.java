package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import java.util.List;
import school.cesar.acadlab.dominio.historicoacademico.ConsultaHistoricoServico;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class ConsultarHistoricoOficialFuncionalidade {

    private final HistoricoFuncionalidade ctx;
    private HistoricoAcademico historico;
    private List<RegistroDisciplina> resultado;
    private final ConsultaHistoricoServico consultaServico;

    public ConsultarHistoricoOficialFuncionalidade(HistoricoFuncionalidade ctx) {
        this.ctx = ctx;
        this.consultaServico = new ConsultaHistoricoServico(ctx.repositorio);
    }

    @Dado("um histórico com um registro consolidado de turma encerrada")
    public void historicoComRegistroConsolidado() {
        historico = new HistoricoAcademico(
                ctx.repositorio.proximoId(),
                new EstudanteId(1),
                new MatrizCurricularId(1));
        historico.consolidarRegistro(
                ctx.repositorio.proximoRegistroId(),
                new DisciplinaId(1),
                new TurmaId(1),
                new PeriodoLetivoId(1),
                8.5, 85.0,
                SituacaoAcademica.APROVADO,
                true);
        ctx.repositorio.salvar(historico);
    }

    @Quando("o sistema consulta o histórico oficial do estudante")
    public void consultaHistoricoOficial() {
        try {
            resultado = consultaServico.obterHistoricoOficial(new EstudanteId(1));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("o histórico oficial contém {int} registro consolidado")
    public void historicoOficialContemRegistros(int quantidade) {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(quantidade, resultado.size());
    }

    @Dado("que não existe histórico cadastrado para o estudante")
    public void semHistoricoParaEstudante() {
        // repositório começa vazio
    }

    @Quando("o sistema tenta consultar o histórico oficial do estudante")
    public void tentaConsultarHistoricoOficial() {
        try {
            resultado = consultaServico.obterHistoricoOficial(new EstudanteId(99));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
