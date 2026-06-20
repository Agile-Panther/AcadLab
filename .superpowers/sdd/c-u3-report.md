# C-U3 Report — Resolução de Contestação (Frontend)

## Arquivos modificados

### 1. `apresentacao-frontend/react/src/lib/financeiro.ts`

**Removido:**
- `useResolverContestacao()` (linhas 100-107 originais) — chamava `resolver-contestacao` com apenas `parecer: string`
- Valor antigo do campo `status` em `ContestacaoResumo`: `"PENDENTE" | "RESOLVIDA" | null`

**Adicionado:**
- `ContestacaoResumo.status` atualizado para `"PENDENTE" | "DEFERIDA" | "INDEFERIDA" | null` (linha 20)
- `export type ModoAjuste = "PERCENTUAL" | "VALOR"` (nova exportação)
- `export function useDeferirContestacao()` — POST `cobrancas/{id}/deferir-contestacao` com body `{modo, valor, parecer}`
- `export function useIndeferirContestacao()` — POST `cobrancas/{id}/indeferir-contestacao` com body `{parecer}`
- Ambos os hooks reutilizam `useInvalidate()` e `api` já existentes; nenhuma redefinição.

### 2. `apresentacao-frontend/react/src/routes/financeiro.tsx`

**Somente dentro de `FinanceiroView`:**

Import:
- Substituído `useResolverContestacao` por `useDeferirContestacao, useIndeferirContestacao, type ModoAjuste`

Hooks/estado adicionados em `FinanceiroView` (acima do restante do estado existente):
- `const deferir = useDeferirContestacao()`
- `const indeferir = useIndeferirContestacao()`
- `useState` para `resolverId`, `decisao`, `modoAjuste`, `valorAjuste`, `parecerResol`
- `cobrancaResolver` (derivado de `contestacoesAbertas.find`)
- `fecharResolver` (reset do painel)
- `rotuloStatusContestacao` e `tomStatusContestacao` (helpers de exibição de status)

Aba `tab === "contestacoes"`:
- Coluna `status`: substituída de `() => <StatusBadge tone="info">Em análise</StatusBadge>` estático para render dinâmico via `tomStatusContestacao` / `rotuloStatusContestacao`
- Coluna `acoes`: substituídos dois botões (Indeferir/Deferir com `resolver.mutate`) por um único `<RowActionButton tone="info">Resolver</RowActionButton>` que chama `setResolverId(r.id)`
- Painel inline `{cobrancaResolver && <div className="rounded-xl border bg-card p-5 shadow-card">…</div>}` adicionado após `<DataTable>` dentro do mesmo `<>…</>`, seguindo o padrão da aba Bolsas

**Intocado (byte-for-byte):**
- Abas Inadimplência, Bolsas, Conciliação, Relatórios — nenhuma classe Tailwind, estrutura ou lógica alterada por esta tarefa
- Visão Estudante (`perfil === "estudante"`) — sem alteração nesta tarefa (mudanças pré-existentes na working tree não são deste escopo)

## Resultado do `npm run build`

```
✓ built in 2.56s   (client)
✓ built in 1.65s   (ssr)
```
Zero erros de tipo. Build limpo.

## Diff stat (`git diff --stat -- apresentacao-frontend/react/src/routes/financeiro.tsx`)

```
.../react/src/routes/financeiro.tsx | 306 ++++++++++++++-------
 1 file changed, 205 insertions(+), 101 deletions(-)
```

**Nota sobre o diff stat amplo:** O arquivo já possuía mudanças na working tree (anteriores a esta tarefa — U3 frontend de outras features: wiring da Visão Estudante com API real, Bolsas tab com hooks reais, etc.) que aparecem no diff em relação ao HEAD. As únicas alterações introduzidas por esta tarefa (Tasks 5 e 6) são:
1. A linha de import substituída em `financeiro.tsx`
2. Os hooks/estado de resolução adicionados em `FinanceiroView`
3. A aba `tab === "contestacoes"` (coluna status, coluna ações, painel inline)

Nenhuma das demais abas foi tocada por esta tarefa.

## Self-Review

**Cobertura da spec:**
- `ContestacaoResumo.status` atualizado: ✓
- `ModoAjuste` exportado: ✓
- `useDeferirContestacao` com `{modo, valor, parecer}`: ✓
- `useIndeferirContestacao` com `{parecer}`: ✓
- Endpoints batem com backend U1/U2: `cobrancas/{id}/deferir-contestacao`, `cobrancas/{id}/indeferir-contestacao`: ✓
- Painel inline segue padrão Bolsas: ✓
- Botão único "Resolver" no lugar de dois botões separados: ✓
- Validações UI: parecer obrigatório, valor > 0, modo VALOR ∈ [50%, 100%) do valorAtual: ✓
- Percentual PERCENTUAL ∈ {5,10,...,50} via `<select>` com opções fixas: ✓
- `StatusBadge` dinâmico para PENDENTE/DEFERIDA/INDEFERIDA: ✓
- Estudante exibido como `Estudante #${id}`: ✓ (já existente, preservado)
- Sem mudanças estéticas nas outras abas: ✓

**Concerns:**
- O diff `--stat` amplo reflete trabalho pré-existente na working tree, não desta tarefa. Recomenda-se confirmar com o time que as mudanças pré-existentes (Visão Estudante API, Bolsas API) são intencionais antes do próximo commit.
- Validação de percentual no modo PERCENTUAL é feita implicitamente pelo `<select>` (só valores {5..50}); o backend valida também. Não há risco de bypass via input livre.
- Nenhum commit realizado (instrução permanente respeitada).

## Final-review fix wave

### O que foi removido/revertido em `financeiro.tsx`

Dois helpers removidos de `FinanceiroView` (eram dead code pois `contestacoes-abertas` retorna sempre PENDENTE):
- `const rotuloStatusContestacao: Record<string, string> = { PENDENTE: "Em análise", DEFERIDA: "Deferida", INDEFERIDA: "Indeferida" };`
- `const tomStatusContestacao = (s: string | null | undefined) => (s === "DEFERIDA" ? "success" : s === "INDEFERIDA" ? "danger" : "info");`

Coluna `status` na aba `contestacoes` revertida de render dinâmico para badge estático:
```tsx
{ key: "status", header: "Status", render: () => <StatusBadge tone="info">Em análise</StatusBadge> },
```

Mantidos integralmente: estado de resolução (`resolverId`, `decisao`, `modoAjuste`, `valorAjuste`, `parecerResol`, `cobrancaResolver`, `fecharResolver`), hooks `deferir`/`indeferir`, painel inline, toasts, validações — tudo que aciona a lógica de deferir/indeferir permanece inalterado.

### Build result

```
✓ built in 2.59s   (client)
✓ built in 1.59s   (ssr)
```
Zero erros de tipo. Build limpo.

### Grep confirmation

```
helpers gone / only static badge remains
```
`tomStatusContestacao` e `rotuloStatusContestacao` não aparecem mais no arquivo. O literal "Em análise" persiste apenas no badge estático (comportamento esperado).
