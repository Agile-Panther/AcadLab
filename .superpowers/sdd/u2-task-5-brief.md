### Task 5: Infra — `BolsaJpa` + seed `V5`

**Files:**
- Create: `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/BolsaJpa.java`
- Create: `apresentacao-backend/src/main/resources/db/migration/V5__bolsas.sql`

**Interfaces:**
- Consumes: `Bolsa`/`BolsaId`/`TipoBolsa`/`StatusBolsa`/`BolsaRepositorio` (Tasks 1-2); `BolsaResumo`/`BolsaRepositorioAplicacao` (Task 4); `EstudanteId` (gestaofinanceira).
- Produces: `@Repository BolsaRepositorioImpl implements BolsaRepositorio, BolsaRepositorioAplicacao`.

- [ ] **Step 1: Criar `BolsaJpa` (entidade + repositório + impl)**

```java
package school.cesar.acadlab.infraestrutura.persistencia.jpa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaRepositorioAplicacao;
import school.cesar.acadlab.aplicacao.gestaofinanceira.BolsaResumo;
import school.cesar.acadlab.dominio.gestaofinanceira.EstudanteId;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.Bolsa;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaId;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.BolsaRepositorio;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.StatusBolsa;
import school.cesar.acadlab.dominio.gestaofinanceira.bolsa.TipoBolsa;

@Entity
@Table(name = "BOLSA")
class BolsaJpa {
    @Id
    int id;
    int estudanteId;
    @Enumerated(EnumType.STRING)
    TipoBolsa tipo;
    BigDecimal percentual;
    LocalDate validade;
    @Enumerated(EnumType.STRING)
    StatusBolsa status;
}

interface BolsaJpaRepository extends JpaRepository<BolsaJpa, Integer> {
    @Query("SELECT COALESCE(MAX(b.id), 0) + 1 FROM BolsaJpa b")
    int proximoId();
}

@Repository
class BolsaRepositorioImpl implements BolsaRepositorio, BolsaRepositorioAplicacao {
    @Autowired
    BolsaJpaRepository repositorio;

    @Override
    public BolsaId proximoId() { return new BolsaId(repositorio.proximoId()); }

    @Override
    public void salvar(Bolsa bolsa) {
        var jpa = new BolsaJpa();
        jpa.id = bolsa.getId().valor();
        jpa.estudanteId = bolsa.getEstudanteId().valor();
        jpa.tipo = bolsa.getTipo();
        jpa.percentual = bolsa.getPercentual();
        jpa.validade = bolsa.getValidade();
        jpa.status = bolsa.getStatus();
        repositorio.save(jpa);
    }

    @Override
    public Bolsa obter(BolsaId id) { return toDomain(repositorio.findById(id.valor()).orElseThrow()); }

    @Override
    public List<Bolsa> listar() { return repositorio.findAll().stream().map(this::toDomain).toList(); }

    @Override
    public List<BolsaResumo> listarResumos() {  // BolsaRepositorioAplicacao
        return repositorio.findAll().stream()
                .map(jpa -> new BolsaResumo(jpa.id, jpa.estudanteId, jpa.tipo.name(),
                        jpa.percentual, jpa.validade, jpa.status.name()))
                .toList();
    }

    private Bolsa toDomain(BolsaJpa jpa) {
        return Bolsa.reconstituir(new BolsaId(jpa.id), new EstudanteId(jpa.estudanteId),
                jpa.tipo, jpa.percentual, jpa.validade, jpa.status);
    }
}
```

> Nota: `BolsaRepositorio.listar()` retorna `List<Bolsa>` e `BolsaRepositorioAplicacao.listarResumos()` retorna `List<BolsaResumo>` — nomes distintos evitam a colisão por erasure (a impl única implementa as duas portas).

- [ ] **Step 2: Criar a migração `V5__bolsas.sql`**

```sql
-- ─── BOLSAS (F-13 sub-A) ─────────────────────────────────────────────────────
INSERT INTO bolsa (id, estudante_id, tipo, percentual, validade, status) VALUES
  (1, 1, 'MERITO',   50.00, '2025-12-31', 'ATIVA'),
  (2, 2, 'PROUNI',  100.00, '2026-12-31', 'ATIVA'),
  (3, 3, 'FIES',     75.00, '2025-06-30', 'EM_RENOVACAO'),
  (4, 4, 'CONVENIO', 20.00, '2024-12-31', 'SUSPENSA')
ON CONFLICT (id) DO NOTHING;
```

- [ ] **Step 3: Compilar**

Run: `mvn -q -pl infraestrutura -am compile`
Expected: BUILD SUCCESS.

---

