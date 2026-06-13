package school.cesar.acadlab.apresentacao.atividadescomplementares;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarResumo;
import school.cesar.acadlab.aplicacao.atividadescomplementares.AtividadeComplementarServicoAplicacao;
import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarId;
import school.cesar.acadlab.dominio.atividadescomplementares.AtividadeComplementarServico;
import school.cesar.acadlab.dominio.atividadescomplementares.CategoriaAtividadeId;
import school.cesar.acadlab.dominio.atividadescomplementares.EstudanteId;

@RestController
@RequestMapping("backend/atividades-complementares")
class AtividadeComplementarControlador {
    @Autowired
    private AtividadeComplementarServico servico;

    @Autowired
    private AtividadeComplementarServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET, path = "estudante/{estudanteId}")
    List<AtividadeComplementarResumo> pesquisarPorEstudante(@PathVariable int estudanteId) {
        return servicoAplicacao.pesquisarPorEstudante(estudanteId);
    }

    @RequestMapping(method = GET, path = "estudante/{estudanteId}/saldo")
    Map<CategoriaAtividadeId, Integer> consultarSaldo(@PathVariable int estudanteId) {
        return servico.calcularSaldoHoras(new EstudanteId(estudanteId));
    }

    @RequestMapping(method = POST, path = "submeter")
    void submeter(@RequestBody SubmeterRequest request) {
        servico.submeter(
                new EstudanteId(request.estudanteId()),
                new CategoriaAtividadeId(request.categoriaId()),
                request.horas(),
                request.dataRealizacao(),
                request.identificadorCertificado(),
                request.descricao());
    }

    @RequestMapping(method = POST, path = "{id}/deferir")
    void deferir(@PathVariable int id, @RequestBody int horasAprovadas) {
        servico.deferir(new AtividadeComplementarId(id), horasAprovadas);
    }

    @RequestMapping(method = POST, path = "{id}/indeferir")
    void indeferir(@PathVariable int id, @RequestBody String justificativa) {
        servico.indeferir(new AtividadeComplementarId(id), justificativa);
    }

    @RequestMapping(method = POST, path = "{id}/solicitar-revisao")
    void solicitarRevisao(@PathVariable int id, @RequestBody String justificativa) {
        servico.solicitarRevisao(new AtividadeComplementarId(id), justificativa);
    }

    @RequestMapping(method = DELETE, path = "{id}/cancelar")
    void cancelar(@PathVariable int id) {
        servico.cancelar(new AtividadeComplementarId(id));
    }

    record SubmeterRequest(
            int estudanteId,
            int categoriaId,
            int horas,
            LocalDate dataRealizacao,
            String identificadorCertificado,
            String descricao) {}
}
