package school.cesar.acadlab.apresentacao.curriculo;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularDetalhe;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularResumo;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularServicoAplicacao;
import school.cesar.acadlab.dominio.curriculo.CursoId;
import school.cesar.acadlab.dominio.curriculo.DisciplinaId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularRepositorio;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularServico;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;

@RestController
@RequestMapping("backend/curriculo")
class MatrizCurricularControlador {

    @Autowired
    private MatrizCurricularServico servico;

    @Autowired
    private MatrizCurricularServicoAplicacao servicoAplicacao;

    @Autowired
    private MatrizCurricularRepositorio repositorio;

    @RequestMapping(method = GET, path = "curso/{cursoId}")
    List<MatrizCurricularResumo> buscarPorCurso(@PathVariable int cursoId) {
        return servicoAplicacao.buscarPorCurso(cursoId);
    }

    @RequestMapping(method = GET, path = "{id}")
    Optional<MatrizCurricularResumo> buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id);
    }

    @RequestMapping(method = GET, path = "{id}/detalhe")
    Optional<MatrizCurricularDetalhe> buscarDetalhePorId(@PathVariable int id) {
        return servicoAplicacao.buscarDetalhePorId(id);
    }

    @RequestMapping(method = POST)
    int criar(@RequestBody CriarMatrizRequest request) {
        return servico.criar(
                new CursoId(request.cursoId()),
                request.nome(),
                request.cargaHorariaMinima(),
                request.creditosExigidos(),
                request.maximoTrancamentos()).getId().getValor();
    }

    @RequestMapping(method = POST, path = "{id}/disciplinas")
    void adicionarDisciplina(@PathVariable int id, @RequestBody AdicionarDisciplinaRequest request) {
        servico.adicionarDisciplina(
                new MatrizCurricularId(id),
                new DisciplinaId(request.disciplinaId()),
                TipoDisciplina.valueOf(request.tipo()),
                request.cargaHoraria(),
                request.creditos());
    }

    @RequestMapping(method = PUT, path = "{id}/disciplinas/{disciplinaId}")
    void editarDisciplina(@PathVariable int id, @PathVariable int disciplinaId,
                          @RequestBody EditarDisciplinaRequest request) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Matriz não encontrada"));
        matriz.editarDisciplina(
                new DisciplinaId(disciplinaId),
                TipoDisciplina.valueOf(request.tipo()),
                request.cargaHoraria(),
                request.creditos());
        repositorio.salvar(matriz);
    }

    @RequestMapping(method = DELETE, path = "{id}/disciplinas/{disciplinaId}")
    void removerDisciplina(@PathVariable int id, @PathVariable int disciplinaId) {
        servico.removerDisciplina(new MatrizCurricularId(id), new DisciplinaId(disciplinaId));
    }

    @RequestMapping(method = PUT, path = "{id}/prerequisitos")
    void adicionarPreRequisito(@PathVariable int id, @RequestBody DependenciaRequest request) {
        servico.adicionarPreRequisito(
                new MatrizCurricularId(id),
                new DisciplinaId(request.disciplinaId()),
                new DisciplinaId(request.dependenciaId()));
    }

    @RequestMapping(method = PUT, path = "{id}/correquisitos")
    void adicionarCorrequisito(@PathVariable int id, @RequestBody DependenciaRequest request) {
        servico.adicionarCorrequisito(
                new MatrizCurricularId(id),
                new DisciplinaId(request.disciplinaId()),
                new DisciplinaId(request.dependenciaId()));
    }

    @RequestMapping(method = PUT, path = "{id}/ativar")
    void ativar(@PathVariable int id) {
        servico.ativar(new MatrizCurricularId(id));
    }

    @RequestMapping(method = PUT, path = "{id}/desativar")
    void desativar(@PathVariable int id) {
        servico.desativar(new MatrizCurricularId(id));
    }

    record CriarMatrizRequest(int cursoId, String nome, int cargaHorariaMinima,
                              int creditosExigidos, int maximoTrancamentos) {}
    record AdicionarDisciplinaRequest(int disciplinaId, String tipo, int cargaHoraria, int creditos) {}
    record EditarDisciplinaRequest(String tipo, int cargaHoraria, int creditos) {}
    record DependenciaRequest(int disciplinaId, int dependenciaId) {}
}
