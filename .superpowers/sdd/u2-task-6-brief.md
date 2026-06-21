### Task 6: Adapter `VerificadorMatriculaConfirmada` (infra) + finder

**Files:**
- Modify: `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/MatriculaJpa.java` (adicionar finder ao `MatriculaJpaRepository`)
- Create: `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/VerificadorMatriculaConfirmadaJpa.java`

**Interfaces:**
- Consumes: `MatriculaJpaRepository` (package-private, mesmo pacote); `StatusMatricula` (`dominio.matricula.matricula`); porta `VerificadorMatriculaConfirmada` + `EstudanteId`/`PeriodoLetivoId` (`dominio.gestaofinanceira`).
- Produces (consumido Task 7): `@Component VerificadorMatriculaConfirmadaJpa implements VerificadorMatriculaConfirmada`.

- [ ] **Step 1: Adicionar finder ao `MatriculaJpaRepository`**

Em `MatriculaJpa.java`, dentro de `interface MatriculaJpaRepository`, adicionar (o import de `StatusMatricula` já existe no arquivo):
```java
    boolean existsByEstudanteIdAndPeriodoLetivoIdAndStatus(int estudanteId, int periodoLetivoId, StatusMatricula status);
```

- [ ] **Step 2: Criar o adapter**

```java
package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.PeriodoLetivoId;
import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorMatriculaConfirmada;
import school.cesar.acadlab.dominio.matricula.matricula.StatusMatricula;

@Component
class VerificadorMatriculaConfirmadaJpa implements VerificadorMatriculaConfirmada {
    @Autowired
    MatriculaJpaRepository repositorio;

    @Override
    public boolean possuiMatricula(EstudanteId estudanteId, PeriodoLetivoId periodoLetivoId) {
        return repositorio.existsByEstudanteIdAndPeriodoLetivoIdAndStatus(
                estudanteId.valor(), periodoLetivoId.valor(), StatusMatricula.CONFIRMADA);
    }
}
```

- [ ] **Step 3: Compilar**

Run: `mvn -q -pl infraestrutura -am compile`
Expected: BUILD SUCCESS.

---

