package school.cesar.acadlab.configuracao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaServicoAplicacao;
import school.cesar.acadlab.dominio.ofertaturmas.ConsultaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorServico;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaServico;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

/* F-02/F-03: Oferta de Turmas */
@Configuration
public class OfertaTurmasConfig {

    @Bean
    OfertaTurmaServico ofertaTurmaServico(TurmaRepositorio turmaRepositorio,
                                           SalaRepositorio salaRepositorio,
                                           ProfessorRepositorio professorRepositorio) {
        return new OfertaTurmaServico(turmaRepositorio, salaRepositorio, professorRepositorio);
    }

    @Bean
    ConsultaTurmaServico consultaTurmaServico(TurmaRepositorio turmaRepositorio) {
        return new ConsultaTurmaServico(turmaRepositorio);
    }

    @Bean
    TurmaServicoAplicacao turmaServicoAplicacao(TurmaRepositorioAplicacao repositorio) {
        return new TurmaServicoAplicacao(repositorio);
    }

    @Bean
    SalaServicoAplicacao salaServicoAplicacao(SalaRepositorioAplicacao repositorio) {
        return new SalaServicoAplicacao(repositorio);
    }

    @Bean
    SalaServico salaServico(SalaRepositorio repositorio) {
        return new SalaServico(repositorio);
    }

    @Bean
    ProfessorServicoAplicacao professorServicoAplicacao(ProfessorRepositorioAplicacao repositorio) {
        return new ProfessorServicoAplicacao(repositorio);
    }

    @Bean
    ProfessorServico professorServico(ProfessorRepositorio repositorio) {
        return new ProfessorServico(repositorio);
    }
}
