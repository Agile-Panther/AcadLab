package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.ApoioPsicopedagogicoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.ApoioPsicopedagogicoServicoAplicacao;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.AgendamentoServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.AtendimentoServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.NotificacaoCasoObservador;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.TriagemServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioRepositorio;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;

/* F-11: Apoio Psicopedagógico */
@Configuration
public class ApoioPsicopedagogicoConfig {

    @Bean
    ApoioServico apoioServico(SolicitacaoApoioRepositorio solicitacaoRepositorio,
                               CasoRepositorio casoRepositorio,
                               EventoBarramento barramento) {
        return new ApoioServico(solicitacaoRepositorio, casoRepositorio, barramento);
    }

    @Bean
    TriagemServico triagemServico(CasoRepositorio casoRepositorio, EventoBarramento barramento) {
        return new TriagemServico(casoRepositorio, barramento);
    }

    @Bean
    AtendimentoServico atendimentoServico(CasoRepositorio casoRepositorio, EventoBarramento barramento) {
        return new AtendimentoServico(casoRepositorio, barramento);
    }

    @Bean
    AgendamentoServico agendamentoServico(CasoRepositorio casoRepositorio, EventoBarramento barramento) {
        return new AgendamentoServico(casoRepositorio, barramento);
    }

    @Bean
    ApoioPsicopedagogicoServicoAplicacao apoioPsicopedagogicoServicoAplicacao(
            ApoioPsicopedagogicoRepositorioAplicacao repositorio) {
        return new ApoioPsicopedagogicoServicoAplicacao(repositorio);
    }

    @Bean
    NotificacaoCasoObservador notificacaoCasoObservador(
            EventoBarramento barramento, RegistroNotificacoes registroNotificacoes) {
        var observador = new NotificacaoCasoObservador(registroNotificacoes);
        barramento.adicionar(observador);
        return observador;
    }
}
