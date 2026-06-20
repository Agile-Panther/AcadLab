### Task 7: Beans + `BolsaControlador` + wiring do `CobrancaServico`

**Files:**
- Modify: `apresentacao-backend/src/main/java/school/cesar/acadlab/BackendAplicacao.java`
- Create: `apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/BolsaControlador.java`

**Interfaces:**
- Consumes: `BolsaServico`/`BolsaRepositorio` (Task 2), `AutorizacaoDescontoPorBolsa` (Task 3), `BolsaServicoAplicacao`/`BolsaRepositorioAplicacao` (Task 4), `CobrancaServico`/`CobrancaRepositorio`/`VerificadorMatriculaConfirmada`/`VerificadorAutorizacaoDesconto`/`EventoBarramento` (existentes), `BolsaResumo` (Task 4).

- [ ] **Step 1: Registrar os beans em `BackendAplicacao`**

Adicionar imports:
```java
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaServicoAplicacao;
import school.cesar.acadlab.dominio.gestaofinanceira.CobrancaServico;
import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorAutorizacaoDesconto;
import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorMatriculaConfirmada;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.CobrancaRepositorio;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.AutorizacaoDescontoPorBolsa;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaRepositorio;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaServico;
```

Adicionar os beans (próximo aos beans de gestão financeira existentes):
```java
    @Bean
    BolsaServico bolsaServico(BolsaRepositorio repositorio, EventoBarramento barramento) {
        return new BolsaServico(repositorio, barramento);
    }

    @Bean
    BolsaServicoAplicacao bolsaServicoAplicacao(BolsaRepositorioAplicacao repositorio) {
        return new BolsaServicoAplicacao(repositorio);
    }

    @Bean
    VerificadorAutorizacaoDesconto verificadorAutorizacaoDesconto(BolsaRepositorio repositorio) {
        return new AutorizacaoDescontoPorBolsa(repositorio);
    }

    @Bean
    CobrancaServico cobrancaServico(CobrancaRepositorio repositorio,
            VerificadorMatriculaConfirmada verificadorMatricula,
            VerificadorAutorizacaoDesconto verificadorAutorizacao,
            EventoBarramento barramento) {
        return new CobrancaServico(repositorio, verificadorMatricula, verificadorAutorizacao, barramento);
    }
```

> `VerificadorMatriculaConfirmada` é satisfeito pelo `@Component VerificadorMatriculaConfirmadaJpa` (Task 6); `CobrancaRepositorio` e `BolsaRepositorio` pelos `@Repository` da infra; `EventoBarramento` pelo bean existente.

- [ ] **Step 2: Criar `BolsaControlador`**

```java
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
```

- [ ] **Step 3: Compilar**

Run: `mvn -q -pl apresentacao-backend -am compile`
Expected: BUILD SUCCESS.

---

