package school.cesar.acadlab.dominio.apoiopsicopedagogico.caso;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioPsicopedagogicoFuncionalidade;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.estudante.EstudanteId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.profissional.PsicopedagogoId;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.PrioridadeTriagem;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.triagem.Triagem;

public class ConsultarCasosFuncionalidade {
    private final ApoioPsicopedagogicoFuncionalidade ctx;
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final PsicopedagogoId responsavelId = new PsicopedagogoId(1);
    private final PsicopedagogoId outroResponsavelId = new PsicopedagogoId(2);
    private List<Caso> resultado;

    public ConsultarCasosFuncionalidade(ApoioPsicopedagogicoFuncionalidade ctx) {
        this.ctx = ctx;
    }

    private void criarCasoComResponsavel(PsicopedagogoId responsavel) {
        var casoId = ctx.repositorio.proximoId();
        var caso = new Caso(casoId, estudanteId);
        var triagem = new Triagem(PrioridadeTriagem.BAIXA, "Triagem", responsavel, LocalDate.now());
        caso.realizarTriagem(triagem);
        ctx.repositorio.salvar(caso);
    }

    @Dado("um psicopedagogo com casos atribuídos a ele")
    public void um_psicopedagogo_com_casos_atribuidos() {
        criarCasoComResponsavel(responsavelId);
        criarCasoComResponsavel(responsavelId);
    }

    @Dado("um psicopedagogo com casos atribuídos a outro profissional")
    public void casos_atribuidos_a_outro_profissional() {
        criarCasoComResponsavel(outroResponsavelId);
    }

    @Quando("o psicopedagogo consulta o histórico de casos")
    public void o_psicopedagogo_consulta_historico() {
        resultado = ctx.consultaServico.listarCasosPorResponsavel(responsavelId);
    }

    @Entao("o sistema retorna apenas os casos nos quais o profissional é responsável")
    public void o_sistema_retorna_casos_do_responsavel() {
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertTrue(resultado.stream().allMatch(c -> responsavelId.equals(c.getResponsavelId())));
    }

    @Entao("o sistema retorna uma lista vazia de casos")
    public void o_sistema_retorna_lista_vazia() {
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}
