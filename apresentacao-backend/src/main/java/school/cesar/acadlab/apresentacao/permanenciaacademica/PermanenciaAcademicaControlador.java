package school.cesar.acadlab.apresentacao.permanenciaacademica;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import school.cesar.acadlab.aplicacao.permanenciaacademica.BeneficioResumo;
import school.cesar.acadlab.aplicacao.permanenciaacademica.EditalResumo;
import school.cesar.acadlab.aplicacao.permanenciaacademica.InscricaoResumo;
import school.cesar.acadlab.aplicacao.permanenciaacademica.PermanenciaAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.permanenciaacademica.AssistenciaEstudantilId;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioId;
import school.cesar.acadlab.dominio.permanenciaacademica.BeneficioServico;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalId;
import school.cesar.acadlab.dominio.permanenciaacademica.EditalServico;
import school.cesar.acadlab.dominio.permanenciaacademica.EstudantePermanenciaId;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoId;
import school.cesar.acadlab.dominio.permanenciaacademica.InscricaoServico;

@RestController
@RequestMapping("backend/permanencia")
class PermanenciaAcademicaControlador {

    @Autowired private EditalServico editalServico;
    @Autowired private InscricaoServico inscricaoServico;
    @Autowired private BeneficioServico beneficioServico;
    @Autowired private PermanenciaAcademicaServicoAplicacao servicoAplicacao;

    /* ===== Editais ===== */

    @RequestMapping(method = GET, path = "editais")
    List<EditalResumo> buscarEditais(@RequestParam(required = false) String programa) {
        if (programa == null || programa.isBlank()) {
            return servicoAplicacao.buscarTodosEditais();
        }
        return servicoAplicacao.buscarEditaisPorPrograma(programa);
    }

    @RequestMapping(method = GET, path = "editais/{id}")
    Optional<EditalResumo> buscarEditalPorId(@PathVariable int id) {
        return servicoAplicacao.buscarEditalPorId(id);
    }

    @RequestMapping(method = POST, path = "editais")
    int criarEdital(@RequestBody CriarEditalRequest request) {
        return editalServico.criar(
                request.programa(), request.vagas(),
                request.prazoInscricaoInicio(), request.prazoInscricaoFim(),
                request.prazoRecursoInicio(), request.prazoRecursoFim(),
                request.prazoRenovacao()).getValor();
    }

    @RequestMapping(method = PUT, path = "editais/{id}/resultado")
    void publicarResultado(@PathVariable int id, @RequestBody PublicarResultadoRequest request) {
        editalServico.publicarResultado(new EditalId(id), request.hoje());
    }

    @RequestMapping(method = PUT, path = "editais/{id}/encerrar")
    void encerrarEdital(@PathVariable int id) {
        editalServico.encerrar(new EditalId(id));
    }

    /* ===== Inscrições ===== */

    @RequestMapping(method = GET, path = "inscricoes")
    List<InscricaoResumo> buscarTodasInscricoes() {
        return servicoAplicacao.buscarTodasInscricoes();
    }

    @RequestMapping(method = GET, path = "editais/{editalId}/inscricoes")
    List<InscricaoResumo> buscarInscricoesPorEdital(@PathVariable int editalId) {
        return servicoAplicacao.buscarInscricoesPorEdital(editalId);
    }

    @RequestMapping(method = GET, path = "estudantes/{estudanteId}/inscricoes")
    List<InscricaoResumo> buscarInscricoesPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarInscricoesPorEstudante(estudanteId);
    }

    @RequestMapping(method = POST, path = "editais/{editalId}/inscricoes")
    int inscrever(@PathVariable int editalId, @RequestBody InscreverRequest request) {
        return inscricaoServico.inscrever(
                new EstudantePermanenciaId(request.estudanteId()),
                new EditalId(editalId),
                request.hoje(),
                request.atendeElegibilidade()).getValor();
    }

    @RequestMapping(method = PUT, path = "inscricoes/{id}/deferir")
    void deferir(@PathVariable int id, @RequestBody DeferirRequest request) {
        inscricaoServico.deferir(new InscricaoId(id),
                new AssistenciaEstudantilId(request.assistenciaId()),
                request.pontuacao());
    }

    @RequestMapping(method = PUT, path = "inscricoes/{id}/indeferir")
    void indeferir(@PathVariable int id, @RequestBody IndeferirRequest request) {
        inscricaoServico.indeferir(new InscricaoId(id),
                new AssistenciaEstudantilId(request.assistenciaId()));
    }

    @RequestMapping(method = POST, path = "inscricoes/{id}/recurso")
    void interporRecurso(@PathVariable int id, @RequestBody RecursoRequest request) {
        inscricaoServico.interporRecurso(new InscricaoId(id),
                new EditalId(request.editalId()), request.hoje());
    }

    /* ===== Benefícios ===== */

    @RequestMapping(method = GET, path = "estudantes/{estudanteId}/beneficios")
    List<BeneficioResumo> buscarBeneficiosPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarBeneficiosPorEstudante(estudanteId);
    }

    @RequestMapping(method = GET, path = "beneficios/{id}")
    Optional<BeneficioResumo> buscarBeneficioPorId(@PathVariable int id) {
        return servicoAplicacao.buscarBeneficioPorId(id);
    }

    @RequestMapping(method = PUT, path = "beneficios/{id}/suspender")
    void suspenderBeneficio(@PathVariable int id) {
        beneficioServico.suspender(new BeneficioId(id));
    }

    @RequestMapping(method = PUT, path = "beneficios/{id}/cancelar")
    void cancelarBeneficio(@PathVariable int id) {
        beneficioServico.cancelar(new BeneficioId(id));
    }

    @RequestMapping(method = POST, path = "beneficios/{id}/renovacao")
    void solicitarRenovacao(@PathVariable int id, @RequestBody RenovacaoRequest request) {
        beneficioServico.solicitarRenovacao(new BeneficioId(id), request.hoje());
    }

    /* ===== Records ===== */

    record CriarEditalRequest(String programa, int vagas,
                               LocalDate prazoInscricaoInicio, LocalDate prazoInscricaoFim,
                               LocalDate prazoRecursoInicio, LocalDate prazoRecursoFim,
                               LocalDate prazoRenovacao) {}

    record PublicarResultadoRequest(LocalDate hoje) {}

    record InscreverRequest(int estudanteId, LocalDate hoje, boolean atendeElegibilidade) {}

    record DeferirRequest(int assistenciaId, int pontuacao) {}

    record IndeferirRequest(int assistenciaId) {}

    record RecursoRequest(int editalId, LocalDate hoje) {}

    record RenovacaoRequest(LocalDate hoje) {}
}
