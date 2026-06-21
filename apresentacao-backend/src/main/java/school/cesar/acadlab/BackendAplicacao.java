package school.cesar.acadlab;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de inicialização do backend.
 *
 * O wiring de beans vive em uma {@code @Configuration} por bounded context no
 * pacote {@code school.cesar.acadlab.configuracao} (CurriculoConfig, OfertaTurmasConfig,
 * MatriculaConfig, …), descobertas automaticamente pelo component scan.
 */
@SpringBootApplication
public class BackendAplicacao {

    public static void main(String[] args) {
        run(BackendAplicacao.class, args);
    }
}
