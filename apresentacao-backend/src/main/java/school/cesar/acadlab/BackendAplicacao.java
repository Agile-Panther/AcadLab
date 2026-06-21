package school.cesar.acadlab;

import static org.springframework.boot.SpringApplication.run;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.ApoioPsicopedagogicoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.historicoacademico.HistoricoAcademicoServicoAplicacao;
import school.cesar.acadlab.dominio.historicoacademico.ConsultaHistoricoServico;
import school.cesar.acadlab.dominio.historicoacademico.HistoricoAcademicoServico;
import school.cesar.acadlab.dominio.historicoacademico.historico.HistoricoAcademicoRepositorio;
import school.cesar.acadlab.dominio.historicoacademico.historico.ConsultaPeriodoEncerradoPorta;
import school.cesar.acadlab.aplicacao.apoiopsicopedagogico.ApoioPsicopedagogicoServicoAplicacao;
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
import school.cesar.acadlab.aplicacao.integralizacao.ColacaoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoServicoAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.matricula.MatriculaServicoAplicacao;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaServicoAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.ofertaturmas.TurmaServicoAplicacao;
import school.cesar.acadlab.aplicacao.permanenciaacademica.PermanenciaAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.permanenciaacademica.PermanenciaAcademicaServicoAplicacao;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaServicoAplicacao;
import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoServicoAplicacao;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.ApoioServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.AtendimentoServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.TriagemServico;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.caso.CasoRepositorio;
import school.cesar.acadlab.dominio.apoiopsicopedagogico.solicitacao.SolicitacaoApoioRepositorio;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoServico;
import school.cesar.acadlab.dominio.periodoletivo.VerificadorMatriculasPeriodo;
import school.cesar.acadlab.dominio.periodoletivo.VerificadorPendenciasPeriodo;
import school.cesar.acadlab.dominio.periodoletivo.periodo.PeriodoLetivoRepositorio;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaRepositorio;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeRepositorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.VerificadorElegibilidade;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.gestaopedagogica.DiarioTurmaServico;
import school.cesar.acadlab.dominio.gestaopedagogica.diario.DiarioTurmaRepositorio;
import school.cesar.acadlab.dominio.integralizacao.ColacaoServico;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPendenciasPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaPeriodoLetivoPorta;
import school.cesar.acadlab.dominio.integralizacao.ConsultaRequisitosIntegralizacaoPorta;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoOperacoes;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoServico;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoServicoProxy;
import school.cesar.acadlab.dominio.integralizacao.colacao.ColacaoRepositorio;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoRepositorio;
import school.cesar.acadlab.dominio.matricula.MatriculaServico;
import school.cesar.acadlab.dominio.matricula.matricula.MatriculaRepositorio;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeAcademicaServico;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.ConsultaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.OfertaTurmaServico;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaRepositorio;
import school.cesar.acadlab.dominio.ofertaturmas.turma.TurmaRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioServico;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalServico;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoRepositorio;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoServico;
import school.cesar.acadlab.dominio.secretariavirtual.AnaliseServico;
import school.cesar.acadlab.dominio.secretariavirtual.CalendarioAcademicoPorta;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServico;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServicoProxy;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServicoReal;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaRepositorio;

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
    }

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
    ProfessorServicoAplicacao professorServicoAplicacao(ProfessorRepositorioAplicacao repositorio) {
        return new ProfessorServicoAplicacao(repositorio);
    }

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

    @Bean
    IntegralizacaoServico integralizacaoServico(IntegralizacaoRepositorio repositorio) {
        return new IntegralizacaoServico(repositorio);
    }

    @Bean
    IntegralizacaoOperacoes integralizacaoOperacoes(IntegralizacaoServico servico,
                                                     IntegralizacaoRepositorio repositorio,
                                                     ConsultaPeriodoLetivoPorta periodoLetivoPorta,
                                                     ConsultaPendenciasPorta pendenciasPorta,
                                                     ConsultaRequisitosIntegralizacaoPorta requisitosPorta) {
        return new IntegralizacaoServicoProxy(servico, repositorio,
                periodoLetivoPorta, pendenciasPorta, requisitosPorta);
    }

    @Bean
    ConsultaPeriodoLetivoPorta consultaPeriodoLetivoPorta() {
        return e -> true;
    }

    @Bean
    ConsultaPendenciasPorta consultaPendenciasPorta() {
        return e -> false;
    }

    @Bean
    ConsultaRequisitosIntegralizacaoPorta consultaRequisitosPorta() {
        return (e, m) -> true;
    }

    @Bean
    ColacaoServico colacaoServico(ColacaoRepositorio colacaoRepositorio,
                                   IntegralizacaoRepositorio integralizacaoRepositorio) {
        return new ColacaoServico(colacaoRepositorio, integralizacaoRepositorio);
    }

    @Bean
    IntegralizacaoServicoAplicacao integralizacaoServicoAplicacao(
            IntegralizacaoRepositorioAplicacao integralizacaoRepositorio,
            ColacaoRepositorioAplicacao colacaoRepositorio) {
        return new IntegralizacaoServicoAplicacao(integralizacaoRepositorio, colacaoRepositorio);
    }

    /* ===== F-10: Permanência Acadêmica ===== */

    @Bean
    EditalServico editalServico(EditalRepositorio repositorio, EventoBarramento barramento) {
        return new EditalServico(repositorio, barramento);
    }

    @Bean
    InscricaoServico inscricaoServico(EditalRepositorio editalRepositorio,
                                      InscricaoRepositorio inscricaoRepositorio,
                                      EventoBarramento barramento) {
        return new InscricaoServico(editalRepositorio, inscricaoRepositorio, barramento);
    }

    @Bean
    BeneficioServico beneficioServico(BeneficioRepositorio beneficioRepositorio,
                                      InscricaoRepositorio inscricaoRepositorio,
                                      EditalRepositorio editalRepositorio,
                                      EventoBarramento barramento) {
        return new BeneficioServico(beneficioRepositorio, inscricaoRepositorio, editalRepositorio, barramento);
    }

    @Bean
    PermanenciaAcademicaServicoAplicacao permanenciaAcademicaServicoAplicacao(
            PermanenciaAcademicaRepositorioAplicacao repositorio) {
        return new PermanenciaAcademicaServicoAplicacao(repositorio);
    }

    /* ===== F-11: Apoio Psicopedagógico ===== */

    @Bean
    ApoioServico apoioServico(SolicitacaoApoioRepositorio solicitacaoRepositorio,
                               CasoRepositorio casoRepositorio,
                               EventoBarramento barramento) {
        return new ApoioServico(solicitacaoRepositorio, casoRepositorio, barramento);
    }

    @Bean
    TriagemServico triagemServico(CasoRepositorio casoRepositorio, EventoBarramento barramento) {
        return new TriagemServico(casoRepositorio, barramento);
    }

    @Bean
    AtendimentoServico atendimentoServico(CasoRepositorio casoRepositorio, EventoBarramento barramento) {
        return new AtendimentoServico(casoRepositorio, barramento);
    }

    @Bean
    ApoioPsicopedagogicoServicoAplicacao apoioPsicopedagogicoServicoAplicacao(
            ApoioPsicopedagogicoRepositorioAplicacao repositorio) {
        return new ApoioPsicopedagogicoServicoAplicacao(repositorio);
    }

    /* ===== Histórico Acadêmico ===== */

    @Bean
    HistoricoAcademicoServico historicoAcademicoServico(HistoricoAcademicoRepositorio repositorio) {
        return new HistoricoAcademicoServico(repositorio);
    }

    @Bean
    ConsultaHistoricoServico consultaHistoricoServico(HistoricoAcademicoRepositorio repositorio,
            ConsultaPeriodoEncerradoPorta consultaPeriodoEncerrado) {
        return new ConsultaHistoricoServico(repositorio, consultaPeriodoEncerrado);
    }

    @Bean
    HistoricoAcademicoServicoAplicacao historicoAcademicoServicoAplicacao(
            HistoricoAcademicoRepositorioAplicacao repositorio) {
        return new HistoricoAcademicoServicoAplicacao(repositorio);
    }

    /* ===== Período Letivo ===== */

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

    public static void main(String[] args) {
        run(BackendAplicacao.class, args);
    }
}
