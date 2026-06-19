package school.cesar.acadlab.dominio.estagios.oportunidade;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OportunidadeTest {

    private OportunidadeId id;
    private EmpresaId empresaId;
    private SetorEstagiosId setorId;

    @BeforeEach
    void setUp() {
        id = new OportunidadeId(1);
        empresaId = new EmpresaId(10);
        setorId = new SetorEstagiosId(1);
    }

    @Test
    void deveCriarOportunidadeCadastrada() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        assertEquals(StatusOportunidade.CADASTRADA, oportunidade.getStatus());
    }

    @Test
    void devePublicarOportunidade() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.publicar(setorId);
        assertEquals(StatusOportunidade.PUBLICADA, oportunidade.getStatus());
    }

    @Test
    void deveRejeitarPublicacaoDeOportunidadeJaPublicada() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.publicar(setorId);
        var ex = assertThrows(IllegalStateException.class, () -> oportunidade.publicar(setorId));
        assertTrue(ex.getMessage().contains("publicação só pode ser realizada em oportunidades cadastradas"));
    }

    @Test
    void deveEncerrarOportunidadePublicada() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.publicar(setorId);
        oportunidade.encerrar(MotivoEncerramento.VAGAS_PREENCHIDAS);
        assertEquals(StatusOportunidade.ENCERRADA, oportunidade.getStatus());
    }

    @Test
    void deveRejeitarEncerrarOportunidadeNaoPublicada() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.encerrar(MotivoEncerramento.DECISAO_ADMINISTRATIVA));
        assertTrue(ex.getMessage().contains("somente oportunidades publicadas podem ser encerradas"));
    }

    @Test
    void deveRejeitarCandidaturaEmOportunidadeNaoPublicada() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.validarCandidatura(new EstudanteId(20)));
        assertTrue(ex.getMessage().contains("oportunidade não está disponível para candidaturas"));
    }

    @Test
    void devePermitirCandidaturaEmOportunidadePublicada() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.publicar(setorId);
        assertDoesNotThrow(() -> oportunidade.validarCandidatura(new EstudanteId(20)));
    }

    @Test
    void deveDefinirCriteriosAntesDePublicar() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        var criterio = new CriterioElegibilidade("Sistemas de Informação", 4, true);
        oportunidade.definirCriterios(setorId, criterio);
        assertNotNull(oportunidade.getCriterioElegibilidade());
    }

    @Test
    void deveRejeitarAlteracaoDeCriteriosAposPublicacao() {
        var oportunidade = new Oportunidade(id, empresaId, "Estágio em TI", 480);
        oportunidade.publicar(setorId);
        var criterio = new CriterioElegibilidade("Sistemas de Informação", 4, true);
        var ex = assertThrows(IllegalStateException.class,
                () -> oportunidade.definirCriterios(setorId, criterio));
        assertTrue(ex.getMessage().contains("critérios não podem ser alterados após a publicação"));
    }
}
