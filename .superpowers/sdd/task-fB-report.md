# Task fB Report — F-13 Sub-projeto 0: Integração Core Frontend

## 1. Status

**DONE**

## 2. Files Created / Modified

| Action   | Path |
|----------|------|
| Modified | `E:\Projetos\AcadLab\apresentacao-frontend\react\src\lib\config.ts` |
| Modified | `E:\Projetos\AcadLab\apresentacao-frontend\react\src\lib\format.ts` |
| Created  | `E:\Projetos\AcadLab\apresentacao-frontend\react\src\lib\financeiro.ts` |
| Modified | `E:\Projetos\AcadLab\apresentacao-frontend\react\src\routes\financeiro.tsx` |

### What changed per file

- **config.ts**: Added `contratoId: 1` to `USUARIO_ATUAL` (contrato do estudante 1 no seed).
- **format.ts**: Added `formatMoeda` function at the end of the file (BRL currency formatter using `toLocaleString("pt-BR")`).
- **financeiro.ts** (new): Full lib file with TS types mirroring backend DTOs (`CobrancaResumo`, `PagamentoResumo`, `ContestacaoResumo`, `DescontoResumo`, `StatusCobranca`) and hooks: `useExtrato`, `useContestacoesAbertas`, `useRegistrarPagamento`, `useContestar`, `useResolverContestacao`. Pattern matches `atividades.ts` exactly.
- **financeiro.tsx**: Data wiring only — see diff summary below.

## 3. Build Command and Result

```
cd /e/Projetos/AcadLab/apresentacao-frontend/react && npm run build
```

Result (tail):
```
✓ 2047 modules transformed.
dist/client/assets/financeiro-3LsXYMVh.js   18.44 kB │ gzip:  5.21 kB
✓ built in 2.38s

vite v8.0.16 building ssr environment for production...
✓ 137 modules transformed.
dist/server/assets/financeiro-CcsCler5.js   30.73 kB │ gzip:  6.65 kB
✓ built in 1.62s
```

**BUILD SUCCESS — zero TypeScript/build errors.**

## 4. git diff --stat for financeiro.tsx

```
.../react/src/routes/financeiro.tsx | 165 ++++++++++++---------
 1 file changed, 95 insertions(+), 70 deletions(-)
```

The delta reflects: removal of the mock `Cobranca` type, `cobrancasIniciais` array, `Contestacao` type, `contest` state, `decidir` function, and the mock `pagar` handler; plus replacement with react-query hooks, derived stats, and updated column render functions. No Tailwind classes or layout structure changed.

## 5. Concerns, Deviations, and Visual-Preservation Judgment Calls

### Deviations from plan (minor, intentional)

1. **`motivo` state hoisted to `Page` level**: The plan said "Textarea controlado por estado local `motivo`" inside the `contestar` view. Since views are conditionally rendered inline (IIFE pattern), the `motivo` state was added at `Page` level to avoid re-initializing on re-render. Functionally identical; motivo resets naturally when the view switches.

2. **`subtitle` for estudante view**: The plan spec says `Estudante #id` convention. The subtitle now reads `Estudante #1 · Contrato CT-2020-0451` (hardcoded `1` from `USUARIO_ATUAL.estudanteId`), which matches the convention while preserving the existing visual structure (same length string, same position in `AppShell`).

3. **`detail` view guard**: Added a `if (!c) return null;` guard after `cobrancas.find(...)` because now `cobrancas` starts empty (loading state), so the IIFE could receive `undefined`. This prevents a runtime crash with no visual difference — the detail panel simply won't render while data loads.

4. **`StatusCobranca` type import**: Imported but only used indirectly via `CobrancaResumo.status`. TypeScript is fine with this (no unused import error since it's in the `import type` position alongside used types).

5. **`bolsaPct` with empty array**: `Math.max(0, ...cobrancas.map(...))` — when `cobrancas` is empty, `Math.max(0)` returns `0`, which is correct ("Bolsa aplicada: 0%").

### Visual preservation

All Tailwind class strings, component names (`StatsRow`, `DataTable`, `TabsRow`, `SectionTitle`, `StatusBadge`, `RowActionButton`, `FormField`, `SuccessBanner`, `ValidationCallout`), JSX structure, and prop names are identical to the original. Columns for Inadimplência, Bolsas, Conciliação, Relatórios are byte-for-byte identical to original.

## 6. Self-Review

### What was done beyond the plan?
- Nothing. All changes are strictly within the scope defined in Tasks 3, 4, and 5.

### What was missed?
- Nothing identified. All checklist steps implemented:
  - Task 3: `config.ts` (contratoId), `format.ts` (formatMoeda), `financeiro.ts` (types + 5 hooks).
  - Task 4: `View` type with numeric ids, student view wired to `useExtrato`/`useRegistrarPagamento`/`useContestar`, all 5 sub-views (overview, detail, pagar, contestar, comprovante) updated.
  - Task 5: `FinanceiroView` wired to `useContestacoesAbertas`/`useResolverContestacao`, contest tab columns replaced, `TabsRow` count + StatsRow count updated from `contestacoesAbertas.length`.

### Confirm 4 untouched tabs
- **Inadimplência**: `inadimp` state, `notificar`/`negociar`/`bloquear` functions, `filtroInad`, `inadimpFiltrada`, and the full `DataTable` JSX are **identical to original**.
- **Bolsas & Descontos**: `bolsas` state, `novaBolsaOpen`, `suspenderBolsa`/`reativarBolsa`, and full JSX including the "Nova concessão" form are **identical to original**.
- **Conciliação**: `lancs` state, `conciliar`, and full `DataTable` JSX are **identical to original**.
- **Relatórios**: the static cards array and JSX are **identical to original**.
- The `Contestacao` type was removed (it was only used for the mock contest state). The remaining types (`Inadimplente`, `Bolsa`, `Lancamento`) are preserved exactly.
