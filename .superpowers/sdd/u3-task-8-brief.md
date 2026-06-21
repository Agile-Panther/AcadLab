### Task 8: Frontend — estender `src/lib/financeiro.ts` (tipos + hooks de bolsa)

**Files:**
- Modify: `apresentacao-frontend/react/src/lib/financeiro.ts`
- Modify: `apresentacao-frontend/react/src/lib/format.ts`

**Interfaces:**
- Produces (consumido Task 9): tipos `TipoBolsa`, `StatusBolsa`, `BolsaResumo`; hooks `useBolsas`, `useConcederBolsa`, `useSuspenderBolsa`, `useReativarBolsa`, `useRenovarBolsa`; helper `formatValidade`.

- [ ] **Step 1: Adicionar `formatValidade` em `format.ts`**

```ts
/** Converte data ISO (YYYY-MM-DD) para "MM/AAAA". Retorna "—" se ausente. */
export function formatValidade(iso: string | null | undefined): string {
  if (!iso) return "—";
  const [ano, mes] = iso.split("-");
  if (!ano || !mes) return iso;
  return `${mes}/${ano}`;
}
```

- [ ] **Step 2: Adicionar tipos e hooks de bolsa em `financeiro.ts`**

No final de `financeiro.ts` (reusa `api`, `hojeIso`, `useInvalidate` já presentes; `useMutation`/`useQuery`/`useQueryClient` já importados):

```ts
/* ===== Bolsas (sub-projeto A) ===== */

export type TipoBolsa = "PROUNI" | "FIES" | "MERITO" | "CONVENIO";
export type StatusBolsa = "ATIVA" | "SUSPENSA" | "EM_RENOVACAO";

export type BolsaResumo = {
  id: number;
  estudanteId: number;
  tipo: TipoBolsa;
  percentual: number;
  validade: string | null;
  status: StatusBolsa;
};

export function useBolsas() {
  return useQuery({
    queryKey: ["financeiro", "bolsas"] as const,
    queryFn: () => api.get<BolsaResumo[]>("bolsas"),
  });
}

export function useConcederBolsa() {
  const invalidate = useInvalidate();
  return useMutation({
    mutationFn: (vars: { estudanteId: number; tipo: TipoBolsa; percentual: number; validade: string }) =>
      api.post("bolsas/conceder", vars),
    onSuccess: invalidate,
  });
}

export function useSuspenderBolsa() {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/suspender`), onSuccess: invalidate });
}

export function useReativarBolsa() {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/reativar`), onSuccess: invalidate });
}

export function useRenovarBolsa() {
  const invalidate = useInvalidate();
  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/renovar`), onSuccess: invalidate });
}
```

> `useInvalidate` é a função-fábrica já definida no arquivo (invalida `["financeiro"]`). Se estiver declarada após este ponto, mover este bloco para depois dela, ou reutilizá-la sem redefinir.

- [ ] **Step 3: Verificar build do front**

Run (em `apresentacao-frontend/react`): `npm run build`
Expected: build sem erros de tipo.

---

