package school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade;

import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class MobilidadeAcademicaTest {

    private final MobilidadeAcademicaId id = new MobilidadeAcademicaId(1);
    private final EstudanteId estudanteId = new EstudanteId(1);
    private final CoordenadorId coordenadorId = new CoordenadorId(1);
    private final SecretariaId secretariaId = new SecretariaId(1);
    private final DisciplinaId disciplinaExterna = new DisciplinaId(10);
    private final DisciplinaId disciplinaEquivalente = new DisciplinaId(20);

    private MobilidadeAcademica novaMobilidade() {
        return new MobilidadeAcademica(id, estudanteId, "MIT");
    }

    private MobilidadeAcademica mobilidadeAutorizada() {
        var m = novaMobilidade();
        m.autorizar(coordenadorId);
        return m;
    }

    private MobilidadeAcademica mobilidadeComItemAutorizado() {
        var m = mobilidadeAutorizada();
        m.adicionarItemPlano(disciplinaExterna, disciplinaEquivalente, 60, 60);
        return m;
    }

    // RN-1: mobilidade não solicitada não pode ser autorizada
    @Test
    void autorizar_mobilidadeJaAutorizada_deveLancarExcecao() {
        var m = mobilidadeAutorizada();
        var excecao = assertThrows(IllegalStateException.class, () -> m.autorizar(coordenadorId));
        assertTrue(excecao.getMessage().contains("RN-1"));
    }

    // RN-1: mobilidade solicitada pode ser autorizada
    @Test
    void autorizar_mobilidadeSolicitada_deveAlterarStatusParaAutorizada() {
        var m = novaMobilidade();
        m.autorizar(coordenadorId);
        assertEquals(StatusMobilidade.AUTORIZADA, m.getStatus());
        assertEquals(coordenadorId, m.getCoordenadorAutorizacao());
    }

    // RN-2: resultado para disciplina não autorizada no plano lança exceção
    @Test
    void registrarResultado_disciplinaNaoAutorizada_deveLancarExcecao() {
        var m = mobilidadeAutorizada();
        var excecao = assertThrows(IllegalStateException.class,
                () -> m.registrarResultado(disciplinaExterna, secretariaId));
        assertNotNull(excecao.getMessage());
    }

    // RN-3: item com carga horária externa insuficiente lança exceção
    @Test
    void adicionarItemPlano_cargaHorariaExternaInsuficiente_deveLancarExcecao() {
        var m = mobilidadeAutorizada();
        var excecao = assertThrows(IllegalStateException.class,
                () -> m.adicionarItemPlano(disciplinaExterna, disciplinaEquivalente, 30, 60));
        assertTrue(excecao.getMessage().contains("RN-3"));
    }

    // RN-3: item com carga horária suficiente é autorizado
    @Test
    void adicionarItemPlano_cargaHorariaSuficiente_deveAutorizarItem() {
        var m = mobilidadeAutorizada();
        m.adicionarItemPlano(disciplinaExterna, disciplinaEquivalente, 60, 60);
        assertEquals(1, m.getPlanoEstudos().size());
        assertEquals(StatusItemPlano.AUTORIZADO, m.getPlanoEstudos().get(0).getStatus());
    }

    // RN-4: registro sem comprovante lança exceção
    @Test
    void registrarResultado_semComprovante_deveLancarExcecao() {
        var m = mobilidadeComItemAutorizado();
        var excecao = assertThrows(IllegalStateException.class,
                () -> m.registrarResultado(disciplinaExterna, secretariaId));
        assertTrue(excecao.getMessage().contains("RN-4"));
    }

    // RN-4: registro com comprovante funciona
    @Test
    void registrarResultado_comComprovante_deveRegistrarResultado() {
        var m = mobilidadeComItemAutorizado();
        m.anexarComprovante(disciplinaExterna);
        m.registrarResultado(disciplinaExterna, secretariaId);
        assertTrue(m.getPlanoEstudos().get(0).isResultadoRegistrado());
    }

    // RN-5: registro sem secretaria (null) lança exceção
    @Test
    void registrarResultado_semSecretaria_deveLancarExcecao() {
        var m = mobilidadeComItemAutorizado();
        m.anexarComprovante(disciplinaExterna);
        var excecao = assertThrows(IllegalStateException.class,
                () -> m.registrarResultado(disciplinaExterna, null));
        assertTrue(excecao.getMessage().contains("RN-5"));
    }

    // RN-7: cancelamento após início do período lança exceção
    @Test
    void solicitarCancelamento_aposInicioPeriodo_deveLancarExcecao() {
        var m = mobilidadeAutorizada();
        LocalDate dataInicio = LocalDate.of(2025, 3, 1);
        m.iniciarPeriodoExterno(dataInicio);
        LocalDate hoje = LocalDate.of(2025, 3, 15);
        var excecao = assertThrows(IllegalStateException.class,
                () -> m.solicitarCancelamento("Motivo qualquer", hoje));
        assertTrue(excecao.getMessage().contains("RN-7"));
    }

    // RN-7: cancelamento antes do início funciona
    @Test
    void solicitarCancelamento_antesInicioPeriodo_devePermitir() {
        var m = novaMobilidade();
        LocalDate hoje = LocalDate.of(2025, 1, 10);
        assertDoesNotThrow(() -> m.solicitarCancelamento("Motivo pessoal", hoje));
        assertEquals("Motivo pessoal", m.getJustificativaCancelamento());
    }

    // RN-8: confirmação de cancelamento sem justificativa lança exceção
    @Test
    void confirmarCancelamento_semJustificativa_deveLancarExcecao() {
        var m = novaMobilidade();
        var excecao = assertThrows(IllegalStateException.class,
                () -> m.confirmarCancelamento(coordenadorId));
        assertTrue(excecao.getMessage().contains("RN-8"));
    }

    // Status final CONCLUIDA quando todos os itens têm resultado
    @Test
    void registrarResultado_todosItens_deveMudarStatusParaConcluida() {
        var m = mobilidadeComItemAutorizado();
        m.anexarComprovante(disciplinaExterna);
        m.registrarResultado(disciplinaExterna, secretariaId);
        assertEquals(StatusMobilidade.CONCLUIDA, m.getStatus());
    }
}
