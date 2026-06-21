package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.secretariavirtual.AnaliseServico;
import school.cesar.acadlab.dominio.secretariavirtual.CalendarioAcademicoPorta;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServico;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServicoProxy;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServicoReal;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;

/* F-07: Secretaria Virtual */
@Configuration
public class SecretariaVirtualConfig {

    @Bean
    SolicitacaoServico solicitacaoServico(SolicitacaoAcademicaRepositorio repositorio,
                                          CalendarioAcademicoPorta calendario) {
        var servicoReal = new SolicitacaoServicoReal(repositorio);
        return new SolicitacaoServicoProxy(servicoReal, repositorio, calendario);
    }

    @Bean
    AnaliseServico analiseServico(SolicitacaoAcademicaRepositorio repositorio) {
        return new AnaliseServico(repositorio);
    }

    @Bean
    SolicitacaoAcademicaServicoAplicacao solicitacaoAcademicaServicoAplicacao(
            SolicitacaoAcademicaRepositorioAplicacao repositorio) {
        return new SolicitacaoAcademicaServicoAplicacao(repositorio);
    }
}
