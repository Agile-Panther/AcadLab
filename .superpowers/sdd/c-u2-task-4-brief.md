### Task 4: Controlador — endpoints deferir/indeferir

**Files:**
- Modify: `apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/gestaofinanceira/CobrancaControlador.java:70-73` (+ records + imports)

**Interfaces:**
- Consumes: `CobrancaServico.deferirContestacao`/`indeferirContestacao` (Task 2), `ModoAjuste` (Task 1).
- Produces (consumido Task 5): `POST backend/cobrancas/{id}/deferir-contestacao` body `{modo,valor,parecer}`; `POST backend/cobrancas/{id}/indeferir-contestacao` body `{parecer}`.

- [ ] **Step 1: Substituir o endpoint de resolução**

Em `CobrancaControlador.java`, substituir o método `resolverContestacao` (linhas 70-73) por:
```java
    @RequestMapping(method = POST, path = "{id}/deferir-contestacao")
    void deferirContestacao(@PathVariable int id, @RequestBody DeferirContestacaoRequest request) {
        servico.deferirContestacao(new CobrancaId(id), ModoAjuste.valueOf(request.modo()),
                request.valor(), request.parecer());
    }

    @RequestMapping(method = POST, path = "{id}/indeferir-contestacao")
    void indeferirContestacao(@PathVariable int id, @RequestBody IndeferirContestacaoRequest request) {
        servico.indeferirContestacao(new CobrancaId(id), request.parecer());
    }
```

Adicionar o import:
```java
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste;
```

Adicionar os records (junto aos demais, antes do `}` final da classe):
```java
    record DeferirContestacaoRequest(String modo, BigDecimal valor, String parecer) {}

    record IndeferirContestacaoRequest(String parecer) {}
```

- [ ] **Step 2: Compilar**

Run: `mvn -q -pl apresentacao-backend -am compile`
Expected: BUILD SUCCESS (compila domínio+aplicação+infra+backend).

---

