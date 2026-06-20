package school.cesar.acadlab.apresentacao.ofertaturmas;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
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

import school.cesar.acadlab.aplicacao.ofertaturmas.SalaResumo;
import school.cesar.acadlab.aplicacao.ofertaturmas.SalaServicoAplicacao;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaId;
import school.cesar.acadlab.dominio.ofertaturmas.sala.SalaServico;

@RestController
@RequestMapping("backend/salas")
class SalaControlador {
    @Autowired SalaServico servico;
    @Autowired SalaServicoAplicacao salaServico;

    @RequestMapping(method = GET, path = "")
    List<SalaResumo> listarAtivas() {
        return salaServico.listarAtivas();
    }

    @RequestMapping(method = GET, path = "/{id}")
    SalaResumo buscarPorId(@PathVariable int id) {
        return salaServico.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = POST, path = "")
    SalaResumo cadastrar(@RequestBody CadastrarSalaRequest req) {
        var sala = servico.cadastrar(req.nome(), req.capacidade());
        return salaServico.buscarPorId(sala.getId().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = PUT, path = "/{id}/inativar")
    void inativar(@PathVariable int id) {
        servico.inativar(new SalaId(id));
    }

    @RequestMapping(method = PUT, path = "/{id}/ativar")
    void ativar(@PathVariable int id) {
        servico.ativar(new SalaId(id));
    }

    @RequestMapping(method = PUT, path = "/{id}/capacidade")
    void alterarCapacidade(@PathVariable int id, @RequestBody AlterarCapacidadeRequest req) {
        servico.alterarCapacidade(new SalaId(id), req.capacidade(), req.maiorCapacidadeTurmaVinculada());
    }

    record CadastrarSalaRequest(String nome, int capacidade) {}
    record AlterarCapacidadeRequest(int capacidade, int maiorCapacidadeTurmaVinculada) {}
}
