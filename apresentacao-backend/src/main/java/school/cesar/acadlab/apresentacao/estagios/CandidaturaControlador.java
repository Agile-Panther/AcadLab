package school.cesar.acadlab.apresentacao.estagios;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.estagios.CandidaturaResumo;
import school.cesar.acadlab.aplicacao.estagios.CandidaturaServicoAplicacao;
import school.cesar.acadlab.dominio.estagios.EstagioServico;
import school.cesar.acadlab.dominio.estagios.candidatura.CandidaturaId;

@RestController
@RequestMapping("backend/candidaturas")
class CandidaturaControlador {

    @Autowired
    private EstagioServico servico;

    @Autowired
    private CandidaturaServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET)
    List<CandidaturaResumo> listarTodas() {
        return servicoAplicacao.listarTodas();
    }

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    List<CandidaturaResumo> buscarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarPorEstudante(estudanteId);
    }

    @RequestMapping(method = PUT, path = "{id}/deferir")
    void deferir(@PathVariable int id) {
        servico.deferir(new CandidaturaId(id));
    }

    @RequestMapping(method = PUT, path = "{id}/indeferir")
    void indeferir(@PathVariable int id) {
        servico.indeferir(new CandidaturaId(id));
    }

    @RequestMapping(method = PUT, path = "{id}/cancelar")
    void cancelar(@PathVariable int id) {
        servico.cancelarCandidatura(new CandidaturaId(id));
    }
}
