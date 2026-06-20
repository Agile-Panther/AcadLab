package school.cesar.acadlab.apresentacao.ofertaturmas;

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

import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorResumo;
import school.cesar.acadlab.aplicacao.ofertaturmas.ProfessorServicoAplicacao;
import school.cesar.acadlab.dominio.ofertaturmas.professor.Professor;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorId;
import school.cesar.acadlab.dominio.ofertaturmas.professor.ProfessorRepositorio;

@RestController
@RequestMapping("backend/professores")
class ProfessorControlador {
    @Autowired ProfessorRepositorio professorRepositorio;
    @Autowired ProfessorServicoAplicacao professorServico;

    @RequestMapping(method = GET, path = "")
    List<ProfessorResumo> listarAtivos() {
        return professorServico.listarAtivos();
    }

    @RequestMapping(method = GET, path = "/{id}")
    ProfessorResumo buscarPorId(@PathVariable int id) {
        return professorServico.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = POST, path = "")
    ProfessorResumo cadastrar(@RequestBody CadastrarProfessorRequest req) {
        var professorId = professorRepositorio.proximoId();
        professorRepositorio.salvar(new Professor(professorId, req.nome()));
        return professorServico.buscarPorId(professorId.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @RequestMapping(method = PUT, path = "/{id}/inativar")
    void inativar(@PathVariable int id) {
        var professor = buscarDomain(id);
        professor.inativar();
        professorRepositorio.salvar(professor);
    }

    @RequestMapping(method = PUT, path = "/{id}/ativar")
    void ativar(@PathVariable int id) {
        var professor = buscarDomain(id);
        professor.ativar();
        professorRepositorio.salvar(professor);
    }

    private Professor buscarDomain(int id) {
        try {
            return professorRepositorio.obter(new ProfessorId(id));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    record CadastrarProfessorRequest(String nome) {}
}
