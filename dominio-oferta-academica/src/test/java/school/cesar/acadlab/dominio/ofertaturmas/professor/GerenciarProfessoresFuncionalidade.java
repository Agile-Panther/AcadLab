package school.cesar.acadlab.dominio.ofertaturmas.professor;

import static org.junit.jupiter.api.Assertions.*;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;
import school.cesar.acadlab.dominio.ofertaturmas.DisciplinaId;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmasFuncionalidade;
import school.cesar.acadlab.dominio.ofertaturmas.PeriodoLetivoId;
import school.cesar.acadlab.dominio.ofertaturmas.turma.ModalidadeTurma;

public class GerenciarProfessoresFuncionalidade extends OfertaTurmasFuncionalidade {

    private ProfessorId professorId;
    private RuntimeException excecao;

    @Dado("um professor ativo cadastrado")
    public void professor_ativo_cadastrado() {
        professorId = professorRepositorio.proximoId();
        professorRepositorio.salvar(new Professor(professorId, "Prof. Santos"));
    }

    @Quando("a secretaria inativa o professor")
    public void secretaria_inativa_professor() {
        try {
            var professor = professorRepositorio.obter(professorId);
            professor.inativar();
            professorRepositorio.salvar(professor);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o professor passa a ter status inativo")
    public void professor_inativo() {
        assertNull(excecao, "Não deveria ter lançado exceção");
        var professor = professorRepositorio.obter(professorId);
        assertFalse(professor.isAtivo());
    }

    @Quando("a coordenação tenta vincular o professor inativo a uma turma")
    public void vincular_professor_inativo() {
        try {
            var professor = professorRepositorio.obter(professorId);
            professor.inativar();
            professorRepositorio.salvar(professor);
            var turma = ofertaTurmaServico.ofertar(
                    new PeriodoLetivoId(1), new DisciplinaId(1), ModalidadeTurma.PRESENCIAL, 30);
            ofertaTurmaServico.vincularProfessor(turma.getId(), professorId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o sistema rejeita a vinculação do professor inativo")
    public void rejeitar_professor_inativo() {
        assertNotNull(excecao);
        assertInstanceOf(IllegalStateException.class, excecao);
    }
}
