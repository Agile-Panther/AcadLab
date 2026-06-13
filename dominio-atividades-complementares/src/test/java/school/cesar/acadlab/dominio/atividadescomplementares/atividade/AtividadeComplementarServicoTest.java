package school.cesar.acadlab.dominio.atividadescomplementares.atividade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.cesar.acadlab.dominio.atividadescomplementares.*;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtividadeComplementarServicoTest {

    private AtividadeComplementarRepositorio repositorio;
    private VerificadorVinculoEstudante verificadorVinculo;
    private VerificadorCertificadoDuplicado verificadorCertificado;
    private VerificadorLimiteCategoria verificadorLimite;
    private VerificadorContabilizacaoIntegralizacao verificadorContabilizacao;
    private EventoBarramento barramento;
    private AtividadeComplementarServico servico;

    @BeforeEach
    void setUp() {
        repositorio = mock(AtividadeComplementarRepositorio.class);
        verificadorVinculo = mock(VerificadorVinculoEstudante.class);
        verificadorCertificado = mock(VerificadorCertificadoDuplicado.class);
        verificadorLimite = mock(VerificadorLimiteCategoria.class);
        verificadorContabilizacao = mock(VerificadorContabilizacaoIntegralizacao.class);
        barramento = mock(EventoBarramento.class);
        servico = new AtividadeComplementarServico(repositorio, verificadorVinculo,
                verificadorCertificado, verificadorLimite, verificadorContabilizacao, barramento);
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
    void deferir_dentroDoLimite_deveSalvarEPublicarEvento() {
        var atividade = criarAtividadePendente();
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);
        when(verificadorLimite.excedeLimite(any(), any(), anyInt())).thenReturn(false);

        servico.deferir(atividade.getId(), 30);

        verify(repositorio).salvar(atividade);
        assertEquals(StatusAtividade.DEFERIDA, atividade.getStatus());
        verify(barramento).postar(any(AtividadeComplementar.DeferidaEvento.class));
    }

    @Test
    void deferir_excedendoLimite_deveLancarExcecaoSemSalvar() {
        var atividade = criarAtividadePendente();
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);
        when(verificadorLimite.excedeLimite(any(), any(), anyInt())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> servico.deferir(atividade.getId(), 30));
        verify(repositorio, never()).salvar(any());
        verify(barramento, never()).postar(any());
    }

    @Test
    void indeferir_devePublicarEvento() {
        var atividade = criarAtividadePendente();
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);

        servico.indeferir(atividade.getId(), "Certificado inválido");

        verify(barramento).postar(any(AtividadeComplementar.IndeferidaEvento.class));
    }

    @Test
    void solicitarRevisao_semContabilizacao_deveSalvarEPublicarEvento() {
        var atividade = criarAtividadeIndeferida();
        when(verificadorContabilizacao.foiContabilizada(atividade.getId())).thenReturn(false);
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);

        servico.solicitarRevisao(atividade.getId(), "Nova documentação");

        verify(repositorio).salvar(atividade);
        assertEquals(StatusAtividade.REVISAO_SOLICITADA, atividade.getStatus());
        verify(barramento).postar(any(AtividadeComplementar.RevisaoSolicitadaEvento.class));
    }

    @Test
    void solicitarRevisao_comContabilizacao_deveLancarExcecaoSemBuscar() {
        var id = new AtividadeComplementarId(1);
        when(verificadorContabilizacao.foiContabilizada(id)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> servico.solicitarRevisao(id, "justificativa"));
        verify(repositorio, never()).obter(any());
        verify(barramento, never()).postar(any());
    }

    @Test
    void cancelar_devePublicarEvento() {
        var atividade = criarAtividadePendente();
        when(repositorio.obter(atividade.getId())).thenReturn(atividade);

        servico.cancelar(atividade.getId());

        verify(barramento).postar(any(AtividadeComplementar.CanceladaEvento.class));
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
