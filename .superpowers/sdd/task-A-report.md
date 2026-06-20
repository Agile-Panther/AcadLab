# Task A Report — F-09 Backend Integration (Tasks 1 & 2)

## 1. Status

DONE

## 2. Files Created or Modified

### Modified
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/atividadescomplementares/AtividadeComplementarRepositorioAplicacao.java`
  — Added `pesquisarPorStatus(String status)` to interface.

- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/atividadescomplementares/AtividadeComplementarServicoAplicacao.java`
  — Added `pesquisarPorStatus(String status)` delegation method.

- `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/AtividadeComplementarJpa.java`
  — Added `findByStatus(StatusAtividade status)` to `AtividadeComplementarJpaRepository`.
  — Added `pesquisarPorStatus(String status)` override to `AtividadeComplementarRepositorioImpl`.

- `apresentacao-backend/src/main/java/school/cesar/acadlab/apresentacao/atividadescomplementares/AtividadeComplementarControlador.java`
  — Added `import org.springframework.web.bind.annotation.RequestParam`.
  — Added `import school.cesar.acadlab.aplicacao.atividadescomplementares.CategoriaHorasResumo`.
  — Added `import school.cesar.acadlab.aplicacao.atividadescomplementares.CategoriaHorasServicoAplicacao`.
  — Added `@Autowired CategoriaHorasServicoAplicacao categoriaServicoAplicacao`.
  — Added endpoint `GET /backend/atividades-complementares?status={STATUS}` (`pesquisarPorStatus`).
  — Added endpoint `GET /backend/atividades-complementares/categorias` (`listarCategorias`).

- `apresentacao-backend/src/main/java/school/cesar/acadlab/BackendAplicacao.java`
  — Added imports for `CategoriaHorasRepositorioAplicacao` and `CategoriaHorasServicoAplicacao`.
  — Added `@Bean categoriaHorasServicoAplicacao(...)`.

### Created
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/atividadescomplementares/CategoriaHorasResumo.java`
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/atividadescomplementares/CategoriaHorasRepositorioAplicacao.java`
- `aplicacao/src/main/java/school/cesar/acadlab/aplicacao/atividadescomplementares/CategoriaHorasServicoAplicacao.java`
- `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/CategoriaAtividadeJpa.java`
- `apresentacao-backend/src/main/resources/db/migration/V4__categorias_atividade.sql`

## 3. Verification Commands and Results

### Compile
```
Command: mvn -pl apresentacao-backend -am compile
Result:
[INFO] BUILD SUCCESS
[INFO] Total time:  4.509 s
[INFO] Finished at: 2026-06-21T14:38:39-03:00
```

### Domain Test Suite
```
Command: mvn -pl dominio-atividades-complementares test
Result:
[INFO] Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
[INFO] Total time:  5.488 s
[INFO] Finished at: 2026-06-21T14:38:56-03:00
```

## 4. Concerns / Deviations

None. All code follows the plan verbatim. The SQL seed uses exactly the category names and hour limits from the plan. The `V4__categorias_atividade.sql` is the next available migration version (V1–V3 already existed).

## 5. Self-Review

- **Added anything not in the plan?** No.
- **Missed anything?** No. All steps in Task 1 (Steps 1–4) and Task 2 (Steps 1–7) were implemented. Tasks 3–5 (frontend) were intentionally skipped per instructions.
- The `pesquisarPorStatus` endpoint uses `@RequestMapping(method = GET)` at the root path, consistent with the plan's instruction to place it "ao lado de pesquisarPorEstudante".
- The `listarCategorias` endpoint uses `path = "categorias"` as specified.
- No commits were made.
- No server was started; no HTTP endpoints were called.
