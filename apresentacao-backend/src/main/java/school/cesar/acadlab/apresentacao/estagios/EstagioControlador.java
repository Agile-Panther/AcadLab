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
import school.cesar.acadlab.aplicacao.estagios.EstagioResumo;
import school.cesar.acadlab.aplicacao.estagios.EstagioServicoAplicacao;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.estagios.estagio.EstagioId;
import school.cesar.acadlab.dominio.estagios.estagio.StatusRelatorio;
import school.cesar.acadlab.dominio.estagios.oportunidade.CoordenadorId;

@RestController
@RequestMapping("backend/estagios")
class EstagioControlador {

    @Autowired
    private EstagioServico servico;

    @Autowired
    private EstagioServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "{id}")
    EstagioResumo buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Estágio não encontrado: " + id));
    }

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    List<EstagioResumo> buscarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarPorEstudante(estudanteId);
    }

    @RequestMapping(method = POST, path = "{id}/relatorios")
    void submeterRelatorio(@PathVariable int id, @RequestBody SubmeterRelatorioRequest request) {
        servico.submeterRelatorio(new EstagioId(id), request.numero(), request.descricao());
    }

    @RequestMapping(method = PUT, path = "{id}/relatorios/{numero}/avaliar")
    void avaliarRelatorio(@PathVariable int id, @PathVariable int numero,
                          @RequestBody AvaliarRelatorioRequest request) {
        servico.avaliarRelatorio(new EstagioId(id), numero,
                StatusRelatorio.valueOf(request.resultado()));
    }

    @RequestMapping(method = PUT, path = "{id}/encerramento")
    void solicitarEncerramento(@PathVariable int id) {
        servico.solicitarEncerramento(new EstagioId(id));
    }

    @RequestMapping(method = PUT, path = "{id}/encerramento/homologar")
    void homologarEncerramento(@PathVariable int id, @RequestBody HomologarRequest request) {
        servico.homologarEncerramento(new EstagioId(id), new CoordenadorId(request.coordenadorId()));
    }

    record SubmeterRelatorioRequest(int numero, String descricao) {}
    record AvaliarRelatorioRequest(String resultado) {}
    record HomologarRequest(int coordenadorId) {}
}
