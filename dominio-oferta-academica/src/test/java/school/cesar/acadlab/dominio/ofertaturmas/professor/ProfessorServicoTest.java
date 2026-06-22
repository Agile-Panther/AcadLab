package school.cesar.acadlab.dominio.ofertaturmas.professor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.ofertaturmas.ProfessorRepositorioTest;

class ProfessorServicoTest {

    private ProfessorRepositorioTest repositorio;
    private ProfessorServico servico;

    @BeforeEach
    void setUp() {
        repositorio = new ProfessorRepositorioTest();
        servico = new ProfessorServico(repositorio);
    }

    @Test
    void cadastrar_devePersistirProfessorAtivo() {
        Professor professor = servico.cadastrar("Ada Lovelace");

        Professor persistido = repositorio.obter(professor.getId());
        assertTrue(persistido.isAtivo());
    }

    @Test
    void inativar_devePersistirProfessorInativo() {
        Professor professor = servico.cadastrar("Ada Lovelace");

        servico.inativar(professor.getId());

        assertFalse(repositorio.obter(professor.getId()).isAtivo());
    }

    @Test
    void ativar_devePersistirProfessorAtivo() {
        Professor professor = servico.cadastrar("Ada Lovelace");
        servico.inativar(professor.getId());

        servico.ativar(professor.getId());

        assertTrue(repositorio.obter(professor.getId()).isAtivo());
    }

    @Test
    void operarSobreProfessorInexistente_deveLancarExcecao() {
        var excecao = assertThrows(IllegalArgumentException.class,
                () -> servico.inativar(new ProfessorId(999)));
        assertTrue(excecao.getMessage().contains("não encontrado"));
    }
}
