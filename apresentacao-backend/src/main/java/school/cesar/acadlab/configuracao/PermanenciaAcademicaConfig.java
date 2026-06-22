package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.permanenciaacademica.PermanenciaAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.permanenciaacademica.PermanenciaAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.RegistroNotificacoes;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioServico;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalServico;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoServico;
import school.cesar.acadlab.dominio.permanenciaacademica.NotificacaoInscricaoObservador;

/* F-10: Permanência Acadêmica */
@Configuration
public class PermanenciaAcademicaConfig {

    @Bean
    EditalServico editalServico(EditalRepositorio repositorio, EventoBarramento barramento) {
        return new EditalServico(repositorio, barramento);
    }

    @Bean
    InscricaoServico inscricaoServico(EditalRepositorio editalRepositorio,
                                      InscricaoRepositorio inscricaoRepositorio,
                                      EventoBarramento barramento) {
        return new InscricaoServico(editalRepositorio, inscricaoRepositorio, barramento);
    }

    @Bean
    BeneficioServico beneficioServico(BeneficioRepositorio beneficioRepositorio,
                                      InscricaoRepositorio inscricaoRepositorio,
                                      EditalRepositorio editalRepositorio,
                                      EventoBarramento barramento) {
        return new BeneficioServico(beneficioRepositorio, inscricaoRepositorio, editalRepositorio, barramento);
    }

    @Bean
    PermanenciaAcademicaServicoAplicacao permanenciaAcademicaServicoAplicacao(
            PermanenciaAcademicaRepositorioAplicacao repositorio) {
        return new PermanenciaAcademicaServicoAplicacao(repositorio);
    }

    @Bean
    NotificacaoInscricaoObservador notificacaoInscricaoObservador(
            EventoBarramento barramento, RegistroNotificacoes registroNotificacoes) {
        var observador = new NotificacaoInscricaoObservador(registroNotificacoes);
        barramento.adicionar(observador);
        return observador;
    }
}
