### Task 5: Frontend — tipos + hooks em `financeiro.ts`

**Files:**
- Modify: `apresentacao-frontend/react/src/lib/financeiro.ts:16-22` (tipo `ContestacaoResumo.status`) e `:100-107` (substituir `useResolverContestacao`)

**Interfaces:**
- Produces (consumido Task 6): `type ModoAjuste`; `useDeferirContestacao()`; `useIndeferirContestacao()`; `ContestacaoResumo.status` com DEFERIDA/INDEFERIDA.

- [ ] **Step 1: Atualizar o tipo `ContestacaoResumo.status`**

Em `financeiro.ts`, alterar a linha 20:
```ts
  status: "PENDENTE" | "DEFERIDA" | "INDEFERIDA" | null;
```

- [ ] **Step 2: Adicionar `ModoAjuste` e substituir o hook de resolução**

Substituir `useResolverContestacao` (linhas 100-107) por:
```ts
export type ModoAjuste = "PERCENTUAL" | "VALOR";

export function useDeferirContestacao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; modo: ModoAjuste; valor: number; parecer: string }) =>
      api.post(`cobrancas/${vars.id}/deferir-contestacao`, {
        modo: vars.modo,
        valor: vars.valor,
        parecer: vars.parecer,
      }),
    onSuccess: invalidate,
  });
}

export function useIndeferirContestacao() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { id: number; parecer: string }) =>
      api.post(`cobrancas/${vars.id}/indeferir-contestacao`, { parecer: vars.parecer }),
    onSuccess: invalidate,
  });
}
```

- [ ] **Step 3: Verificar build do front**

Run (em `apresentacao-frontend/react`): `npm run build`
Expected: build sem erros de tipo (a aba Contestações ainda usa `useResolverContestacao` — este step pode falhar a compilação do TSX até a Task 6; se executar isolado, prossiga para a Task 6 e rode o build no fim dela).

---

