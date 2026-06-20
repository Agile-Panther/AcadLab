package school.cesar.acadlab.apresentacao.estagios;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeResumo;
import school.cesar.acadlab.aplicacao.estagios.OportunidadeServicoAplicacao;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;
import school.cesar.acadlab.dominio.estagios.oportunidade.SetorEstagiosId;

@RestController
@RequestMapping("backend/oportunidades")
class OportunidadeControlador {

    @Autowired
    private EstagioServico servico;

    @Autowired
    private OportunidadeServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET)
    List<OportunidadeResumo> listarAbertas() {
        return servicoAplicacao.listarAbertas();
    }

    @RequestMapping(method = GET, path = "{id}")
    OportunidadeResumo buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Oportunidade não encontrada: " + id));
    }

    @RequestMapping(method = POST)
    int cadastrar(@RequestBody CadastrarOportunidadeRequest request) {
        return servico.cadastrarOportunidade(
                new EmpresaId(request.empresaId()),
                request.descricao(),
                request.cargaHorariaTotal()).getValor();
    }

    @RequestMapping(method = PUT, path = "{id}/publicar")
    void publicar(@PathVariable int id, @RequestBody PublicarRequest request) {
        servico.publicarOportunidade(
                new OportunidadeId(id),
                new SetorEstagiosId(request.setorId()));
    }

    @RequestMapping(method = DELETE, path = "{id}")
    void excluir(@PathVariable int id) {
        servicoAplicacao.excluir(id);
    }

    @RequestMapping(method = PUT, path = "{id}/candidatura")
    int candidatar(@PathVariable int id, @RequestBody CandidatarRequest request) {
        return servico.registrarCandidatura(
                new OportunidadeId(id),
                new EstudanteId(request.estudanteId()),
                LocalDate.now()).getValor();
    }

    @RequestMapping(method = PUT, path = "{id}/candidaturas/{candidaturaId}/deferir")
    void deferir(@PathVariable int id, @PathVariable int candidaturaId) {
        servico.deferir(new CandidaturaId(candidaturaId));
    }

    @RequestMapping(method = PUT, path = "{id}/candidaturas/{candidaturaId}/indeferir")
    void indeferir(@PathVariable int id, @PathVariable int candidaturaId) {
        servico.indeferir(new CandidaturaId(candidaturaId));
    }

    @RequestMapping(method = PUT, path = "{id}/candidaturas/{candidaturaId}/encaminhar")
    int encaminharEConfirmar(@PathVariable int id, @PathVariable int candidaturaId,
                              @RequestBody EmpresaRequest request) {
        return servico.encaminharEConfirmar(
                new CandidaturaId(candidaturaId),
                new EmpresaId(request.empresaId())).getValor();
    }

    record CadastrarOportunidadeRequest(int empresaId, String descricao, int cargaHorariaTotal) {}
    record PublicarRequest(int setorId) {}
    record CandidatarRequest(int estudanteId) {}
    record EmpresaRequest(int empresaId) {}
}
