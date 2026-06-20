package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoServicoAplicacao;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoServico;
import school.cesar.acadlab.dominio.periodoletivo.VerificadorMatriculasPeriodo;
import school.cesar.acadlab.dominio.periodoletivo.VerificadorPendenciasPeriodo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;

/* Período Letivo */
@Configuration
public class PeriodoLetivoConfig {

    @Bean
    VerificadorPendenciasPeriodo verificadorPendenciasPeriodo() {
        return id -> false;
    }

    @Bean
    VerificadorMatriculasPeriodo verificadorMatriculasPeriodo() {
        return id -> false;
    }

    @Bean
    PeriodoLetivoServico periodoLetivoServico(PeriodoLetivoRepositorio repositorio,
                                               VerificadorPendenciasPeriodo verificadorPendencias,
                                               VerificadorMatriculasPeriodo verificadorMatriculas) {
        return new PeriodoLetivoServico(repositorio, verificadorPendencias, verificadorMatriculas);
    }

    @Bean
    PeriodoLetivoServicoAplicacao periodoLetivoServicoAplicacao(PeriodoLetivoRepositorioAplicacao repositorio) {
        return new PeriodoLetivoServicoAplicacao(repositorio);
    }
}
