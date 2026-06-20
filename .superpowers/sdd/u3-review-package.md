# U3 Review Package — F-13 Sub-A frontend (Tasks 8-9)

No commits. Each file diffed against its PRE-Sub-A baseline snapshot, so ONLY U3's changes show (Sub-0 changes are in the baseline and excluded).

## === format.ts (formatValidade added) ===
diff --git a/.superpowers/sdd/baseline/subA/format.ts.base b/apresentacao-frontend/react/src/lib/format.ts
index 32e5350..5727f4b 100644
--- a/.superpowers/sdd/baseline/subA/format.ts.base
+++ b/apresentacao-frontend/react/src/lib/format.ts
@@ -21,6 +21,14 @@ export function agoraParaInput(): string {
   return local.toISOString().slice(0, 16);
 }
 
+/** Converte data ISO (YYYY-MM-DD) para "MM/AAAA". Retorna "—" se ausente. */
+export function formatValidade(iso: string | null | undefined): string {
+  if (!iso) return "—";
+  const [ano, mes] = iso.split("-");
+  if (!ano || !mes) return iso;
+  return `${mes}/${ano}`;
+}
+
 /** Formata número/decimal do backend como moeda BRL (ex.: 1420 → "R$ 1.420,00"). */
 export function formatMoeda(valor: number | string | null | undefined): string {
   if (valor === null || valor === undefined) return "—";

## === financeiro.ts (bolsa types + 5 hooks appended) ===
diff --git a/.superpowers/sdd/baseline/subA/financeiro.ts.base b/apresentacao-frontend/react/src/lib/financeiro.ts
index 036e62b..e727705 100644
--- a/.superpowers/sdd/baseline/subA/financeiro.ts.base
+++ b/apresentacao-frontend/react/src/lib/financeiro.ts
@@ -105,3 +105,48 @@ export function useResolverContestacao() {
     onSuccess: invalidate,
   });
 }
+
+/* ===== Bolsas (sub-projeto A) ===== */
+
+export type TipoBolsa = "PROUNI" | "FIES" | "MERITO" | "CONVENIO";
+export type StatusBolsa = "ATIVA" | "SUSPENSA" | "EM_RENOVACAO";
+
+export type BolsaResumo = {
+  id: number;
+  estudanteId: number;
+  tipo: TipoBolsa;
+  percentual: number;
+  validade: string | null;
+  status: StatusBolsa;
+};
+
+export function useBolsas() {
+  return useQuery({
+    queryKey: ["financeiro", "bolsas"] as const,
+    queryFn: () => api.get<BolsaResumo[]>("bolsas"),
+  });
+}
+
+export function useConcederBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({
+    mutationFn: (vars: { estudanteId: number; tipo: TipoBolsa; percentual: number; validade: string }) =>
+      api.post("bolsas/conceder", vars),
+    onSuccess: invalidate,
+  });
+}
+
+export function useSuspenderBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/suspender`), onSuccess: invalidate });
+}
+
+export function useReativarBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/reativar`), onSuccess: invalidate });
+}
+
+export function useRenovarBolsa() {
+  const invalidate = useInvalidate();
+  return useMutation({ mutationFn: (id: number) => api.post(`bolsas/${id}/renovar`), onSuccess: invalidate });
+}

## === financeiro.tsx (bolsas tab rewired — MUST be data/types/handlers only) ===
diff --git a/.superpowers/sdd/baseline/subA/financeiro.tsx.base b/apresentacao-frontend/react/src/routes/financeiro.tsx
index 7df7be2..f7bf261 100644
--- a/.superpowers/sdd/baseline/subA/financeiro.tsx.base
+++ b/apresentacao-frontend/react/src/routes/financeiro.tsx
@@ -13,9 +13,10 @@ import { toast } from "sonner";
 import {
   useExtrato, useRegistrarPagamento, useContestar,
   useContestacoesAbertas, useResolverContestacao,
-  type CobrancaResumo, type StatusCobranca,
+  useBolsas, useConcederBolsa, useSuspenderBolsa, useReativarBolsa, useRenovarBolsa,
+  type CobrancaResumo, type StatusCobranca, type BolsaResumo, type TipoBolsa, type StatusBolsa,
 } from "@/lib/financeiro";
-import { formatData, formatMoeda } from "@/lib/format";
+import { formatData, formatMoeda, formatValidade } from "@/lib/format";
 import { hojeIso } from "@/lib/api";
 import { USUARIO_ATUAL } from "@/lib/config";
 
@@ -201,7 +202,6 @@ function Page() {
 }
 
 type Inadimplente = { matricula: string; aluno: string; curso: string; emAtraso: string; diasAtraso: number; status: "Notificado" | "Negociar" | "Bloqueado" };
-type Bolsa = { id: string; aluno: string; tipo: "ProUni" | "FIES" | "Mérito" | "Convênio"; percentual: number; validade: string; status: "Ativa" | "Em renovação" | "Suspensa" };
 type Lancamento = { id: string; data: string; descricao: string; metodo: "PIX" | "Boleto" | "Cartão"; valor: string; status: "Conciliado" | "Pendente" | "Divergente" };
 
 function FinanceiroView() {
@@ -217,12 +217,14 @@ function FinanceiroView() {
     { matricula: "2020.0712", aluno: "Rafael Lima", curso: "Medicina", emAtraso: "R$ 9.120,00", diasAtraso: 121, status: "Bloqueado" },
     { matricula: "2023.0034", aluno: "Beatriz Souza", curso: "Psicologia", emAtraso: "R$ 1.420,00", diasAtraso: 18, status: "Notificado" },
   ]);
-  const [bolsas, setBolsas] = useState<Bolsa[]>([
-    { id: "BL-441", aluno: "Maria Santos", tipo: "Mérito", percentual: 50, validade: "12/2025", status: "Ativa" },
-    { id: "BL-318", aluno: "João Oliveira", tipo: "ProUni", percentual: 100, validade: "12/2026", status: "Ativa" },
-    { id: "BL-205", aluno: "Ana Costa", tipo: "FIES", percentual: 75, validade: "06/2025", status: "Em renovação" },
-    { id: "BL-099", aluno: "Bruno Dias", tipo: "Convênio", percentual: 20, validade: "12/2024", status: "Suspensa" },
-  ]);
+  const bolsasQuery = useBolsas();
+  const bolsas = bolsasQuery.data ?? [];
+  const concederBolsa = useConcederBolsa();
+  const suspenderBolsa = useSuspenderBolsa();
+  const reativarBolsa = useReativarBolsa();
+  const renovarBolsa = useRenovarBolsa();
+  const rotuloStatusBolsa: Record<StatusBolsa, string> = { ATIVA: "Ativa", EM_RENOVACAO: "Em renovação", SUSPENSA: "Suspensa" };
+  const tomStatusBolsa = (s: StatusBolsa) => (s === "ATIVA" ? "success" : s === "SUSPENSA" ? "danger" : "warning");
   const [lancs, setLancs] = useState<Lancamento[]>([
     { id: "LC-9821", data: "18/03/2025", descricao: "PIX recebido — 2021.0188", metodo: "PIX", valor: "R$ 1.420,00", status: "Conciliado" },
     { id: "LC-9820", data: "18/03/2025", descricao: "Boleto compensado — 2022.0345", metodo: "Boleto", valor: "R$ 1.420,00", status: "Conciliado" },
@@ -231,6 +233,10 @@ function FinanceiroView() {
   ]);
   const [filtroInad, setFiltroInad] = useState("");
   const [novaBolsaOpen, setNovaBolsaOpen] = useState(false);
+  const [novaMatricula, setNovaMatricula] = useState("");
+  const [novoTipo, setNovoTipo] = useState<TipoBolsa>("MERITO");
+  const [novoPercentual, setNovoPercentual] = useState("");
+  const [novaValidade, setNovaValidade] = useState("");
 
   const notificar = (m: string) => {
     setInadimp((p) => p.map((i) => i.matricula === m ? { ...i, status: "Notificado" } : i));
@@ -248,14 +254,6 @@ function FinanceiroView() {
     setLancs((p) => p.map((l) => l.id === id ? { ...l, status: "Conciliado" } : l));
     toast.success(`Lançamento ${id} conciliado.`);
   };
-  const suspenderBolsa = (id: string) => {
-    setBolsas((p) => p.map((b) => b.id === id ? { ...b, status: "Suspensa" } : b));
-    toast.warning(`Bolsa ${id} suspensa.`);
-  };
-  const reativarBolsa = (id: string) => {
-    setBolsas((p) => p.map((b) => b.id === id ? { ...b, status: "Ativa" } : b));
-    toast.success(`Bolsa ${id} reativada.`);
-  };
 
   const inadimpFiltrada = useMemo(
     () => inadimp.filter((i) => (i.aluno + i.matricula + i.curso).toLowerCase().includes(filtroInad.toLowerCase())),
@@ -268,7 +266,7 @@ function FinanceiroView() {
         { label: "Arrecadação no mês", value: "R$ 1,82M", tone: "success" },
         { label: "Inadimplência", value: `${inadimp.length} alunos`, tone: "warning" },
         { label: "Contestações abertas", value: contestacoesAbertas.length, tone: "info" },
-        { label: "Bolsas ativas", value: bolsas.filter((b) => b.status === "Ativa").length, tone: "info" },
+        { label: "Bolsas ativas", value: bolsas.filter((b) => b.status === "ATIVA").length, tone: "info" },
       ]} />
 
       <TabsRow
@@ -361,38 +359,36 @@ function FinanceiroView() {
             <div className="rounded-xl border bg-card p-5 shadow-card">
               <SectionTitle title="Conceder bolsa" subtitle="Preencha os dados da nova concessão." />
               <div className="mt-3 grid grid-cols-2 gap-3">
-                <FormField label="Matrícula" required><Input placeholder="2024.XXXX" /></FormField>
-                <FormField label="Tipo" required><Input placeholder="Mérito, ProUni, FIES…" /></FormField>
-                <FormField label="Percentual (%)" required><Input type="number" placeholder="0–100" /></FormField>
-                <FormField label="Validade" required><Input placeholder="MM/AAAA" /></FormField>
+                <FormField label="Matrícula" required><Input placeholder="2024.XXXX" value={novaMatricula} onChange={(e) => setNovaMatricula(e.target.value)} /></FormField>
+                <FormField label="Tipo" required><Input placeholder="MERITO, PROUNI, FIES, CONVENIO" value={novoTipo} onChange={(e) => setNovoTipo(e.target.value as TipoBolsa)} /></FormField>
+                <FormField label="Percentual (%)" required><Input type="number" placeholder="0–100" value={novoPercentual} onChange={(e) => setNovoPercentual(e.target.value)} /></FormField>
+                <FormField label="Validade" required><Input type="date" value={novaValidade} onChange={(e) => setNovaValidade(e.target.value)} /></FormField>
               </div>
               <div className="mt-4 flex justify-end gap-2">
                 <Button variant="outline" onClick={() => setNovaBolsaOpen(false)}>Cancelar</Button>
-                <Button onClick={() => {
-                  const id = `BL-${Math.floor(Math.random() * 900 + 100)}`;
-                  setBolsas((p) => [{ id, aluno: "Novo Beneficiário", tipo: "Mérito", percentual: 25, validade: "12/2025", status: "Ativa" }, ...p]);
-                  setNovaBolsaOpen(false);
-                  toast.success(`Bolsa ${id} concedida.`);
-                }}>Conceder</Button>
+                <Button onClick={() => concederBolsa.mutate(
+                  { estudanteId: Number(novaMatricula), tipo: novoTipo, percentual: Number(novoPercentual), validade: novaValidade },
+                  { onSuccess: () => { setNovaBolsaOpen(false); toast.success("Bolsa concedida."); } },
+                )}>Conceder</Button>
               </div>
             </div>
           )}
           <DataTable
             columns={[
-              { key: "id", header: "Código" },
-              { key: "aluno", header: "Beneficiário" },
+              { key: "id", header: "Código", render: (r) => `BL-${r.id}` },
+              { key: "estudante", header: "Beneficiário", render: (r) => `Estudante #${r.estudanteId}` },
               { key: "tipo", header: "Tipo" },
               { key: "percentual", header: "%", align: "right", render: (r) => `${r.percentual}%` },
-              { key: "validade", header: "Validade" },
+              { key: "validade", header: "Validade", render: (r) => formatValidade(r.validade) },
               { key: "status", header: "Status", render: (r) => (
-                <StatusBadge tone={r.status === "Ativa" ? "success" : r.status === "Suspensa" ? "danger" : "warning"}>{r.status}</StatusBadge>
+                <StatusBadge tone={tomStatusBolsa(r.status)}>{rotuloStatusBolsa[r.status]}</StatusBadge>
               )},
               { key: "acoes", header: "", align: "right", render: (r) => (
                 <div className="flex justify-end gap-2">
-                  {r.status === "Ativa"
-                    ? <RowActionButton tone="danger" onClick={() => suspenderBolsa(r.id)}>Suspender</RowActionButton>
-                    : <RowActionButton tone="info" onClick={() => reativarBolsa(r.id)}>Reativar</RowActionButton>}
-                  <RowActionButton onClick={() => toast.success(`Renovação de ${r.id} iniciada.`)}>Renovar</RowActionButton>
+                  {r.status === "SUSPENSA"
+                    ? <RowActionButton tone="info" onClick={() => reativarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Bolsa BL-${r.id} reativada.`) })}>Reativar</RowActionButton>
+                    : <RowActionButton tone="danger" onClick={() => suspenderBolsa.mutate(r.id, { onSuccess: () => toast.warning(`Bolsa BL-${r.id} suspensa.`) })}>Suspender</RowActionButton>}
+                  <RowActionButton onClick={() => renovarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Renovação de BL-${r.id} iniciada.`) })}>Renovar</RowActionButton>
                 </div>
               )},
             ]}
