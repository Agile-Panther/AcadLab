package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.matricula.MatriculaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaServicoAplicacao;
import school.cesar.acadlab.dominio.matricula.MatriculaServico;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;

/* F-04: Matrícula */
@Configuration
public class MatriculaConfig {

    @Bean
    MatriculaServico matriculaServico(MatriculaRepositorio repositorio) {
        return new MatriculaServico(repositorio);
    }

    @Bean
    MatriculaServicoAplicacao matriculaServicoAplicacao(MatriculaRepositorioAplicacao repositorio) {
        return new MatriculaServicoAplicacao(repositorio);
    }
}
