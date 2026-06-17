package school.cesar.acadlab.apresentacao.secretariavirtual;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaResumo;
import school.cesar.acadlab.aplicacao.secretariavirtual.SolicitacaoAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.secretariavirtual.AnaliseServico;
import school.cesar.acadlab.dominio.secretariavirtual.SolicitacaoServico;
import school.cesar.acadlab.dominio.secretariavirtual.analista.SecretariaId;
import school.cesar.acadlab.dominio.secretariavirtual.documento.Documento;
import school.cesar.acadlab.dominio.secretariavirtual.estudante.EstudanteId;
import school.cesar.acadlab.dominio.secretariavirtual.periodo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.TipoSolicitacao;

@RestController
@RequestMapping("backend/solicitacoes")
class SolicitacaoAcademicaControlador {

    @Autowired
    private SolicitacaoServico solicitacaoServico;

    @Autowired
    private AnaliseServico analiseServico;

    @Autowired
    private SolicitacaoAcademicaServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    List<SolicitacaoAcademicaResumo> buscarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarPorEstudante(estudanteId);
    }

    @RequestMapping(method = GET, path = "{id}")
    Optional<SolicitacaoAcademicaResumo> buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id);
    }

    @RequestMapping(method = GET, path = "pendentes")
    List<SolicitacaoAcademicaResumo> buscarPendentesDeAnalise() {
        return servicoAplicacao.buscarPendentesDeAnalise();
    }

    @RequestMapping(method = GET, path = "todas")
    List<SolicitacaoAcademicaResumo> buscarTodas() {
        return servicoAplicacao.buscarTodas();
    }

    @RequestMapping(method = GET, path = "estatisticas")
    Map<String, Long> obterEstatisticas() {
        var todas = servicoAplicacao.buscarTodas();
        return Map.of(
                "PENDENTE_ANALISE", todas.stream().filter(s -> "PENDENTE_ANALISE".equals(s.status())).count(),
                "EM_ANALISE", todas.stream().filter(s -> "EM_ANALISE".equals(s.status())).count(),
                "DEFERIDA", todas.stream().filter(s -> "DEFERIDA".equals(s.status())).count(),
                "INDEFERIDA", todas.stream().filter(s -> "INDEFERIDA".equals(s.status())).count(),
                "CONCLUIDA", todas.stream().filter(s -> "CONCLUIDA".equals(s.status())).count(),
                "CANCELADA", todas.stream().filter(s -> "CANCELADA".equals(s.status())).count(),
                "PENDENTE_COMPLEMENTACAO", todas.stream().filter(s -> "PENDENTE_COMPLEMENTACAO".equals(s.status())).count()
        );
    }

    @RequestMapping(method = POST)
    int abrirSolicitacao(@RequestBody AbrirSolicitacaoRequest request) {
        var documentos = request.documentos() != null
                ? request.documentos().stream()
                    .map(d -> new Documento(d.tipo(), d.nomeArquivo()))
                    .toList()
                : List.<Documento>of();

        var solicitacao = solicitacaoServico.abrirSolicitacao(
                new EstudanteId(request.estudanteId()),
                new PeriodoLetivoId(request.periodoLetivoId()),
                TipoSolicitacao.valueOf(request.tipo()),
                request.descricao(),
                documentos);

        return solicitacao.getId().getId();
    }

    @RequestMapping(method = PUT, path = "{id}/complementar")
    void complementar(@PathVariable int id, @RequestBody DocumentoRequest request) {
        var documento = new Documento(request.tipo(), request.nomeArquivo());
        solicitacaoServico.complementarSolicitacao(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id),
                documento);
    }

    @RequestMapping(method = PUT, path = "{id}/cancelar")
    void cancelar(@PathVariable int id) {
        solicitacaoServico.cancelarSolicitacao(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id));
    }

    @RequestMapping(method = PUT, path = "{id}/iniciar-analise")
    void iniciarAnalise(@PathVariable int id, @RequestBody AnalistaRequest request) {
        analiseServico.iniciarAnalise(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id),
                new SecretariaId(request.analistaId()));
    }

    @RequestMapping(method = PUT, path = "{id}/deferir")
    void deferir(@PathVariable int id, @RequestBody DeferirRequest request) {
        analiseServico.deferir(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id),
                new SecretariaId(request.analistaId()),
                request.justificativa(),
                request.impactoAcademico());
    }

    @RequestMapping(method = PUT, path = "{id}/indeferir")
    void indeferir(@PathVariable int id, @RequestBody IndeferirRequest request) {
        analiseServico.indeferir(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id),
                new SecretariaId(request.analistaId()),
                request.justificativa());
    }

    @RequestMapping(method = PUT, path = "{id}/solicitar-complementacao")
    void solicitarComplementacao(@PathVariable int id, @RequestBody AnalistaRequest request) {
        analiseServico.solicitarComplementacao(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id),
                new SecretariaId(request.analistaId()));
    }

    @RequestMapping(method = PUT, path = "{id}/concluir")
    void concluir(@PathVariable int id) {
        analiseServico.concluir(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id));
    }

    @RequestMapping(method = PUT, path = "{id}/vincular-e-concluir")
    void vincularAlteracoesEConcluir(@PathVariable int id) {
        analiseServico.vincularAlteracoesEConcluir(
                new school.cesar.acadlab.dominio.secretariavirtual.solicitacaoAcademica.SolicitacaoAcademicaId(id));
    }

    record DocumentoRequest(String tipo, String nomeArquivo) {}
    record AbrirSolicitacaoRequest(int estudanteId, int periodoLetivoId, String tipo,
                                    String descricao, List<DocumentoRequest> documentos) {}
    record AnalistaRequest(int analistaId) {}
    record DeferirRequest(int analistaId, String justificativa, boolean impactoAcademico) {}
    record IndeferirRequest(int analistaId, String justificativa) {}
}
