package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoServicoAplicacao;
import school.cesar.acadlab.dominio.historicoacademico.ConsultaHistoricoServico;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoAcademicoServico;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademicoRepositorio;

/* F-06: Histórico Acadêmico */
@Configuration
public class HistoricoAcademicoConfig {

    @Bean
    HistoricoAcademicoServico historicoAcademicoServico(HistoricoAcademicoRepositorio repositorio) {
        return new HistoricoAcademicoServico(repositorio);
    }

    @Bean
    ConsultaHistoricoServico consultaHistoricoServico(HistoricoAcademicoRepositorio repositorio) {
        return new ConsultaHistoricoServico(repositorio);
    }

    @Bean
    HistoricoAcademicoServicoAplicacao historicoAcademicoServicoAplicacao(
            HistoricoAcademicoRepositorioAplicacao repositorio) {
        return new HistoricoAcademicoServicoAplicacao(repositorio);
    }
}
