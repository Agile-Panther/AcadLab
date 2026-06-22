package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaServicoAplicacao;
import school.cesar.acadlab.dominio.gestaopedagogica.DiarioTurmaServico;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;

/* F-05: Gestão Pedagógica */
@Configuration
public class GestaoPedagogicaConfig {

    @Bean
    DiarioTurmaServico diarioTurmaServico(DiarioTurmaRepositorio repositorio) {
        return new DiarioTurmaServico(repositorio);
    }

    @Bean
    DiarioTurmaServicoAplicacao diarioTurmaServicoAplicacao(DiarioTurmaRepositorioAplicacao repositorio) {
        return new DiarioTurmaServicoAplicacao(repositorio);
    }
}
