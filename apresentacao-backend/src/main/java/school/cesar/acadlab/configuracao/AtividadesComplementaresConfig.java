package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarServicoAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.CategoriaHorasRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.CategoriaHorasServicoAplicacao;
import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarServico;
import school.cesar.acadlab.dominio.atividadescomplementares.NotificacaoAtividadeObservador;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorCertificadoDuplicado;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorContabilizacaoIntegralizacao;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorLimiteCategoria;
import school.cesar.acadlab.dominio.atividadescomplementares.VerificadorVinculoEstudante;
import school.cesar.acadlab.dominio.atividadescomplementares.atividade.AtividadeComplementarRepositorio;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

/* F-09: Atividades Complementares */
@Configuration
public class AtividadesComplementaresConfig {

    @Bean
    AtividadeComplementarServicoAplicacao atividadeComplementarServicoAplicacao(
            AtividadeComplementarRepositorioAplicacao repositorio) {
        return new AtividadeComplementarServicoAplicacao(repositorio);
    }

    @Bean
    AtividadeComplementarServico atividadeComplementarServico(
            AtividadeComplementarRepositorio repositorio,
            VerificadorVinculoEstudante verificadorVinculo,
            VerificadorCertificadoDuplicado verificadorCertificado,
            VerificadorLimiteCategoria verificadorLimite,
            VerificadorContabilizacaoIntegralizacao verificadorContabilizacao,
            EventoBarramento barramento) {
        return new AtividadeComplementarServico(
                repositorio,
                verificadorVinculo,
                verificadorCertificado,
                verificadorLimite,
                verificadorContabilizacao,
                barramento);
    }

    @Bean
    CategoriaHorasServicoAplicacao categoriaHorasServicoAplicacao(
            CategoriaHorasRepositorioAplicacao repositorio) {
        return new CategoriaHorasServicoAplicacao(repositorio);
    }

    @Bean
    NotificacaoAtividadeObservador notificacaoAtividadeObservador(
            EventoBarramento barramento, RegistroNotificacoes registroNotificacoes) {
        var observador = new NotificacaoAtividadeObservador(registroNotificacoes);
        barramento.adicionar(observador);
        return observador;
    }
}
