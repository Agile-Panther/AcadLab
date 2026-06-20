package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaServicoAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaServicoAplicacao;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;
import school.cesar.acadlab.dominio.gestaofinanceira.CobrancaServico;
import school.cesar.acadlab.dominio.gestaofinanceira.NotificacaoCobrancaObservador;
import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorAutorizacaoDesconto;
import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorMatriculaConfirmada;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.AutorizacaoDescontoPorBolsa;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaRepositorio;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaServico;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.CobrancaRepositorio;

/* F-13: Gestão Financeira */
@Configuration
public class GestaoFinanceiraConfig {

    @Bean
    CobrancaServicoAplicacao cobrancaServicoAplicacao(
            CobrancaRepositorioAplicacao repositorio) {
        return new CobrancaServicoAplicacao(repositorio);
    }

    @Bean
    BolsaServico bolsaServico(BolsaRepositorio repositorio, EventoBarramento barramento) {
        return new BolsaServico(repositorio, barramento);
    }

    @Bean
    BolsaServicoAplicacao bolsaServicoAplicacao(BolsaRepositorioAplicacao repositorio) {
        return new BolsaServicoAplicacao(repositorio);
    }

    @Bean
    VerificadorAutorizacaoDesconto verificadorAutorizacaoDesconto(BolsaRepositorio repositorio) {
        return new AutorizacaoDescontoPorBolsa(repositorio);
    }

    @Bean
    CobrancaServico cobrancaServico(CobrancaRepositorio repositorio,
            VerificadorMatriculaConfirmada verificadorMatricula,
            VerificadorAutorizacaoDesconto verificadorAutorizacao,
            EventoBarramento barramento) {
        return new CobrancaServico(repositorio, verificadorMatricula, verificadorAutorizacao, barramento);
    }

    @Bean
    NotificacaoCobrancaObservador notificacaoCobrancaObservador(
            EventoBarramento barramento, RegistroNotificacoes registroNotificacoes) {
        var observador = new NotificacaoCobrancaObservador(registroNotificacoes);
        barramento.adicionar(observador);
        return observador;
    }
}
