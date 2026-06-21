package school.cesar.acadlab.dominio.mobilidadeacademica;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.CoordenadorId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.EstudanteId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.StatusMobilidade;

class MobilidadeAcademicaServicoTest {

    private MobilidadeAcademicaRepositorioTest repositorio;
    private MobilidadeAcademicaServico servico;

    @BeforeEach
    void setUp() {
        repositorio = new MobilidadeAcademicaRepositorioTest();
        servico = new MobilidadeAcademicaServico(repositorio);
    }

    @Test
    void solicitar_persisteMobilidadeSolicitada() {
        MobilidadeAcademicaId id = servico.solicitar(new EstudanteId(1), "Universidade de Coimbra");

        var mobilidade = repositorio.buscarPorId(id).orElseThrow();
        assertEquals(StatusMobilidade.SOLICITADA, mobilidade.getStatus());
    }

    @Test
    void autorizar_orquestraDominioEPersiste() {
        MobilidadeAcademicaId id = servico.solicitar(new EstudanteId(1), "Universidade de Coimbra");

        servico.autorizar(id, new CoordenadorId(10));

        assertEquals(StatusMobilidade.AUTORIZADA, repositorio.buscarPorId(id).orElseThrow().getStatus());
    }

    @Test
    void operarSobreMobilidadeInexistente_lancaExcecao() {
        var excecao = assertThrows(IllegalArgumentException.class,
                () -> servico.autorizar(new MobilidadeAcademicaId(999), new CoordenadorId(10)));
        assertTrue(excecao.getMessage().contains("não encontrada"));
    }
}
