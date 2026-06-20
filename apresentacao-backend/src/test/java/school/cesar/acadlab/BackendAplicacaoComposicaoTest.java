package school.cesar.acadlab;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;

import school.cesar.acadlab.configuracao.AtividadesComplementaresConfig;
import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarServico;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorCertificadoDuplicado;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorContabilizacaoIntegralizacao;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorLimiteCategoria;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorVinculoEstudante;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementarRepositorio;
import school.cesar.acadlab.dominio.evento.EventoBarramento;

class BackendAplicacaoComposicaoTest {

    @Test
    void registraServicoDeAtividadesComplementaresComoBean() {
        var metodo = assertDoesNotThrow(() -> AtividadesComplementaresConfig.class.getDeclaredMethod(
                "atividadeComplementarServico",
                AtividadeComplementarRepositorio.class,
                VerificadorVinculoEstudante.class,
                VerificadorCertificadoDuplicado.class,
                VerificadorLimiteCategoria.class,
                VerificadorContabilizacaoIntegralizacao.class,
                EventoBarramento.class));

        assertTrue(metodo.isAnnotationPresent(Bean.class));
        assertTrue(AtividadeComplementarServico.class.isAssignableFrom(metodo.getReturnType()));
    }
}
