package school.cesar.acadlab.apresentacao.gestaofinanceira;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaResumo;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaServicoAplicacao;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaId;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaServico;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.TipoBolsa;

@RestController
@RequestMapping("backend/bolsas")
class BolsaControlador {
    @Autowired
    private BolsaServico servico;

    @Autowired
    private BolsaServicoAplicacao servicoAplicacao;

    @RequestMapping(method = GET)
    List<BolsaResumo> listar() {
        return servicoAplicacao.listar();
    }

    @RequestMapping(method = POST, path = "conceder")
    void conceder(@RequestBody ConcederRequest request) {
        servico.conceder(new EstudanteId(request.estudanteId()), TipoBolsa.valueOf(request.tipo()),
                request.percentual(), request.validade());
    }

    @RequestMapping(method = POST, path = "{id}/suspender")
    void suspender(@PathVariable int id) { servico.suspender(new BolsaId(id)); }

    @RequestMapping(method = POST, path = "{id}/reativar")
    void reativar(@PathVariable int id) { servico.reativar(new BolsaId(id)); }

    @RequestMapping(method = POST, path = "{id}/renovar")
    void renovar(@PathVariable int id) { servico.solicitarRenovacao(new BolsaId(id)); }

    record ConcederRequest(int estudanteId, String tipo, BigDecimal percentual, LocalDate validade) {}
}
