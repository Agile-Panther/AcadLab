### Task 6: Frontend — religar a aba "Contestações" em `financeiro.tsx`

**Files:**
- Modify: `apresentacao-frontend/react/src/routes/financeiro.tsx` (import de `@/lib/financeiro`; bloco `tab === "contestacoes"`, linhas ~284-305; e a `FinanceiroView` para estado + painel de resolução)

**Interfaces:**
- Consumes: `useDeferirContestacao`, `useIndeferirContestacao`, `type ModoAjuste` (Task 5); `formatMoeda`/`formatData` já importados; componentes `DataTable`/`StatusBadge`/`RowActionButton`/`FormField`/`Input`/`Button`/`toast` já no arquivo.

**Regra:** só a aba `tab === "contestacoes"` muda; demais abas e a Visão Estudante intocadas. Reusa o padrão de painel inline da aba Bolsas (`novaBolsaOpen && <div className="rounded-xl border bg-card p-5 shadow-card">…</div>`).

- [ ] **Step 1: Imports e hooks na `FinanceiroView`**

No import de `@/lib/financeiro`, trocar `useResolverContestacao` por:
```ts
  useDeferirContestacao, useIndeferirContestacao, type ModoAjuste,
```

Dentro de `FinanceiroView`, substituir `const resolver = useResolverContestacao();` por:
```tsx
const deferir = useDeferirContestacao();
const indeferir = useIndeferirContestacao();
const [resolverId, setResolverId] = useState<number | null>(null);
const [decisao, setDecisao] = useState<"DEFERIR" | "INDEFERIR">("DEFERIR");
const [modoAjuste, setModoAjuste] = useState<ModoAjuste>("PERCENTUAL");
const [valorAjuste, setValorAjuste] = useState("");
const [parecerResol, setParecerResol] = useState("");
const cobrancaResolver = contestacoesAbertas.find((c) => c.id === resolverId) ?? null;
const fecharResolver = () => { setResolverId(null); setValorAjuste(""); setParecerResol(""); setDecisao("DEFERIR"); setModoAjuste("PERCENTUAL"); };
const rotuloStatusContestacao: Record<string, string> = { PENDENTE: "Em análise", DEFERIDA: "Deferida", INDEFERIDA: "Indeferida" };
const tomStatusContestacao = (s: string | null | undefined) => (s === "DEFERIDA" ? "success" : s === "INDEFERIDA" ? "danger" : "info");
```

- [ ] **Step 2: Substituir a coluna de ações e o status da tabela de contestações**

No bloco `tab === "contestacoes"`, trocar a coluna `status` e a coluna `acoes` (linhas ~294-300) por:
```tsx
              { key: "status", header: "Status", render: (r) => (
                <StatusBadge tone={tomStatusContestacao(r.contestacao?.status)}>
                  {rotuloStatusContestacao[r.contestacao?.status ?? "PENDENTE"] ?? "Em análise"}
                </StatusBadge>
              )},
              { key: "acoes", header: "", align: "right", render: (r) => (
                <div className="flex justify-end gap-2">
                  <RowActionButton tone="info" onClick={() => { setResolverId(r.id); }}>Resolver</RowActionButton>
                </div>
              )},
```

- [ ] **Step 3: Adicionar o painel de resolução logo após a `DataTable` de contestações**

Dentro do bloco `tab === "contestacoes"`, após o `<DataTable ... rows={contestacoesAbertas} />` (e antes do fechamento `</>`), inserir:
```tsx
          {cobrancaResolver && (
            <div className="rounded-xl border bg-card p-5 shadow-card">
              <SectionTitle title={`Resolver contestação COB-${cobrancaResolver.id}`}
                subtitle={`Valor atual ${formatMoeda(cobrancaResolver.valorAtual)} • Estudante #${cobrancaResolver.estudanteId}`} />
              <div className="mt-3 flex flex-col gap-3">
                <div className="flex gap-2">
                  <Button variant={decisao === "DEFERIR" ? "default" : "outline"} onClick={() => setDecisao("DEFERIR")}>Deferir</Button>
                  <Button variant={decisao === "INDEFERIR" ? "default" : "outline"} onClick={() => setDecisao("INDEFERIR")}>Indeferir</Button>
                </div>
                {decisao === "DEFERIR" && (
                  <div className="grid grid-cols-2 gap-3">
                    <FormField label="Modo">
                      <select className="h-9 w-full rounded-md border bg-background px-3 text-sm"
                        value={modoAjuste} onChange={(e) => setModoAjuste(e.target.value as ModoAjuste)}>
                        <option value="PERCENTUAL">Percentual (%)</option>
                        <option value="VALOR">Valor (R$)</option>
                      </select>
                    </FormField>
                    <FormField label={modoAjuste === "PERCENTUAL" ? "Desconto (%)" : "Novo valor (R$)"} required>
                      {modoAjuste === "PERCENTUAL" ? (
                        <select className="h-9 w-full rounded-md border bg-background px-3 text-sm"
                          value={valorAjuste} onChange={(e) => setValorAjuste(e.target.value)}>
                          <option value="">Selecione…</option>
                          {[5, 10, 15, 20, 25, 30, 35, 40, 45, 50].map((p) => (
                            <option key={p} value={p}>{p}%</option>
                          ))}
                        </select>
                      ) : (
                        <Input type="number" placeholder={`entre ${formatMoeda(cobrancaResolver.valorAtual * 0.5)} e ${formatMoeda(cobrancaResolver.valorAtual)}`}
                          value={valorAjuste} onChange={(e) => setValorAjuste(e.target.value)} />
                      )}
                    </FormField>
                  </div>
                )}
                <FormField label="Parecer" required>
                  <Input placeholder="Justificativa da decisão" value={parecerResol} onChange={(e) => setParecerResol(e.target.value)} />
                </FormField>
              </div>
              <div className="mt-4 flex justify-end gap-2">
                <Button variant="outline" onClick={fecharResolver}>Cancelar</Button>
                <Button onClick={() => {
                  if (!parecerResol.trim()) { toast.error("Informe o parecer."); return; }
                  if (decisao === "INDEFERIR") {
                    indeferir.mutate({ id: cobrancaResolver.id, parecer: parecerResol },
                      { onSuccess: () => { fecharResolver(); toast.success(`Contestação COB-${cobrancaResolver.id} indeferida.`); } });
                    return;
                  }
                  const v = Number(valorAjuste);
                  if (!valorAjuste || Number.isNaN(v) || v <= 0) { toast.error("Informe o valor do deferimento."); return; }
                  if (modoAjuste === "VALOR" && (v >= cobrancaResolver.valorAtual || v < cobrancaResolver.valorAtual * 0.5)) {
                    toast.error("Valor deve reduzir no máximo 50% e ser menor que o atual."); return;
                  }
                  deferir.mutate({ id: cobrancaResolver.id, modo: modoAjuste, valor: v, parecer: parecerResol },
                    { onSuccess: () => { fecharResolver(); toast.success(`Contestação COB-${cobrancaResolver.id} deferida.`); } });
                }}>Confirmar</Button>
              </div>
            </div>
          )}
```

> `SectionTitle`, `FormField`, `Input`, `Button`, `StatusBadge`, `RowActionButton`, `toast`, `formatMoeda`, `useState` já estão importados/usados no arquivo (a aba Bolsas usa o mesmo conjunto). O `<select>` usa as mesmas classes utilitárias do projeto; se já existir um componente `Select` no design system, preferir reutilizá-lo mantendo as opções.

- [ ] **Step 4: Verificar build do front**

Run (em `apresentacao-frontend/react`): `npm run build`
Expected: build sem erros de tipo.

- [ ] **Step 5: Verificação visual de regressão (diff)**

Run: `git diff --stat -- src/routes/financeiro.tsx`
Inspecionar: só a aba contestações + o estado/painel de resolução na `FinanceiroView` mudaram; nenhuma classe Tailwind/estrutura das demais abas (Estudante/Bolsas/Inadimplência/Conciliação/Relatórios) alterada. Reverter qualquer mudança estrutural acidental.

---

## Self-Review

**1. Spec coverage:**
- §3.1 StatusContestacao → Task 1 ✓
- §3.2 ModoAjuste → Task 1 ✓
- §3.3 Contestacao deferir/indeferir → Task 1 ✓
- §3.4 Cobranca deferir/indeferir + ajuste + auditoria → Task 1 ✓
- §4 CobrancaServico → Task 2 ✓
- §5 infra reconstituição → Task 3 ✓
- §6 controlador endpoints → Task 4 ✓
- §7.1 financeiro.ts tipos+hooks → Task 5 ✓
- §7.2 financeiro.tsx aba → Task 6 ✓
- §8 BDD+unit → Tasks 1 (CobrancaTest), 2 (feature+steps) ✓
- §9 seed V6 → Task 3 ✓
- §10 critérios → gates por tarefa ✓

**2. Placeholder scan:** sem TBD/TODO; código completo em cada step.

**3. Type consistency:** `ModoAjuste{PERCENTUAL,VALOR}` consistente Java (Task 1/2/4) ↔ TS (Task 5/6). `StatusContestacao{PENDENTE,DEFERIDA,INDEFERIDA}` consistente domínio (Task 1) ↔ infra (Task 3) ↔ TS (Task 5). Endpoints `deferir-contestacao {modo,valor,parecer}` / `indeferir-contestacao {parecer}` (Task 4) batem com os hooks (Task 5) e o painel (Task 6). `deferirContestacao(ModoAjuste,BigDecimal,String)`/`indeferirContestacao(String)` idênticos entre agregado (Task 1), serviço (Task 2) e controlador (Task 4).

**Notas de execução:** sem commits; teste ao vivo é do usuário; eventos seguem o padrão de `Cobranca` (um `ContestacaoResolvidaEvento` para ambos os caminhos).
