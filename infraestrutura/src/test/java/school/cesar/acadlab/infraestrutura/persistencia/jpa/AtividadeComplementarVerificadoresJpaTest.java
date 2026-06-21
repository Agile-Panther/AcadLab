package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarId;
import school.cesar.acadlab.dominio.atividadescomplementares.CategoriaAtividadeId;
import school.cesar.acadlab.dominio.atividadescomplementares.EstudanteId;
import school.cesar.acadlab.dominio.atividadescomplementares.StatusAtividade;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorCertificadoDuplicado;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorContabilizacaoIntegralizacao;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorLimiteCategoria;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorVinculoEstudante;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;

class AtividadeComplementarVerificadoresJpaTest {

    private AtividadeComplementarJpaRepository atividadeRepository;
    private MatriculaJpaRepository matriculaRepository;
    private CategoriaAtividadeJpaRepository categoriaRepository;
    private AtividadeComplementarRepositorioImpl adapter;

    @BeforeEach
    void configurar() throws Exception {
        atividadeRepository = mock(AtividadeComplementarJpaRepository.class);
        matriculaRepository = mock(MatriculaJpaRepository.class);
        categoriaRepository = mock(CategoriaAtividadeJpaRepository.class);
        adapter = new AtividadeComplementarRepositorioImpl();
        adapter.repositorio = atividadeRepository;
        definirCampo(adapter, "matriculaRepositorio", matriculaRepository);
        definirCampo(adapter, "categoriaRepositorio", categoriaRepository);
    }

    @Test
    void reconheceVinculoQuandoExisteMatriculaConfirmada() {
        var matricula = new MatriculaJpa();
        matricula.estudanteId = 1;
        matricula.status = StatusMatricula.CONFIRMADA;
        when(matriculaRepository.findByEstudanteId(1)).thenReturn(List.of(matricula));

        var verificador = (VerificadorVinculoEstudante) adapter;

        assertTrue(verificador.estaNoVinculo(new EstudanteId(1), LocalDate.of(2026, 6, 20)));
    }

    @Test
    void detectaCertificadoReutilizadoPeloMesmoEstudante() {
        var atividade = atividade(1, 1, StatusAtividade.PENDENTE, 20, 0);
        atividade.identificadorCertificado = "certificado.pdf";
        when(atividadeRepository.findByEstudanteId(1)).thenReturn(List.of(atividade));

        var verificador = (VerificadorCertificadoDuplicado) adapter;

        assertTrue(verificador.jaUtilizado(new EstudanteId(1), "certificado.pdf"));
        assertFalse(verificador.jaUtilizado(new EstudanteId(1), "outro.pdf"));
    }

    @Test
    void detectaQuandoHorasAprovadasExcedemLimiteDaCategoria() {
        var categoria = new CategoriaAtividadeJpa();
        categoria.id = 3;
        categoria.limiteHoras = 80;
        when(categoriaRepository.findById(3)).thenReturn(Optional.of(categoria));
        when(atividadeRepository.findByEstudanteId(1)).thenReturn(List.of(
                atividade(1, 3, StatusAtividade.DEFERIDA, 50, 50),
                atividade(2, 3, StatusAtividade.PENDENTE, 20, 0)));

        var verificador = (VerificadorLimiteCategoria) adapter;

        assertTrue(verificador.excedeLimite(new EstudanteId(1), new CategoriaAtividadeId(3), 31));
        assertFalse(verificador.excedeLimite(new EstudanteId(1), new CategoriaAtividadeId(3), 30));
    }

    @Test
    void informaSeAtividadeJaFoiContabilizadaNaIntegralizacao() throws Exception {
        var atividade = atividade(7, 3, StatusAtividade.DEFERIDA, 20, 20);
        definirCampo(atividade, "contabilizadaIntegralizacao", true);
        when(atividadeRepository.findById(7)).thenReturn(Optional.of(atividade));

        var verificador = (VerificadorContabilizacaoIntegralizacao) adapter;

        assertTrue(verificador.foiContabilizada(new AtividadeComplementarId(7)));
    }

    @Test
    void incluiDadosEnviadosNoResumoDaFila() {
        var atividade = atividade(7, 3, StatusAtividade.PENDENTE, 20, 0);
        atividade.dataRealizacao = LocalDate.of(2026, 6, 20);
        atividade.identificadorCertificado = "certificado.pdf";
        when(atividadeRepository.findByStatus(StatusAtividade.PENDENTE)).thenReturn(List.of(atividade));

        var resumo = adapter.pesquisarPorStatus("PENDENTE").get(0);

        assertEquals(LocalDate.of(2026, 6, 20), resumo.dataRealizacao());
        assertEquals("certificado.pdf", resumo.identificadorCertificado());
    }

    private static AtividadeComplementarJpa atividade(
            int id, int categoriaId, StatusAtividade status, int submetidas, int aprovadas) {
        var atividade = new AtividadeComplementarJpa();
        atividade.id = id;
        atividade.estudanteId = 1;
        atividade.categoriaId = categoriaId;
        atividade.status = status;
        atividade.horasSubmetidas = submetidas;
        atividade.horasAprovadas = aprovadas;
        return atividade;
    }

    private static void definirCampo(Object alvo, String nome, Object valor) throws Exception {
        Field campo = alvo.getClass().getDeclaredField(nome);
        campo.setAccessible(true);
        campo.set(alvo, valor);
    }
}
