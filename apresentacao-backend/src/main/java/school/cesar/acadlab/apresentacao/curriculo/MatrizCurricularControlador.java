package school.cesar.acadlab.apresentacao.curriculo;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularResumo;
import school.cesar.acadlab.aplicacao.curriculo.MatrizCurricularServicoAplicacao;
import school.cesar.acadlab.dominio.curriculo.CursoId;
import school.cesar.acadlab.dominio.curriculo.DisciplinaId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularId;
import school.cesar.acadlab.dominio.curriculo.MatrizCurricularRepositorio;
import school.cesar.acadlab.dominio.curriculo.TipoDisciplina;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaMatrizAtivaPorta;
import school.cesar.acadlab.dominio.curriculo.porta.ConsultaTurmasPorta;

@RestController
@RequestMapping("backend/curriculo")
class MatrizCurricularControlador {

    @Autowired
    private MatrizCurricularRepositorio repositorio;

    @Autowired
    private ConsultaMatrizAtivaPorta consultaMatrizAtiva;

    @Autowired
    private ConsultaTurmasPorta consultaTurmas;

    @Autowired
    private MatrizCurricularServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "curso/{cursoId}")
    List<MatrizCurricularResumo> buscarPorCurso(@PathVariable int cursoId) {
        return servicoAplicacao.buscarPorCurso(cursoId);
    }

    @RequestMapping(method = GET, path = "{id}")
    Optional<MatrizCurricularResumo> buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarPorId(id);
    }

    @RequestMapping(method = POST)
    int criar(@RequestBody CriarMatrizRequest request) {
        MatrizCurricularId novoId = repositorio.proximaMatrizId();
        MatrizCurricular matriz = new MatrizCurricular(
                novoId,
                new CursoId(request.cursoId()),
                request.nome(),
                request.cargaHorariaMinima(),
                request.creditosExigidos(),
                request.maximoTrancamentos());
        repositorio.salvar(matriz);
        return novoId.getValor();
    }

    @RequestMapping(method = POST, path = "{id}/disciplinas")
    void adicionarDisciplina(@PathVariable int id, @RequestBody AdicionarDisciplinaRequest request) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada"));
        matriz.adicionarDisciplina(
                new DisciplinaId(request.disciplinaId()),
                TipoDisciplina.valueOf(request.tipo()),
                request.cargaHoraria(),
                request.creditos());
        repositorio.salvar(matriz);
    }

    @RequestMapping(method = DELETE, path = "{id}/disciplinas/{disciplinaId}")
    void removerDisciplina(@PathVariable int id, @PathVariable int disciplinaId) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada"));
        matriz.removerDisciplina(new DisciplinaId(disciplinaId), consultaTurmas);
        repositorio.salvar(matriz);
    }

    @RequestMapping(method = PUT, path = "{id}/prerequisitos")
    void adicionarPreRequisito(@PathVariable int id, @RequestBody DependenciaRequest request) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada"));
        matriz.adicionarPreRequisito(
                new DisciplinaId(request.disciplinaId()),
                new DisciplinaId(request.dependenciaId()));
        repositorio.salvar(matriz);
    }

    @RequestMapping(method = PUT, path = "{id}/correquisitos")
    void adicionarCorrequisito(@PathVariable int id, @RequestBody DependenciaRequest request) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada"));
        matriz.adicionarCorrequisito(
                new DisciplinaId(request.disciplinaId()),
                new DisciplinaId(request.dependenciaId()));
        repositorio.salvar(matriz);
    }

    @RequestMapping(method = PUT, path = "{id}/ativar")
    void ativar(@PathVariable int id) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada"));
        matriz.ativar(consultaMatrizAtiva);
        repositorio.salvar(matriz);
    }

    @RequestMapping(method = PUT, path = "{id}/desativar")
    void desativar(@PathVariable int id) {
        MatrizCurricular matriz = repositorio.buscarPorId(new MatrizCurricularId(id))
                .orElseThrow(() -> new IllegalArgumentException("Matriz não encontrada"));
        matriz.desativar();
        repositorio.salvar(matriz);
    }

    record CriarMatrizRequest(int cursoId, String nome, int cargaHorariaMinima,
                              int creditosExigidos, int maximoTrancamentos) {}
    record AdicionarDisciplinaRequest(int disciplinaId, String tipo, int cargaHoraria, int creditos) {}
    record DependenciaRequest(int disciplinaId, int dependenciaId) {}
}
