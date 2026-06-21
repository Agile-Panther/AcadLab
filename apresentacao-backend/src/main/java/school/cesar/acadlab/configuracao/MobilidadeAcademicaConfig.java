package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeAcademicaServico;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaRepositorio;

/* F-12: Mobilidade Acadêmica */
@Configuration
public class MobilidadeAcademicaConfig {

    @Bean
    MobilidadeAcademicaServico mobilidadeAcademicaServico(MobilidadeAcademicaRepositorio repositorio) {
        return new MobilidadeAcademicaServico(repositorio);
    }

    @Bean
    MobilidadeAcademicaServicoAplicacao mobilidadeAcademicaServicoAplicacao(
            MobilidadeAcademicaRepositorioAplicacao repositorio) {
        return new MobilidadeAcademicaServicoAplicacao(repositorio);
    }
}
