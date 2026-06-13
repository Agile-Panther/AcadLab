package school.cesar.acadlab;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarServicoAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaServicoAplicacao;

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

    public static void main(String[] args) {
        run(BackendAplicacao.class, args);
    }
}
