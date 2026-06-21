### Task 2: `BolsaRepositorio` + `BolsaServico` + BDD do ciclo de vida

**Files:**
- Create: `.../dominio/gestaofinanceira/bolsa/BolsaRepositorio.java`
- Create: `.../dominio/gestaofinanceira/bolsa/BolsaServico.java`
- Test: `.../test/.../gestaofinanceira/bolsa/BolsaRepositorioFake.java`
- Test: `.../test/.../gestaofinanceira/bolsa/BolsaFuncionalidade.java`
- Test: `.../test/.../gestaofinanceira/bolsa/CicloVidaBolsaSteps.java`
- Test resources: `.../test/resources/school/cesar/acadlab/dominio/gestaofinanceira/bolsa/conceder_bolsa.feature`, `suspender_reativar_bolsa.feature`, `renovar_bolsa.feature`

**Interfaces:**
- Consumes: `Bolsa` e eventos (Task 1); `EventoBarramento` (`school.cesar.acadlab.dominio.evento`).
- Produces (consumido Tasks 3-7): `BolsaRepositorio { BolsaId proximoId(); void salvar(Bolsa); Bolsa obter(BolsaId); java.util.List<Bolsa> listar(); }`; `BolsaServico` com `conceder(EstudanteId,TipoBolsa,BigDecimal,LocalDate)→Bolsa`, `suspender(BolsaId)`, `reativar(BolsaId)`, `solicitarRenovacao(BolsaId)`, `renovar(BolsaId,LocalDate)`.

- [ ] **Step 1: Criar a porta `BolsaRepositorio`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import java.util.List;

public interface BolsaRepositorio {
    BolsaId proximoId();
    void salvar(Bolsa bolsa);
    Bolsa obter(BolsaId id);
    List<Bolsa> listar();
}
```

- [ ] **Step 2: Escrever as features (pt)**

`conceder_bolsa.feature`:
```gherkin
# language: pt

Funcionalidade: Conceder bolsa

  Cenário: Conceder bolsa de mérito ativa
    Quando concedo uma bolsa MERITO de 50 por cento ao estudante 1 com validade "2025-12-31"
    Então a bolsa está com status "ATIVA"
```

`suspender_reativar_bolsa.feature`:
```gherkin
# language: pt

Funcionalidade: Suspender e reativar bolsa

  Cenário: Suspender uma bolsa ativa
    Dado uma bolsa ATIVA do estudante 1
    Quando suspendo a bolsa
    Então a bolsa está com status "SUSPENSA"

  Cenário: Reativar uma bolsa suspensa
    Dado uma bolsa ATIVA do estudante 1
    E a bolsa está suspensa
    Quando reativo a bolsa
    Então a bolsa está com status "ATIVA"
```

`renovar_bolsa.feature`:
```gherkin
# language: pt

Funcionalidade: Renovar bolsa

  Cenário: Solicitar renovação coloca a bolsa em renovação
    Dado uma bolsa ATIVA do estudante 1
    Quando solicito a renovação da bolsa
    Então a bolsa está com status "EM_RENOVACAO"
```

- [ ] **Step 3: Rodar e ver falhar**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=RunCucumberTest`
Expected: FALHA — passos/serviço/contexto de bolsa indefinidos (e compilação dos steps).

- [ ] **Step 4: Criar `BolsaServico`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import static org.apache.commons.lang3.Validate.notNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;

public class BolsaServico {
    private final BolsaRepositorio repositorio;
    private final EventoBarramento barramento;

    public BolsaServico(BolsaRepositorio repositorio, EventoBarramento barramento) {
        notNull(repositorio, "repositório obrigatório");
        notNull(barramento, "barramento obrigatório");
        this.repositorio = repositorio;
        this.barramento = barramento;
    }

    public Bolsa conceder(EstudanteId estudanteId, TipoBolsa tipo, BigDecimal percentual, LocalDate validade) {
        var bolsa = Bolsa.conceder(repositorio.proximoId(), estudanteId, tipo, percentual, validade);
        repositorio.salvar(bolsa);
        barramento.postar(bolsa.eventoConcessao());
        return bolsa;
    }

    public void suspender(BolsaId id) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.suspender();
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }

    public void reativar(BolsaId id) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.reativar();
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }

    public void solicitarRenovacao(BolsaId id) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.solicitarRenovacao();
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }

    public void renovar(BolsaId id, LocalDate novaValidade) {
        var bolsa = repositorio.obter(id);
        var evento = bolsa.renovar(novaValidade);
        repositorio.salvar(bolsa);
        barramento.postar(evento);
    }
}
```

- [ ] **Step 5: Criar o duplo in-memory `BolsaRepositorioFake`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import java.util.*;

public class BolsaRepositorioFake implements BolsaRepositorio {
    private int seq = 1;
    private final Map<BolsaId, Bolsa> store = new HashMap<>();

    @Override public BolsaId proximoId() { return new BolsaId(seq++); }
    @Override public void salvar(Bolsa bolsa) { store.put(bolsa.getId(), bolsa); }
    @Override public Bolsa obter(BolsaId id) { return Optional.ofNullable(store.get(id)).orElseThrow(); }
    @Override public java.util.List<Bolsa> listar() { return new ArrayList<>(store.values()); }
}
```

- [ ] **Step 6: Criar o contexto compartilhado `BolsaFuncionalidade`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;

public class BolsaFuncionalidade {
    public final BolsaRepositorioFake repositorio = new BolsaRepositorioFake();
    public final EventoBarramento barramento = new BarramentoStub();
    public final BolsaServico servico = new BolsaServico(repositorio, barramento);
    public BolsaId ultimaBolsa;
    public RuntimeException excecao;

    static class BarramentoStub implements EventoBarramento {
        @Override public <E> void adicionar(EventoObservador<E> o) {}
        @Override public <E> void postar(E e) {}
    }
}
```

- [ ] **Step 7: Criar os passos `CicloVidaBolsaSteps`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CicloVidaBolsaSteps {
    private final BolsaFuncionalidade ctx;
    public CicloVidaBolsaSteps(BolsaFuncionalidade ctx) { this.ctx = ctx; }

    @Quando("concedo uma bolsa {word} de {int} por cento ao estudante {int} com validade {string}")
    public void concedo(String tipo, int pct, int estudante, String validade) {
        var b = ctx.servico.conceder(new EstudanteId(estudante), TipoBolsa.valueOf(tipo),
                new BigDecimal(pct), LocalDate.parse(validade));
        ctx.ultimaBolsa = b.getId();
    }

    @Dado("uma bolsa ATIVA do estudante {int}")
    public void bolsaAtiva(int estudante) {
        var b = ctx.servico.conceder(new EstudanteId(estudante), TipoBolsa.MERITO,
                new BigDecimal("50"), LocalDate.of(2025, 12, 31));
        ctx.ultimaBolsa = b.getId();
    }

    @E("a bolsa está suspensa")
    public void jaSuspensa() { ctx.servico.suspender(ctx.ultimaBolsa); }

    @Quando("suspendo a bolsa")
    public void suspendo() { ctx.servico.suspender(ctx.ultimaBolsa); }

    @Quando("reativo a bolsa")
    public void reativo() { ctx.servico.reativar(ctx.ultimaBolsa); }

    @Quando("solicito a renovação da bolsa")
    public void solicitoRenovacao() { ctx.servico.solicitarRenovacao(ctx.ultimaBolsa); }

    @Entao("a bolsa está com status {string}")
    public void statusEsperado(String status) {
        Assertions.assertEquals(StatusBolsa.valueOf(status), ctx.repositorio.obter(ctx.ultimaBolsa).getStatus());
    }
}
```

- [ ] **Step 8: Rodar todos os testes do módulo e ver passar**

Run: `mvn -q -pl dominio-gestao-financeira test`
Expected: PASS — features novas de bolsa + `BolsaTest` + as 42 cobranças existentes.

---

