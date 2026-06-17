package school.cesar.acadlab;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarServicoAplicacao;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularServicoAplicacao;
import school.cesar.acadlab.aplicacao.estagios.EstagioRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.EstagioServicoAplicacao;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeServicoAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.CobrancaServicoAplicacao;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaopedagogica.DiarioTurmaServicoAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaServicoAplicacao;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;
import school.cesar.acadlab.dominio.gestaopedagogica.DiarioTurmaServico;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;
import school.cesar.acadlab.dominio.matricula.MatriculaServico;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeAcademicaServico;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaRepositorio;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.secretariavirtual.AnaliseServico;
import school.cesar.acadlab.dominio.secretariavirtual.CalendarioAcademicoPorta;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServico;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServicoProxy;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServicoReal;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaServicoAplicacao;
import school.cesar.acadlab.dominio.ofertaturmas.ConsultaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;

@SpringBootApplication
public class BackendAplicacao {

    @Bean
    MatrizCurricularServicoAplicacao matrizCurricularServicoAplicacao(
            MatrizCurricularRepositorioAplicacao repositorio) {
        return new MatrizCurricularServicoAplicacao(repositorio);
    }

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

    @Bean
    MobilidadeAcademicaServico mobilidadeAcademicaServico(MobilidadeAcademicaRepositorio repositorio) {
        return new MobilidadeAcademicaServico(repositorio);
    }

    @Bean
    MobilidadeAcademicaServicoAplicacao mobilidadeAcademicaServicoAplicacao(
            MobilidadeAcademicaRepositorioAplicacao repositorio) {
        return new MobilidadeAcademicaServicoAplicacao(repositorio);
    }

    @Bean
    SolicitacaoServico solicitacaoServico(SolicitacaoAcademicaRepositorio repositorio,
                                          CalendarioAcademicoPorta calendario) {
        var servicoReal = new SolicitacaoServicoReal(repositorio);
        return new SolicitacaoServicoProxy(servicoReal, repositorio, calendario);
    }

    @Bean
    AnaliseServico analiseServico(SolicitacaoAcademicaRepositorio repositorio) {
        return new AnaliseServico(repositorio);
    }

    @Bean
    SolicitacaoAcademicaServicoAplicacao solicitacaoAcademicaServicoAplicacao(
            SolicitacaoAcademicaRepositorioAplicacao repositorio) {
        return new SolicitacaoAcademicaServicoAplicacao(repositorio);
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
    ProfessorServicoAplicacao professorServicoAplicacao(ProfessorRepositorioAplicacao repositorio) {
        return new ProfessorServicoAplicacao(repositorio);
    }

    @Bean
    EstagioServico estagioServico(OportunidadeRepositorio oportunidadeRepositorio,
                                   EstagioRepositorio estagioRepositorio) {
        return new EstagioServico(oportunidadeRepositorio, estagioRepositorio);
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

    public static void main(String[] args) {
        run(BackendAplicacao.class, args);
    }
}
