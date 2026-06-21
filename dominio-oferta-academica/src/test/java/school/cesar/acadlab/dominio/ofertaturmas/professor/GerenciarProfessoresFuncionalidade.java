package school.cesar.acadlab.dominio.ofertaturmas.professor;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;

public class GerenciarProfessoresFuncionalidade {

    private final OfertaTurmasFuncionalidade ctx;
    private ProfessorId professorId;

    public GerenciarProfessoresFuncionalidade(OfertaTurmasFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("um professor ativo cadastrado")
    public void professor_ativo_cadastrado() {
        professorId = ctx.professorRepositorio.proximoId();
        ctx.professorRepositorio.salvar(new Professor(professorId, "Prof. Santos"));
    }

    @Quando("a secretaria inativa o professor")
    public void secretaria_inativa_professor() {
        var professor = ctx.professorRepositorio.obter(professorId);
        professor.inativar();
        ctx.professorRepositorio.salvar(professor);
    }

    @Entao("o professor passa a ter status inativo")
    public void professor_inativo() {
        var professor = ctx.professorRepositorio.obter(professorId);
        assertFalse(professor.isAtivo());
    }

    @Quando("a coordenação tenta vincular o professor inativo a uma turma")
    public void vincular_professor_inativo() {
        try {
            var professor = ctx.professorRepositorio.obter(professorId);
            professor.inativar();
            ctx.professorRepositorio.salvar(professor);
            var turma = ctx.ofertaTurmaServico.ofertar(
                    new PeriodoLetivoId(1), new DisciplinaId(1), ModalidadeTurma.PRESENCIAL, 30);
            ctx.ofertaTurmaServico.vincularProfessor(turma.getId(), professorId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
