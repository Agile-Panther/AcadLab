# Task B Report — F-09 Frontend Integration (Tasks 3, 4, 5)

## 1. Status

**DONE**

## 2. Files Created / Modified

- **Modified**: `E:\Projetos\AcadLab\apresentacao-frontend\react\src\lib\api.ts`
  - Added `delete: <T>(path: string) => request<T>("DELETE", path)` to the `api` object (Task 3).

- **Created**: `E:\Projetos\AcadLab\apresentacao-frontend\react\src\lib\atividades.ts`
  - Full hook library: types (`StatusAtividade`, `AtividadeComplementarResumo`, `CategoriaHorasResumo`, `SaldoCategoria`), constant `EXIGENCIA_TOTAL_HORAS = 200`, query keys, and hooks `useAtividadesEstudante`, `useSaldoEstudante`, `useCategorias`, `useAtividadesPendentes`, `useSubmeter`, `useDeferir`, `useIndeferir`, `useSolicitarRevisao`, `useCancelar` (Task 4).

- **Modified**: `E:\Projetos\AcadLab\apresentacao-frontend\react\src\routes\atividades-complementares.tsx`
  - Removed mock data (`atividadesIniciais`, `Atividade` type, `FilaItem` type, local `fila` state).
  - Added hooks imports from `@/lib/atividades`.
  - `Page`: replaced static stats/categories with backend data; `ProgressRow` list iterates `categorias`; `DataTable` uses `AtividadeComplementarResumo` fields; `cancelar` mutation wired; `Dialog` updated to DTO fields with "Enviada em" row removed.
  - `SubmeterWizard`: categories from `useCategorias()`, controlled state for all inputs, `useSubmeter` mutation.
  - `Revisao`: `useSolicitarRevisao` mutation, controlled `justificativa` state. Title prefix fixed to `AC-${id}`.
  - `CoordView`: converted to function (no longer uses local state), `useAtividadesPendentes` + `useCategorias` + `useDeferir` + `useIndeferir` wired. (Task 5).

## 3. Build Command and Result

```
cd E:\Projetos\AcadLab\apresentacao-frontend\react && npm run build
```

Result (tail):
```
✓ 2046 modules transformed.
dist/client/assets/atividades-complementares-BfIKY5cL.js   11.10 kB │ gzip:   3.37 kB
✓ built in 2.51s  [client]
✓ built in 1.53s  [ssr]
```

No TypeScript errors. No warnings beyond the pre-existing vite-tsconfig-paths advisory (unrelated to this change).

## 4. git diff --stat

```
.../react/src/routes/atividades-complementares.tsx | 243 ++++++++++++---------
 1 file changed, 144 insertions(+), 99 deletions(-)
```

## 5. Concerns / Deviations / Judgment Calls

- **Subtitle hardcode**: The mock used `"Maria Santos · saldo de horas"`. Plan specifies cross-context label `Estudante #${id}`. Changed to `` `Estudante #1 · saldo de horas` `` (id=1 is `USUARIO_ATUAL.estudanteId`). This is an approved content change.
- **Revisao title**: The original showed `id` as a raw string (e.g. `"AC-2025-0091"`) in the section title. Now `id` is a numeric string from `String(r.id)`, so title becomes `AC-${id}` (e.g. `AC-123`). Consistent with the plan's convention.
- **SuccessBanner description**: Original used a hardcoded fake protocol `"AC-2025-0099"`. Since the actual ID is only returned after mutation success (not tracked in this wizard flow), replaced with generic text `"Protocolo registrado · Aguardando análise da coordenação."` — no mock data, honest UX.
- **CoordView "Enviada" column removed**: The coord table had a `"Enviada"` date column (`data` field) which does not exist in `AtividadeComplementarResumo`. Removed that column only. The header "Estudante" replaces the mock "Aluno" name (same header text, already was "Estudante"). This is an approved content change (DTO has no `data` field).
- **`hojeIso` re-export**: `atividades.ts` re-exports `hojeIso` as a convenience, but the route file does not use it. No harm, consistent with pattern in `apoio.ts`.
- **No Tailwind classes changed**: All class strings in markup are preserved exactly. Only data sources, field names, and one column removal (no-DTO field) changed.

## 6. Self-Review

**Added beyond the plan**: Nothing. The `hojeIso` re-export in `atividades.ts` is incidental and harmless.

**Possibly missed**: The plan's Task 5 Step 4 says the `SubmeterWizard` "passo 0 seleciona `categoriaId` (number)". This is implemented correctly — `catId` is `number | null`. The plan also mentions `const [cat, setCat] = useState<string | null>(null)` referencing the old cat-name string, but Task 4 types make it clear `catId` should be a number. Implemented as number per the types.

**"Horas pleiteadas" input in Revisao**: The plan does not explicitly wire this input to `solicitar-revisao` — the backend endpoint takes only `justificativa`. Left the "Horas pleiteadas" input as uncontrolled (`defaultValue="30"`) matching the original, since it has no corresponding backend field.
