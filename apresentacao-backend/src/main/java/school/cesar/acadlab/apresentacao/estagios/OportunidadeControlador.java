package school.cesar.acadlab.apresentacao.estagios;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
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
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EmpresaId;
import school.cesar.acadlab.dominio.estagios.oportunidade.EstudanteId;
import school.cesar.acadlab.dominio.estagios.oportunidade.OportunidadeId;

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

    @RequestMapping(method = PUT, path = "{id}/candidatura")
    void candidatar(@PathVariable int id, @RequestBody CandidatarRequest request) {
        servico.candidatar(new OportunidadeId(id), new EstudanteId(request.estudanteId()));
    }

    @RequestMapping(method = PUT, path = "{id}/encaminhar")
    void encaminhar(@PathVariable int id, @RequestBody EncaminharRequest request) {
        servico.encaminhar(new OportunidadeId(id), new CoordenadorId(request.coordenadorId()));
    }

    @RequestMapping(method = PUT, path = "{id}/confirmar")
    int confirmar(@PathVariable int id, @RequestBody EmpresaRequest request) {
        return servico.confirmar(new OportunidadeId(id), new EmpresaId(request.empresaId())).getValor();
    }

    @RequestMapping(method = PUT, path = "{id}/recusar")
    void recusar(@PathVariable int id, @RequestBody EmpresaRequest request) {
        servico.recusar(new OportunidadeId(id), new EmpresaId(request.empresaId()));
    }

    record CadastrarOportunidadeRequest(int empresaId, String descricao, int cargaHorariaTotal) {}
    record CandidatarRequest(int estudanteId) {}
    record EncaminharRequest(int coordenadorId) {}
    record EmpresaRequest(int empresaId) {}
}
