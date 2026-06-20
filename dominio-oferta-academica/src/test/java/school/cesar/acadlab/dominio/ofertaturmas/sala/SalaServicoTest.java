package school.cesar.acadlab.dominio.ofertaturmas.sala;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.ofertaturmas.SalaRepositorioTest;

class SalaServicoTest {

    private SalaRepositorioTest repositorio;
    private SalaServico servico;

    @BeforeEach
    void setUp() {
        repositorio = new SalaRepositorioTest();
        servico = new SalaServico(repositorio);
    }

    @Test
    void cadastrar_devePersistirSalaAtiva() {
        Sala sala = servico.cadastrar("Lab 1", 40);

        Sala persistida = repositorio.obter(sala.getId());
        assertTrue(persistida.isAtiva());
        assertEquals(40, persistida.getCapacidade());
    }

    @Test
    void inativar_devePersistirSalaInativa() {
        Sala sala = servico.cadastrar("Lab 1", 40);

        servico.inativar(sala.getId());

        assertFalse(repositorio.obter(sala.getId()).isAtiva());
    }

    @Test
    void alterarCapacidade_devePersistirNovaCapacidade() {
        Sala sala = servico.cadastrar("Lab 1", 40);

        servico.alterarCapacidade(sala.getId(), 60, 30);

        assertEquals(60, repositorio.obter(sala.getId()).getCapacidade());
    }

    @Test
    void alterarCapacidade_abaixoDaTurmaVinculada_deveLancarExcecao() {
        Sala sala = servico.cadastrar("Lab 1", 40);

        assertThrows(IllegalStateException.class,
                () -> servico.alterarCapacidade(sala.getId(), 20, 30));
    }

    @Test
    void operarSobreSalaInexistente_deveLancarExcecao() {
        var excecao = assertThrows(IllegalArgumentException.class,
                () -> servico.inativar(new SalaId(999)));
        assertTrue(excecao.getMessage().contains("não encontrada"));
    }
}
