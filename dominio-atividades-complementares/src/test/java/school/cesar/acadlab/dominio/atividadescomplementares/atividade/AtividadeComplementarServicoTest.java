package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtividadeComplementarServicoTest {

    private AtividadeComplementarRepositorio repositorio;
    private VerificadorVinculoEstudante verificadorVinculo;
    private VerificadorCertificadoDuplicado verificadorCertificado;
    private VerificadorLimiteCategoria verificadorLimite;
    private VerificadorContabilizacaoIntegralizacao verificadorContabilizacao;
    private AtividadeComplementarServico servico;

    @BeforeEach
    void setUp() {
        repositorio = mock(AtividadeComplementarRepositorio.class);
        verificadorVinculo = mock(VerificadorVinculoEstudante.class);
        verificadorCertificado = mock(VerificadorCertificadoDuplicado.class);
        verificadorLimite = mock(VerificadorLimiteCategoria.class);
        verificadorContabilizacao = mock(VerificadorContabilizacaoIntegralizacao.class);
        servico = new AtividadeComplementarServico(repositorio, verificadorVinculo,
                verificadorCertificado, verificadorLimite, verificadorContabilizacao);
        when(repositorio.proximoId()).thenReturn(new AtividadeComplementarId(1));
    }

    @Test
    void submeter_comVinculoValido_deveSalvarAtividade() {
        var estudanteId = new EstudanteId(1);
        var data = LocalDate.of(2025, 3, 15);
        when(verificadorVinculo.estaNoVinculo(estudanteId, data)).thenReturn(true);
        when(verificadorCertificado.jaUtilizado(estudanteId, "CERT-001")).thenReturn(false);

        var resultado = servico.submeter(estudanteId, new CategoriaAtividadeId(1), 40, data, "CERT-001", "Curso");

        verify(repositorio).salvar(any());
        assertEquals(StatusAtividade.PENDENTE, resultado.getStatus());
    }

    @Test
    void submeter_semVinculo_deveLancarExcecaoSemSalvar() {
        var estudanteId = new EstudanteId(1);
        var data = LocalDate.of(2025, 3, 15);
        when(verificadorVinculo.estaNoVinculo(estudanteId, data)).thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                servico.submeter(estudanteId, new CategoriaAtividadeId(1), 40, data, "CERT-001", "Curso"));
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void submeter_comCertificadoDuplicado_deveLancarExcecaoSemSalvar() {
        var estudanteId = new EstudanteId(1);
        var data = LocalDate.of(2025, 3, 15);
        when(verificadorVinculo.estaNoVinculo(estudanteId, data)).thenReturn(true);
        when(verificadorCertificado.jaUtilizado(estudanteId, "CERT-001")).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                servico.submeter(estudanteId, new CategoriaAtividadeId(1), 40, data, "CERT-001", "Curso"));
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void deferir_dentroDoLimite_deveSalvar() {
        var atividade = criarAtividadePendente();
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);
        when(verificadorLimite.excedeLimite(any(), any(), anyInt())).thenReturn(false);

        servico.deferir(atividade.getId(), 30);

        verify(repositorio).salvar(atividade);
        assertEquals(StatusAtividade.DEFERIDA, atividade.getStatus());
    }

    @Test
    void deferir_excedendoLimite_deveLancarExcecaoSemSalvar() {
        var atividade = criarAtividadePendente();
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);
        when(verificadorLimite.excedeLimite(any(), any(), anyInt())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> servico.deferir(atividade.getId(), 30));
        verify(repositorio, never()).salvar(any());
    }

    @Test
    void solicitarRevisao_semContabilizacao_deveSalvar() {
        var atividade = criarAtividadeIndeferida();
        when(verificadorContabilizacao.foiContabilizada(atividade.getId())).thenReturn(false);
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);

        servico.solicitarRevisao(atividade.getId(), "Nova documentação");

        verify(repositorio).salvar(atividade);
        assertEquals(StatusAtividade.REVISAO_SOLICITADA, atividade.getStatus());
    }

    @Test
    void solicitarRevisao_comContabilizacao_deveLancarExcecaoSemBuscar() {
        var id = new AtividadeComplementarId(1);
        when(verificadorContabilizacao.foiContabilizada(id)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> servico.solicitarRevisao(id, "justificativa"));
        verify(repositorio, never()).obter(any());
    }

    private AtividadeComplementar criarAtividadePendente() {
        return new AtividadeComplementar(new AtividadeComplementarId(1), new EstudanteId(1),
                new CategoriaAtividadeId(1), "CERT-001", "Curso", 40, LocalDate.of(2025, 3, 15));
    }

    private AtividadeComplementar criarAtividadeIndeferida() {
        var a = criarAtividadePendente();
        a.indeferir("motivo");
        return a;
    }
}
