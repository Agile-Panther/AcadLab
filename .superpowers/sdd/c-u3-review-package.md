# U3 Review Package — Contestação: frontend (Tasks 5-6)

No commits. Each file diffed vs its PRE-feature baseline → only THIS feature's changes show (Sub-0/Sub-A changes are in the baseline and excluded).

## === financeiro.ts (type + hooks) ===
diff --git a/.superpowers/sdd/baseline/contestacao/financeiro.ts.base b/apresentacao-frontend/react/src/lib/financeiro.ts
index e727705..66d00f5 100644
--- a/.superpowers/sdd/baseline/contestacao/financeiro.ts.base
+++ b/apresentacao-frontend/react/src/lib/financeiro.ts
@@ -17,7 +17,7 @@ export type ContestacaoResumo = {
   requerenteId: number | null;
   justificativa: string | null;
   data: string | null;
-  status: "PENDENTE" | "RESOLVIDA" | null;
+  status: "PENDENTE" | "DEFERIDA" | "INDEFERIDA" | null;
   parecer: string | null;
 };
 
@@ -97,11 +97,26 @@ export function useContestar() {
   });
 }
 
-export function useResolverContestacao() {
+export type ModoAjuste = "PERCENTUAL" | "VALOR";
+
+export function useDeferirContestacao() {
+  const invalidate = useInvalidate();
+  return useMutation({
+    mutationFn: (vars: { id: number; modo: ModoAjuste; valor: number; parecer: string }) =>
+      api.post(`cobrancas/${vars.id}/deferir-contestacao`, {
+        modo: vars.modo,
+        valor: vars.valor,
+        parecer: vars.parecer,
+      }),
+    onSuccess: invalidate,
+  });
+}
+
+export function useIndeferirContestacao() {
   const invalidate = useInvalidate();
   return useMutation({
     mutationFn: (vars: { id: number; parecer: string }) =>
-      api.post(`cobrancas/${vars.id}/resolver-contestacao`, vars.parecer),
+      api.post(`cobrancas/${vars.id}/indeferir-contestacao`, { parecer: vars.parecer }),
     onSuccess: invalidate,
   });
 }

## === financeiro.tsx (Contestações tab + resolution panel — MUST be tab-scoped only) ===
diff --git a/.superpowers/sdd/baseline/contestacao/financeiro.tsx.base b/apresentacao-frontend/react/src/routes/financeiro.tsx
index 2519900..2653c39 100644
--- a/.superpowers/sdd/baseline/contestacao/financeiro.tsx.base
+++ b/apresentacao-frontend/react/src/routes/financeiro.tsx
@@ -12,7 +12,7 @@ import { ArrowLeft, Download, CreditCard, AlertCircle, Send, FileText, Plus } fr
 import { toast } from "sonner";
 import {
   useExtrato, useRegistrarPagamento, useContestar,
-  useContestacoesAbertas, useResolverContestacao,
+  useContestacoesAbertas, useDeferirContestacao, useIndeferirContestacao, type ModoAjuste,
   useBolsas, useConcederBolsa, useSuspenderBolsa, useReativarBolsa, useRenovarBolsa,
   type CobrancaResumo, type StatusCobranca, type BolsaResumo, type TipoBolsa, type StatusBolsa,
 } from "@/lib/financeiro";
@@ -209,7 +209,17 @@ function FinanceiroView() {
 
   const contestacoesQuery = useContestacoesAbertas();
   const contestacoesAbertas = contestacoesQuery.data ?? [];
-  const resolver = useResolverContestacao();
+  const deferir = useDeferirContestacao();
+  const indeferir = useIndeferirContestacao();
+  const [resolverId, setResolverId] = useState<number | null>(null);
+  const [decisao, setDecisao] = useState<"DEFERIR" | "INDEFERIR">("DEFERIR");
+  const [modoAjuste, setModoAjuste] = useState<ModoAjuste>("PERCENTUAL");
+  const [valorAjuste, setValorAjuste] = useState("");
+  const [parecerResol, setParecerResol] = useState("");
+  const cobrancaResolver = contestacoesAbertas.find((c) => c.id === resolverId) ?? null;
+  const fecharResolver = () => { setResolverId(null); setValorAjuste(""); setParecerResol(""); setDecisao("DEFERIR"); setModoAjuste("PERCENTUAL"); };
+  const rotuloStatusContestacao: Record<string, string> = { PENDENTE: "Em análise", DEFERIDA: "Deferida", INDEFERIDA: "Indeferida" };
+  const tomStatusContestacao = (s: string | null | undefined) => (s === "DEFERIDA" ? "success" : s === "INDEFERIDA" ? "danger" : "info");
 
   const [inadimp, setInadimp] = useState<Inadimplente[]>([
     { matricula: "2021.0451", aluno: "Lucas Pereira", curso: "Eng. Civil", emAtraso: "R$ 2.840,00", diasAtraso: 47, status: "Notificado" },
@@ -291,16 +301,77 @@ function FinanceiroView() {
               { key: "cobranca", header: "Cobrança", render: (r) => `Mensalidade • venc. ${formatData(r.vencimento)}` },
               { key: "valor", header: "Valor", align: "right", render: (r) => formatMoeda(r.valorAtual) },
               { key: "motivo", header: "Motivo", render: (r) => r.contestacao?.justificativa ?? "—" },
-              { key: "status", header: "Status", render: () => <StatusBadge tone="info">Em análise</StatusBadge> },
+              { key: "status", header: "Status", render: (r) => (
+                <StatusBadge tone={tomStatusContestacao(r.contestacao?.status)}>
+                  {rotuloStatusContestacao[r.contestacao?.status ?? "PENDENTE"] ?? "Em análise"}
+                </StatusBadge>
+              )},
               { key: "acoes", header: "", align: "right", render: (r) => (
                 <div className="flex justify-end gap-2">
-                  <RowActionButton onClick={() => resolver.mutate({ id: r.id, parecer: "Indeferida pelo Setor Financeiro." }, { onSuccess: () => toast.success(`Contestação COB-${r.id} indeferida.`) })}>Indeferir</RowActionButton>
-                  <RowActionButton tone="info" onClick={() => resolver.mutate({ id: r.id, parecer: "Deferida pelo Setor Financeiro." }, { onSuccess: () => toast.success(`Contestação COB-${r.id} deferida.`) })}>Deferir</RowActionButton>
+                  <RowActionButton tone="info" onClick={() => { setResolverId(r.id); }}>Resolver</RowActionButton>
                 </div>
               )},
             ]}
             rows={contestacoesAbertas}
           />
+          {cobrancaResolver && (
+            <div className="rounded-xl border bg-card p-5 shadow-card">
+              <SectionTitle title={`Resolver contestação COB-${cobrancaResolver.id}`}
+                subtitle={`Valor atual ${formatMoeda(cobrancaResolver.valorAtual)} • Estudante #${cobrancaResolver.estudanteId}`} />
+              <div className="mt-3 flex flex-col gap-3">
+                <div className="flex gap-2">
+                  <Button variant={decisao === "DEFERIR" ? "default" : "outline"} onClick={() => setDecisao("DEFERIR")}>Deferir</Button>
+                  <Button variant={decisao === "INDEFERIR" ? "default" : "outline"} onClick={() => setDecisao("INDEFERIR")}>Indeferir</Button>
+                </div>
+                {decisao === "DEFERIR" && (
+                  <div className="grid grid-cols-2 gap-3">
+                    <FormField label="Modo">
+                      <select className="h-9 w-full rounded-md border bg-background px-3 text-sm"
+                        value={modoAjuste} onChange={(e) => setModoAjuste(e.target.value as ModoAjuste)}>
+                        <option value="PERCENTUAL">Percentual (%)</option>
+                        <option value="VALOR">Valor (R$)</option>
+                      </select>
+                    </FormField>
+                    <FormField label={modoAjuste === "PERCENTUAL" ? "Desconto (%)" : "Novo valor (R$)"} required>
+                      {modoAjuste === "PERCENTUAL" ? (
+                        <select className="h-9 w-full rounded-md border bg-background px-3 text-sm"
+                          value={valorAjuste} onChange={(e) => setValorAjuste(e.target.value)}>
+                          <option value="">Selecione…</option>
+                          {[5, 10, 15, 20, 25, 30, 35, 40, 45, 50].map((p) => (
+                            <option key={p} value={p}>{p}%</option>
+                          ))}
+                        </select>
+                      ) : (
+                        <Input type="number" placeholder={`entre ${formatMoeda(cobrancaResolver.valorAtual * 0.5)} e ${formatMoeda(cobrancaResolver.valorAtual)}`}
+                          value={valorAjuste} onChange={(e) => setValorAjuste(e.target.value)} />
+                      )}
+                    </FormField>
+                  </div>
+                )}
+                <FormField label="Parecer" required>
+                  <Input placeholder="Justificativa da decisão" value={parecerResol} onChange={(e) => setParecerResol(e.target.value)} />
+                </FormField>
+              </div>
+              <div className="mt-4 flex justify-end gap-2">
+                <Button variant="outline" onClick={fecharResolver}>Cancelar</Button>
+                <Button onClick={() => {
+                  if (!parecerResol.trim()) { toast.error("Informe o parecer."); return; }
+                  if (decisao === "INDEFERIR") {
+                    indeferir.mutate({ id: cobrancaResolver.id, parecer: parecerResol },
+                      { onSuccess: () => { fecharResolver(); toast.success(`Contestação COB-${cobrancaResolver.id} indeferida.`); } });
+                    return;
+                  }
+                  const v = Number(valorAjuste);
+                  if (!valorAjuste || Number.isNaN(v) || v <= 0) { toast.error("Informe o valor do deferimento."); return; }
+                  if (modoAjuste === "VALOR" && (v >= cobrancaResolver.valorAtual || v < cobrancaResolver.valorAtual * 0.5)) {
+                    toast.error("Valor deve reduzir no máximo 50% e ser menor que o atual."); return;
+                  }
+                  deferir.mutate({ id: cobrancaResolver.id, modo: modoAjuste, valor: v, parecer: parecerResol },
+                    { onSuccess: () => { fecharResolver(); toast.success(`Contestação COB-${cobrancaResolver.id} deferida.`); } });
+                }}>Confirmar</Button>
+              </div>
+            </div>
+          )}
         </>
       )}
 
