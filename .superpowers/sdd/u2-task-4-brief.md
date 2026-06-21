### Task 4: Camada de aplicação (`BolsaResumo` + serviço/repositório de leitura)

**Files:**
- Create: `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/gestaofinanceira/BolsaResumo.java`
- Create: `aplicacao/.../gestaofinanceira/BolsaRepositorioAplicacao.java`
- Create: `aplicacao/.../gestaofinanceira/BolsaServicoAplicacao.java`

**Interfaces:**
- Produces (consumido Tasks 5,7 e front Task 8): `BolsaResumo(int id, int estudanteId, String tipo, BigDecimal percentual, LocalDate validade, String status)`; `BolsaRepositorioAplicacao { List<BolsaResumo> listarResumos(); }` (nome distinto de `BolsaRepositorio.listar()` para evitar colisão por erasure, já que a impl única implementa as duas portas); `BolsaServicoAplicacao` com `listar()`.

- [ ] **Step 1: Criar `BolsaResumo`**

```java
package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BolsaResumo(int id, int estudanteId, String tipo, BigDecimal percentual,
        LocalDate validade, String status) {}
```

- [ ] **Step 2: Criar a porta de leitura**

```java
package school.cesar.acadlab.aplicacao.gestaofinanceira;

import java.util.List;

public interface BolsaRepositorioAplicacao {
    List<BolsaResumo> listarResumos();
}
```

- [ ] **Step 3: Criar o serviço de aplicação**

```java
package school.cesar.acadlab.aplicacao.gestaofinanceira;

import static org.apache.commons.lang3.Validate.notNull;
import java.util.List;

public class BolsaServicoAplicacao {
    private final BolsaRepositorioAplicacao repositorio;

    public BolsaServicoAplicacao(BolsaRepositorioAplicacao repositorio) {
        notNull(repositorio, "repositório obrigatório");
        this.repositorio = repositorio;
    }

    public List<BolsaResumo> listar() { return repositorio.listarResumos(); }
}
```

- [ ] **Step 4: Compilar**

Run: `mvn -q -pl aplicacao -am compile`
Expected: BUILD SUCCESS.

---

