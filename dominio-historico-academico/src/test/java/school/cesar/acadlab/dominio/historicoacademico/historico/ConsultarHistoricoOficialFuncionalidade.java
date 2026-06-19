package school.cesar.acadlab.dominio.historicoacademico.historico;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import java.util.List;
import school.cesar.acadlab.dominio.historicoacademico.ConsultaHistoricoServico;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoFuncionalidade;

public class ConsultarHistoricoOficialFuncionalidade extends HistoricoFuncionalidade {

    private HistoricoAcademico historico;
    private List<RegistroDisciplina> resultado;
    private RuntimeException excecao;
    private final ConsultaHistoricoServico consultaServico;

    public ConsultarHistoricoOficialFuncionalidade() {
        consultaServico = new ConsultaHistoricoServico(repositorio);
    }

    @Dado("um histórico com um registro consolidado de turma encerrada")
    public void historicoComRegistroConsolidado() {
        historico = new HistoricoAcademico(
                repositorio.proximoId(),
                new EstudanteId(1),
                new MatrizCurricularId(1));
        historico.consolidarRegistro(
                repositorio.proximoRegistroId(),
                new DisciplinaId(1),
                new TurmaId(1),
                new PeriodoLetivoId(1),
                8.5, 85.0,
                SituacaoAcademica.APROVADO,
                true);
        repositorio.salvar(historico);
    }

    @Quando("o sistema consulta o histórico oficial do estudante")
    public void consultaHistoricoOficial() {
        try {
            resultado = consultaServico.obterHistoricoOficial(new EstudanteId(1));
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o histórico oficial contém {int} registro consolidado")
    public void historicoOficialContemRegistros(int quantidade) {
        assertNull(excecao, "Não deveria ter lançado exceção");
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
            excecao = e;
        }
    }

    @Entao("o sistema informa que o estudante não possui histórico")
    public void sistemaInformaQueNaoPossuiHistorico() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalArgumentException.class, excecao);
    }
}
