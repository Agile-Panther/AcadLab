package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.EstudanteId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaComListaEspera;
import school.cesar.acadlab.dominio.ofertaturmas.turma.decorator.TurmaOnline;

public class TurmaDecoradorFuncionalidade {

    private final OfertaTurmasFuncionalidade ctx;
    private TurmaComListaEspera turmaComLista;
    private TurmaOnline turmaOnline;

    public TurmaDecoradorFuncionalidade(OfertaTurmasFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma turma ofertada com lista de espera habilitada")
    public void turma_com_lista_espera() {
        var turma = new Turma(new TurmaId(10), new PeriodoLetivoId(1),
                new DisciplinaId(1), ModalidadeTurma.PRESENCIAL, 30);
        turmaComLista = new TurmaComListaEspera(turma);
    }

    @Quando("o estudante de id {int} entra na lista de espera")
    public void estudante_entra_lista(int id) {
        try {
            turmaComLista.entrarListaEspera(new EstudanteId(id));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a lista de espera contém {int} estudante")
    public void lista_contem_estudante(int quantidade) {
        assertNull(ctx.excecao);
        assertEquals(quantidade, turmaComLista.getListaEspera().size());
    }

    @E("o estudante de id {int} tenta entrar novamente")
    public void estudante_tenta_entrar_novamente(int id) {
        try {
            turmaComLista.entrarListaEspera(new EstudanteId(id));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Dado("uma turma EAD com decorator online")
    public void turma_ead_com_decorator() {
        var turma = new Turma(new TurmaId(11), new PeriodoLetivoId(1),
                new DisciplinaId(1), ModalidadeTurma.EAD, 100);
        turmaOnline = new TurmaOnline(turma);
    }

    @Quando("o link de acesso é definido como {string}")
    public void definir_link(String link) {
        turmaOnline.definirLinkAcesso(link);
    }

    @Entao("o link de acesso da turma online é {string}")
    public void verificar_link(String link) {
        assertEquals(link, turmaOnline.getLinkAcesso());
    }
}
