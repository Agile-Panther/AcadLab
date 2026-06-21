package school.cesar.acadlab.dominio.ofertaturmas.turma;

import static org.junit.jupiter.api.Assertions.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.Professor;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.Sala;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;

public class OfertarTurmaFuncionalidade {

    private final OfertaTurmasFuncionalidade ctx;
    private final PeriodoLetivoId periodoId = new PeriodoLetivoId(1);
    private final DisciplinaId disciplinaId = new DisciplinaId(1);
    private Turma turmaCriada;
    private SalaId salaId;
    private ProfessorId professorId;

    public OfertarTurmaFuncionalidade(OfertaTurmasFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um período letivo e uma disciplina disponíveis")
    public void periodo_e_disciplina_disponiveis() {
        salaId = ctx.salaRepositorio.proximoId();
        ctx.salaRepositorio.salvar(new Sala(salaId, "Sala 101", 40));
        professorId = ctx.professorRepositorio.proximoId();
        ctx.professorRepositorio.salvar(new Professor(professorId, "Prof. Silva"));
    }

    @Quando("a coordenação oferta uma turma para a disciplina")
    public void coordenacao_oferta_turma() {
        try {
            turmaCriada = ctx.ofertaTurmaServico.ofertar(periodoId, disciplinaId, ModalidadeTurma.PRESENCIAL, 30);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a turma é criada com status planejada")
    public void turma_criada_planejada() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertNotNull(turmaCriada);
        assertEquals(StatusTurma.PLANEJADA, turmaCriada.getStatus());
    }

    @Quando("a coordenação vincula professor, sala e horário à turma e confirma a oferta")
    public void configurar_e_ofertar_turma() {
        try {
            turmaCriada = ctx.ofertaTurmaServico.ofertar(periodoId, disciplinaId, ModalidadeTurma.PRESENCIAL, 30);
            ctx.ofertaTurmaServico.vincularProfessor(turmaCriada.getId(), professorId);
            ctx.ofertaTurmaServico.vincularSala(turmaCriada.getId(), salaId);
            ctx.ofertaTurmaServico.adicionarHorario(turmaCriada.getId(),
                    DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(10, 0));
            ctx.ofertaTurmaServico.confirmarOferta(turmaCriada.getId());
            turmaCriada = ctx.consultaTurmaServico.buscar(turmaCriada.getId());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a turma é ofertada com status ofertada")
    public void turma_com_status_ofertada() {
        assertNull(ctx.excecao, "Não deveria ter lançado exceção");
        assertEquals(StatusTurma.OFERTADA, turmaCriada.getStatus());
    }

    @Quando("a coordenação vincula uma sala com capacidade insuficiente à turma")
    public void vincular_sala_insuficiente() {
        try {
            turmaCriada = ctx.ofertaTurmaServico.ofertar(periodoId, disciplinaId, ModalidadeTurma.PRESENCIAL, 50);
            ctx.ofertaTurmaServico.vincularSala(turmaCriada.getId(), salaId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
