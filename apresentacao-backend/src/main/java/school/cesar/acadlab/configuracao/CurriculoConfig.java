package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularServicoAplicacao;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularRepositorio;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularServico;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaMatrizAtivaPorta;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaTurmasPorta;

/* F-01: Currículo */
@Configuration
public class CurriculoConfig {

    @Bean
    MatrizCurricularServico matrizCurricularServico(
            MatrizCurricularRepositorio repositorio,
            ConsultaMatrizAtivaPorta consultaMatrizAtiva,
            ConsultaTurmasPorta consultaTurmas) {
        return new MatrizCurricularServico(repositorio, consultaMatrizAtiva, consultaTurmas);
    }

    @Bean
    MatrizCurricularServicoAplicacao matrizCurricularServicoAplicacao(
            MatrizCurricularRepositorioAplicacao repositorio) {
        return new MatrizCurricularServicoAplicacao(repositorio);
    }
}
