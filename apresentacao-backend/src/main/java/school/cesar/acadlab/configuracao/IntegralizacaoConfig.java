package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.integralizacao.ColacaoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoServicoAplicacao;
import school.cesar.acadlab.dominio.integralizacao.ColacaoServico;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPendenciasPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPeriodoLetivoPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaRequisitosIntegralizacaoPorta;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoOperacoes;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoServico;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoServicoProxy;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;

/* F-08: Integralização */
@Configuration
public class IntegralizacaoConfig {

    @Bean
    IntegralizacaoServico integralizacaoServico(IntegralizacaoRepositorio repositorio) {
        return new IntegralizacaoServico(repositorio);
    }

    @Bean
    IntegralizacaoOperacoes integralizacaoOperacoes(IntegralizacaoServico servico,
                                                     IntegralizacaoRepositorio repositorio,
                                                     ConsultaPeriodoLetivoPorta periodoLetivoPorta,
                                                     ConsultaPendenciasPorta pendenciasPorta,
                                                     ConsultaRequisitosIntegralizacaoPorta requisitosPorta) {
        return new IntegralizacaoServicoProxy(servico, repositorio,
                periodoLetivoPorta, pendenciasPorta, requisitosPorta);
    }

    @Bean
    ConsultaPeriodoLetivoPorta consultaPeriodoLetivoPorta() {
        return e -> true;
    }

    @Bean
    ConsultaPendenciasPorta consultaPendenciasPorta() {
        return e -> false;
    }

    @Bean
    ConsultaRequisitosIntegralizacaoPorta consultaRequisitosPorta() {
        return (e, m) -> true;
    }

    @Bean
    ColacaoServico colacaoServico(ColacaoRepositorio colacaoRepositorio,
                                   IntegralizacaoRepositorio integralizacaoRepositorio) {
        return new ColacaoServico(colacaoRepositorio, integralizacaoRepositorio);
    }

    @Bean
    IntegralizacaoServicoAplicacao integralizacaoServicoAplicacao(
            IntegralizacaoRepositorioAplicacao integralizacaoRepositorio,
            ColacaoRepositorioAplicacao colacaoRepositorio) {
        return new IntegralizacaoServicoAplicacao(integralizacaoRepositorio, colacaoRepositorio);
    }
}
