package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.estagios.EstagioRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.EstagioServicoAplicacao;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeServicoAplicacao;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaRepositorio;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.VerificadorElegibilidade;

/* F-14: Estágios */
@Configuration
public class EstagiosConfig {

    @Bean
    VerificadorElegibilidade verificadorElegibilidade() {
        return (estudanteId, criterio) -> true;
    }

    @Bean
    EstagioServico estagioServico(OportunidadeRepositorio oportunidadeRepositorio,
                                   CandidaturaRepositorio candidaturaRepositorio,
                                   EstagioRepositorio estagioRepositorio,
                                   VerificadorElegibilidade verificadorElegibilidade) {
        return new EstagioServico(oportunidadeRepositorio, candidaturaRepositorio,
                estagioRepositorio, verificadorElegibilidade);
    }

    @Bean
    OportunidadeServicoAplicacao oportunidadeServicoAplicacao(
            OportunidadeRepositorioAplicacao repositorio) {
        return new OportunidadeServicoAplicacao(repositorio);
    }

    @Bean
    EstagioServicoAplicacao estagioServicoAplicacao(EstagioRepositorioAplicacao repositorio) {
        return new EstagioServicoAplicacao(repositorio);
    }
}
