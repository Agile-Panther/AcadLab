package school.cesar.acadlab.apresentacao.periodoletivo;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoResumo;
import school.cesar.acadlab.aplicacao.periodoletivo.PeriodoLetivoServicoAplicacao;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoId;
import school.cesar.acadlab.dominio.periodoletivo.PeriodoLetivoServico;
import school.cesar.acadlab.dominio.periodoletivo.TipoJanela;
import school.cesar.acadlab.dominio.periodoletivo.curso.CursoId;

@RestController
@RequestMapping("backend/periodos-letivos")
class PeriodoLetivoControlador {
    @Autowired
    private PeriodoLetivoServico servico;

    @Autowired
    private PeriodoLetivoServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "curso/{cursoId}")
    List<PeriodoLetivoResumo> pesquisarPorCurso(@PathVariable int cursoId) {
        return servicoAplicacao.pesquisarPorCurso(cursoId);
    }

    @RequestMapping(method = POST)
    void cadastrar(@RequestBody CadastrarRequest request) {
        servico.cadastrar(
                new CursoId(request.cursoId()),
                request.ano(),
                request.semestre(),
                request.dataInicio(),
                request.dataFim());
    }

    @RequestMapping(method = POST, path = "{id}/janela")
    void definirJanela(@PathVariable int id, @RequestBody DefinirJanelaRequest request) {
        servico.definirJanela(
                new PeriodoLetivoId(id),
                request.tipo(),
                request.inicio(),
                request.fim());
    }

    @RequestMapping(method = POST, path = "{id}/encerrar")
    void encerrar(@PathVariable int id) {
        servico.encerrar(new PeriodoLetivoId(id));
    }

    @RequestMapping(method = PUT, path = "{id}")
    void editar(@PathVariable int id, @RequestBody EditarRequest request) {
        servico.editar(new PeriodoLetivoId(id), request.dataInicio(), request.dataFim());
    }

    @RequestMapping(method = DELETE, path = "{id}/cancelar")
    void cancelar(@PathVariable int id) {
        servico.cancelar(new PeriodoLetivoId(id));
    }

    record CadastrarRequest(int cursoId, int ano, int semestre, LocalDate dataInicio, LocalDate dataFim) {}
    record DefinirJanelaRequest(TipoJanela tipo, LocalDate inicio, LocalDate fim) {}
    record EditarRequest(LocalDate dataInicio, LocalDate dataFim) {}
}
