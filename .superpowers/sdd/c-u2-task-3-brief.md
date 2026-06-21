### Task 3: Infra — reconstituição em `CobrancaJpa` + seed `V6`

**Files:**
- Modify: `infraestrutura/src/main/java/school/cesar/acadlab/infraestrutura/persistencia/jpa/CobrancaJpa.java:219-225`
- Create: `apresentacao-backend/src/main/resources/db/migration/V6__contestacao_seed.sql`

**Interfaces:**
- Consumes: `StatusContestacao{PENDENTE,DEFERIDA,INDEFERIDA}`, `Contestacao.deferir`/`indeferir` (Task 1).
- Produces: reconstituição correta da `Contestacao` resolvida; seed da cobrança contestada.

- [ ] **Step 1: Atualizar a reconstituição da `Contestacao`**

Em `CobrancaJpa.java`, substituir o bloco (linhas 219-225):
```java
        Contestacao contestacao = null;
        if (jpa.contestacaoRequerente != null) {
            contestacao = new Contestacao(new EstudanteId(jpa.contestacaoRequerente),
                    jpa.contestacaoJustificativa, jpa.contestacaoData);
            if (jpa.contestacaoStatus == StatusContestacao.RESOLVIDA)
                contestacao.resolver(jpa.contestacaoParecer != null ? jpa.contestacaoParecer : "");
        }
```
por:
```java
        Contestacao contestacao = null;
        if (jpa.contestacaoRequerente != null) {
            contestacao = new Contestacao(new EstudanteId(jpa.contestacaoRequerente),
                    jpa.contestacaoJustificativa, jpa.contestacaoData);
            var parecer = jpa.contestacaoParecer != null ? jpa.contestacaoParecer : "";
            if (jpa.contestacaoStatus == StatusContestacao.DEFERIDA)
                contestacao.deferir(parecer);
            else if (jpa.contestacaoStatus == StatusContestacao.INDEFERIDA)
                contestacao.indeferir(parecer);
        }
```

> A reconstituição só restaura o status/parecer da `Contestacao`; o `valorAtual` ajustado já vem persistido na própria `CobrancaJpa` e é passado a `Cobranca.reconstituir` separadamente. `findByContestacaoStatus(PENDENTE)` e o mapeamento `toResumo` (status `.name()`) permanecem inalterados.

- [ ] **Step 2: Criar a migração `V6__contestacao_seed.sql`**

```sql
-- ─── CONTESTAÇÃO seed (cobrança 6, que já é CONTESTADA no V1) ─────────────────
-- V1 já aplicado não pode ser editado; populamos as colunas de contestação aqui.
UPDATE cobranca
   SET contestacao_requerente   = 3,
       contestacao_justificativa = 'Valor cobrado diverge do contrato firmado.',
       contestacao_data         = '2025-08-15',
       contestacao_status       = 'PENDENTE'
 WHERE id = 6 AND contestacao_requerente IS NULL;
```

> Os nomes de coluna seguem o naming snake_case do Hibernate para os campos `contestacaoRequerente`/`contestacaoJustificativa`/`contestacaoData`/`contestacaoStatus` de `CobrancaJpa`. O `requerente = 3` casa com o `estudante_id` da cobrança 6 no V1 (titular da cobrança), respeitando a invariante de titularidade.

- [ ] **Step 3: Compilar**

Run: `mvn -q -pl infraestrutura -am compile`
Expected: BUILD SUCCESS.

---

