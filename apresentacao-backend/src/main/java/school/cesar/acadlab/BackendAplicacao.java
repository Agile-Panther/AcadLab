package school.cesar.acadlab;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarServicoAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaServicoAplicacao;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaServicoAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaServicoAplicacao;
import school.cesar.acadlab.dominio.gestaopedagogica.DiarioTurmaServico;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;
import school.cesar.acadlab.dominio.matricula.MatriculaServico;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;

@SpringBootApplication
public class BackendAplicacao {

    @Bean
    AtividadeComplementarServicoAplicacao atividadeComplementarServicoAplicacao(
            AtividadeComplementarRepositorioAplicacao repositorio) {
        return new AtividadeComplementarServicoAplicacao(repositorio);
    }

    @Bean
    CobrancaServicoAplicacao cobrancaServicoAplicacao(
            CobrancaRepositorioAplicacao repositorio) {
        return new CobrancaServicoAplicacao(repositorio);
    }

    @Bean
    DiarioTurmaServico diarioTurmaServico(DiarioTurmaRepositorio repositorio) {
        return new DiarioTurmaServico(repositorio);
    }

    @Bean
    DiarioTurmaServicoAplicacao diarioTurmaServicoAplicacao(DiarioTurmaRepositorioAplicacao repositorio) {
        return new DiarioTurmaServicoAplicacao(repositorio);
    }

    @Bean
    MatriculaServico matriculaServico(MatriculaRepositorio repositorio) {
        return new MatriculaServico(repositorio);
    }

    @Bean
    MatriculaServicoAplicacao matriculaServicoAplicacao(MatriculaRepositorioAplicacao repositorio) {
        return new MatriculaServicoAplicacao(repositorio);
    }

    public static void main(String[] args) {
        run(BackendAplicacao.class, args);
    }
}
