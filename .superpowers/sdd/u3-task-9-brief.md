### Task 9: Frontend — religar a aba "Bolsas & Descontos" em `financeiro.tsx`

**Files:**
- Modify: `apresentacao-frontend/react/src/routes/financeiro.tsx`

**Interfaces:**
- Consumes: hooks/tipos da Task 8; `formatValidade` (Task 8); `formatData`/`formatMoeda` já importados.

**Regra:** só a aba `tab === "bolsas"` muda. Mock `bolsas`/`suspenderBolsa`/`reativarBolsa`/`novaBolsaOpen` e o tipo `Bolsa` local são substituídos pelos hooks. Markup/classes preservados.

- [ ] **Step 1: Imports e hooks na `FinanceiroView`**

Adicionar ao import de `@/lib/financeiro`:
```ts
  useBolsas, useConcederBolsa, useSuspenderBolsa, useReativarBolsa, useRenovarBolsa,
  type BolsaResumo, type TipoBolsa, type StatusBolsa,
```
Adicionar `formatValidade` ao import de `@/lib/format`.

Dentro de `FinanceiroView`, adicionar:
```tsx
const bolsasQuery = useBolsas();
const bolsas = bolsasQuery.data ?? [];
const concederBolsa = useConcederBolsa();
const suspenderBolsa = useSuspenderBolsa();
const reativarBolsa = useReativarBolsa();
const renovarBolsa = useRenovarBolsa();
const [novaBolsaOpen, setNovaBolsaOpen] = useState(false);
const rotuloStatusBolsa: Record<StatusBolsa, string> = { ATIVA: "Ativa", EM_RENOVACAO: "Em renovação", SUSPENSA: "Suspensa" };
const tomStatusBolsa = (s: StatusBolsa) => (s === "ATIVA" ? "success" : s === "SUSPENSA" ? "danger" : "warning");
```
Remover o mock `const [bolsas, setBolsas] = useState<Bolsa[]>([...])`, as funções `suspenderBolsa`/`reativarBolsa` mock, o `const [novaBolsaOpen, setNovaBolsaOpen]` duplicado e o tipo local `Bolsa` (manter `Contestacao` removido no Sub-0; manter `Inadimplente`/`Lancamento`).

- [ ] **Step 2: Ajustar contador da aba e stat "Bolsas ativas"**

No `TabsRow`, a aba `bolsas` usa `count: bolsas.length`. No `StatsRow` da `FinanceiroView`, "Bolsas ativas" = `bolsas.filter((b) => b.status === "ATIVA").length`.

- [ ] **Step 3: Formulário "Nova concessão" via `useConcederBolsa`**

Estado local para os campos (mesmos `Input`/`FormField`):
```tsx
const [novaMatricula, setNovaMatricula] = useState("");
const [novoTipo, setNovoTipo] = useState<TipoBolsa>("MERITO");
const [novoPercentual, setNovoPercentual] = useState("");
const [novaValidade, setNovaValidade] = useState(""); // input type=date → ISO
```
Botão "Conceder":
```tsx
onClick={() => concederBolsa.mutate(
  { estudanteId: Number(novaMatricula), tipo: novoTipo, percentual: Number(novoPercentual), validade: novaValidade },
  { onSuccess: () => { setNovaBolsaOpen(false); toast.success("Bolsa concedida."); } },
)}
```
(O campo "Matrícula" passa a coletar o `estudanteId` numérico; o `Input` de validade vira `type="date"` produzindo ISO — mantendo as classes/`FormField` atuais. Tipo via os 4 valores de `TipoBolsa`.)

- [ ] **Step 4: `DataTable` de bolsas vinda do backend**

Colunas (cabeçalhos/estilos preservados):
```tsx
columns={[
  { key: "id", header: "Código", render: (r) => `BL-${r.id}` },
  { key: "estudante", header: "Beneficiário", render: (r) => `Estudante #${r.estudanteId}` },
  { key: "tipo", header: "Tipo" },
  { key: "percentual", header: "%", align: "right", render: (r) => `${r.percentual}%` },
  { key: "validade", header: "Validade", render: (r) => formatValidade(r.validade) },
  { key: "status", header: "Status", render: (r) => (
    <StatusBadge tone={tomStatusBolsa(r.status)}>{rotuloStatusBolsa[r.status]}</StatusBadge>
  )},
  { key: "acoes", header: "", align: "right", render: (r) => (
    <div className="flex justify-end gap-2">
      {r.status === "ATIVA"
        ? <RowActionButton tone="danger" onClick={() => suspenderBolsa.mutate(r.id, { onSuccess: () => toast.warning(`Bolsa BL-${r.id} suspensa.`) })}>Suspender</RowActionButton>
        : <RowActionButton tone="info" onClick={() => reativarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Bolsa BL-${r.id} reativada.`) })}>Reativar</RowActionButton>}
      <RowActionButton onClick={() => renovarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Renovação de BL-${r.id} iniciada.`) })}>Renovar</RowActionButton>
    </div>
  )},
]}
rows={bolsas}
```

- [ ] **Step 4b: Reativar só faz sentido em SUSPENSA**

Como há 3 status, o botão exibe "Suspender" quando `ATIVA` e "Reativar" caso contrário; para `EM_RENOVACAO`, "Reativar" chamaria o backend e seria rejeitado (`reativar` exige SUSPENSA). Para evitar erro silencioso, exibir "Suspender" para `ATIVA` e `EM_RENOVACAO` (ambas suspendíveis) e "Reativar" apenas para `SUSPENSA`:
```tsx
{r.status === "SUSPENSA"
  ? <RowActionButton tone="info" onClick={() => reativarBolsa.mutate(r.id, { onSuccess: () => toast.success(`Bolsa BL-${r.id} reativada.`) })}>Reativar</RowActionButton>
  : <RowActionButton tone="danger" onClick={() => suspenderBolsa.mutate(r.id, { onSuccess: () => toast.warning(`Bolsa BL-${r.id} suspensa.`) })}>Suspender</RowActionButton>}
```
(Usar esta versão; substitui o ternário do Step 4.)

- [ ] **Step 5: Verificar build do front**

Run: `npm run build`
Expected: sem erros de tipo.

- [ ] **Step 6: Verificação visual de regressão (diff)**

Run: `git diff --stat -- src/routes/financeiro.tsx`
Inspecionar: só a aba bolsas mudou (data/tipos/handlers); nenhuma classe Tailwind/estrutura alterada; abas Contestações/Inadimplência/Conciliação/Relatórios idênticas. Reverter qualquer mudança estrutural acidental.

---

## Self-Review

**1. Spec coverage:**
- §2 agregado Bolsa + enums + eventos → Task 1 ✓
- §2 BolsaRepositorio/BolsaServico → Task 2 ✓
- §3 RN5 AutorizacaoDescontoPorBolsa → Task 3 ✓
- §4 conserto wiring CobrancaServico + adapter matrícula → Tasks 6 (adapter) + 7 (beans) ✓
- §5 aplicação (BolsaResumo/Repositorio/Servico) → Task 4 ✓
- §6 infra BolsaJpa + V5 → Task 5 ✓
- §7 controlador backend/bolsas → Task 7 ✓
- §8 frontend lib + aba bolsas → Tasks 8-9 ✓
- §9 BDD + testes → Tasks 1 (BolsaTest), 2 (3 features + steps), 3 (RN5 feature) ✓
- §11 critérios → todas as tasks + gates ✓

**2. Placeholder scan:** sem "TBD/TODO". Código completo em cada step. A colisão de erasure foi resolvida na origem: `BolsaRepositorioAplicacao.listarResumos()` (Task 4) ≠ `BolsaRepositorio.listar()` (Task 2); a impl única (Task 5) implementa ambas sem conflito.

**3. Type consistency:** `Bolsa`/`BolsaId`/`TipoBolsa`/`StatusBolsa` consistentes entre Tasks 1-7. `BolsaResumo(id,estudanteId,tipo,percentual,validade,status)` igual entre Java (Task 4) e TS (Task 8). Endpoints `backend/bolsas` (Task 7) batem com hooks (Task 8): `GET bolsas`, `POST bolsas/conceder`, `POST bolsas/{id}/suspender|reativar|renovar`. `AutorizacaoDescontoPorBolsa` (Task 3) é o bean `VerificadorAutorizacaoDesconto` (Task 7). Adapter matrícula (Task 6) satisfaz o bean `CobrancaServico` (Task 7).

**Notas de execução:** sem commits; teste ao vivo é do usuário; eventos seguem o padrão de `Cobranca` (ctor privado, criados no agregado, postados pelo serviço).
