package school.cesar.acadlab.apresentacao.mobilidadeacademica;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaResumo;
import school.cesar.acadlab.aplicacao.mobilidadeacademica.MobilidadeAcademicaServicoAplicacao;
import school.cesar.acadlab.dominio.mobilidadeacademica.MobilidadeAcademicaServico;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.CoordenadorId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.DisciplinaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.EstudanteId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.MobilidadeAcademicaId;
import school.cesar.acadlab.dominio.mobilidadeacademica.mobilidade.SecretariaId;

@RestController
@RequestMapping("backend/mobilidades")
class MobilidadeAcademicaControlador {

    @Autowired
    private MobilidadeAcademicaServico servico;

    @Autowired
    private MobilidadeAcademicaServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    List<MobilidadeAcademicaResumo> buscarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarPorEstudante(estudanteId);
    }

    @RequestMapping(method = GET, path = "{id}")
    Optional<MobilidadeAcademicaResumo> buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id);
    }

    @RequestMapping(method = POST)
    int solicitar(@RequestBody SolicitarRequest request) {
        return servico.solicitar(
                new EstudanteId(request.estudanteId()),
                request.instituicaoDestino()).getId();
    }

    @RequestMapping(method = PUT, path = "{id}/autorizar")
    void autorizar(@PathVariable int id, @RequestBody AutorizarRequest request) {
        servico.autorizar(
                new MobilidadeAcademicaId(id),
                new CoordenadorId(request.coordenadorId()));
    }

    @RequestMapping(method = PUT, path = "{id}/iniciar")
    void iniciarPeriodoExterno(@PathVariable int id, @RequestBody IniciarRequest request) {
        servico.iniciarPeriodoExterno(
                new MobilidadeAcademicaId(id),
                request.dataInicio());
    }

    @RequestMapping(method = POST, path = "{id}/plano")
    void adicionarItemPlano(@PathVariable int id, @RequestBody AdicionarItemPlanoRequest request) {
        servico.adicionarItemPlano(
                new MobilidadeAcademicaId(id),
                new DisciplinaId(request.disciplinaExternaId()),
                new DisciplinaId(request.disciplinaEquivalenteId()),
                request.cargaHorariaExterna(),
                request.cargaHorariaEquivalente());
    }

    @RequestMapping(method = PUT, path = "{id}/plano/{disciplinaExternaId}/comprovante")
    void anexarComprovante(@PathVariable int id, @PathVariable int disciplinaExternaId) {
        servico.anexarComprovante(
                new MobilidadeAcademicaId(id),
                new DisciplinaId(disciplinaExternaId));
    }

    @RequestMapping(method = PUT, path = "{id}/plano/{disciplinaExternaId}/resultado")
    void registrarResultado(@PathVariable int id, @PathVariable int disciplinaExternaId,
                            @RequestBody RegistrarResultadoRequest request) {
        servico.registrarResultado(
                new MobilidadeAcademicaId(id),
                new DisciplinaId(disciplinaExternaId),
                new SecretariaId(request.secretariaId()));
    }

    @RequestMapping(method = POST, path = "{id}/cancelamento")
    void solicitarCancelamento(@PathVariable int id, @RequestBody CancelamentoRequest request) {
        servico.solicitarCancelamento(
                new MobilidadeAcademicaId(id),
                request.justificativa(),
                request.hoje());
    }

    @RequestMapping(method = PUT, path = "{id}/cancelamento/confirmar")
    void confirmarCancelamento(@PathVariable int id, @RequestBody AutorizarRequest request) {
        servico.confirmarCancelamento(
                new MobilidadeAcademicaId(id),
                new CoordenadorId(request.coordenadorId()));
    }

    record SolicitarRequest(int estudanteId, String instituicaoDestino) {}
    record AutorizarRequest(int coordenadorId) {}
    record IniciarRequest(LocalDate dataInicio) {}
    record AdicionarItemPlanoRequest(int disciplinaExternaId, int disciplinaEquivalenteId,
                                     int cargaHorariaExterna, int cargaHorariaEquivalente) {}
    record RegistrarResultadoRequest(int secretariaId) {}
    record CancelamentoRequest(String justificativa, LocalDate hoje) {}
}
