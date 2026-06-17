package school.cesar.acadlab.apresentacao.integralizacao;

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

import school.cesar.acadlab.aplicacao.integralizacao.ColacaoResumo;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoResumo;
import school.cesar.acadlab.aplicacao.integralizacao.IntegralizacaoServicoAplicacao;
import school.cesar.acadlab.dominio.integralizacao.ColacaoServico;
import school.cesar.acadlab.dominio.integralizacao.CoordenadorId;
import school.cesar.acadlab.dominio.integralizacao.EstudanteId;
import school.cesar.acadlab.dominio.integralizacao.IntegralizacaoOperacoes;
import school.cesar.acadlab.dominio.integralizacao.MatrizCurricularId;
import school.cesar.acadlab.dominio.integralizacao.checklist.ItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.checklist.TipoItemChecklist;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.IntegralizacaoId;
import school.cesar.acadlab.dominio.integralizacao.integralizacao.StatusIntegralizacao;

@RestController
@RequestMapping("backend/integralizacoes")
class IntegralizacaoControlador {

    @Autowired
    private IntegralizacaoOperacoes integralizacaoOperacoes;

    @Autowired
    private ColacaoServico colacaoServico;

    @Autowired
    private IntegralizacaoServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    List<IntegralizacaoResumo> buscarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarIntegralizacoesPorEstudante(estudanteId);
    }

    @RequestMapping(method = GET, path = "{id}")
    Optional<IntegralizacaoResumo> buscarPorId(@PathVariable int id) {
        return servicoAplicacao.buscarIntegralizacaoPorId(id);
    }

    @RequestMapping(method = POST)
    int iniciarAnalise(@RequestBody IniciarAnaliseRequest request) {
        return integralizacaoOperacoes.iniciarAnalise(
                new EstudanteId(request.estudanteId()),
                new MatrizCurricularId(request.matrizCurricularId())).getId().getId();
    }

    @RequestMapping(method = PUT, path = "{id}/checklist")
    void gerarChecklist(@PathVariable int id, @RequestBody List<ItemChecklistRequest> itens) {
        var itensChecklist = itens.stream()
                .map(i -> new ItemChecklist(
                        TipoItemChecklist.valueOf(i.tipo()), i.descricao(), i.cumprido()))
                .toList();
        integralizacaoOperacoes.gerarChecklist(new IntegralizacaoId(id), itensChecklist);
    }

    @RequestMapping(method = PUT, path = "{id}/resultado")
    void registrarResultado(@PathVariable int id, @RequestBody ResultadoRequest request) {
        integralizacaoOperacoes.registrarResultado(
                new IntegralizacaoId(id), StatusIntegralizacao.valueOf(request.resultado()));
    }

    @RequestMapping(method = PUT, path = "{id}/aptidao")
    void aprovarAptidao(@PathVariable int id, @RequestBody AprovarAptidaoRequest request) {
        integralizacaoOperacoes.aprovarAptidao(
                new IntegralizacaoId(id), new CoordenadorId(request.coordenadorId()));
    }

    @RequestMapping(method = GET, path = "colacao/estudante/{estudanteId}")
    Optional<ColacaoResumo> buscarColacaoPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.buscarColacaoPorEstudante(estudanteId);
    }

    @RequestMapping(method = POST, path = "{id}/colacao")
    int registrarColacao(@PathVariable int id, @RequestBody RegistrarColacaoRequest request) {
        return colacaoServico.registrar(
                new IntegralizacaoId(id),
                request.dataCerimonia(),
                request.local()).getId().getId();
    }

    record IniciarAnaliseRequest(int estudanteId, int matrizCurricularId) {}
    record ItemChecklistRequest(String tipo, String descricao, boolean cumprido) {}
    record ResultadoRequest(String resultado) {}
    record AprovarAptidaoRequest(int coordenadorId) {}
    record RegistrarColacaoRequest(LocalDate dataCerimonia, String local) {}
}
