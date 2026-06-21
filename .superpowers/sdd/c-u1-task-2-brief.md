### Task 2: Domínio — `CobrancaServico` + BDD da resolução

**Files:**
- Modify: `dominio-gestao-financeira/src/main/java/school/cesar/acadlab/dominio/gestaofinanceira/CobrancaServico.java:68-74` (substituir `resolverContestacao`)
- Modify: `dominio-gestao-financeira/src/test/resources/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/resolver_contestacao.feature`
- Modify: `dominio-gestao-financeira/src/test/java/school/cesar/acadlab/dominio/gestaofinanceira/cobranca/ResolverContestacaoFuncionalidade.java`

**Interfaces:**
- Consumes: `Cobranca.deferirContestacao`/`indeferirContestacao`, `ModoAjuste`, `StatusContestacao` (Task 1).
- Produces (consumido Task 4): `CobrancaServico.deferirContestacao(CobrancaId,ModoAjuste,BigDecimal,String)`, `CobrancaServico.indeferirContestacao(CobrancaId,String)`.

- [ ] **Step 1: Reescrever a feature (pt)**

`resolver_contestacao.feature`:
```gherkin
# language: pt

Funcionalidade: Resolver Contestação de Cobrança

  Cenário: Indeferir contestação mantém o valor
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro indefere a contestação com parecer "Cobrança correta"
    Então a contestação deve ter status "INDEFERIDA"
    E a cobrança deve retornar ao status ABERTA após resolução
    E o valor atual da cobrança permanece 1500.00

  Cenário: Deferir com percentual reduz o valor
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro defere a contestação com 20 por cento e parecer "Ajuste deferido"
    Então a contestação deve ter status "DEFERIDA"
    E o valor atual da cobrança permanece 1200.00

  Cenário: Deferir com valor absoluto define o valor
    Dado uma cobrança contestada pelo estudante 1 no contrato 50
    Quando o setor financeiro defere a contestação com o valor 1000.00 e parecer "Novo valor"
    Então a contestação deve ter status "DEFERIDA"
    E o valor atual da cobrança permanece 1000.00

  Cenário: Rejeitar resolução de cobrança sem contestação
    Dado uma cobrança aberta sem contestação para o contrato 51
    Quando o setor financeiro tenta indeferir a contestação
    Então o sistema deve rejeitar informando "não há contestação registrada"

  Cenário: Rejeitar segunda resolução de contestação já resolvida
    Dado uma cobrança com contestação já resolvida para o contrato 52
    Quando o setor financeiro tenta indeferir a contestação novamente
    Então o sistema deve rejeitar informando "contestação já foi resolvida"
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=RunCucumberTest`
Expected: FALHA — passos/serviço indefinidos (e compilação dos steps, pois ainda chamam `resolverContestacao`).

- [ ] **Step 3: Atualizar `CobrancaServico`**

Substituir o método `resolverContestacao` (linhas 68-74) por:
```java
    public void deferirContestacao(CobrancaId id, ModoAjuste modo, BigDecimal valor, String parecer) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.deferirContestacao(modo, valor, parecer);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }

    public void indeferirContestacao(CobrancaId id, String parecer) {
        notNull(id, "id obrigatório");
        var cobranca = repositorio.obter(id);
        var evento = cobranca.indeferirContestacao(parecer);
        repositorio.salvar(cobranca);
        barramento.postar(evento);
    }
```

Adicionar os imports necessários no topo do arquivo (se ainda não houver): `import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.ModoAjuste;` (e confirmar que `java.math.BigDecimal` já está importado — está, pois `registrarPagamento`/`aplicarDesconto` usam `BigDecimal`).

- [ ] **Step 4: Atualizar os steps `ResolverContestacaoFuncionalidade`**

Reescrever o corpo de `ResolverContestacaoFuncionalidade.java` (mantendo o pacote e o construtor):
```java
package school.cesar.acadlab.dominio.gestaofinanceira.cobranca;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class ResolverContestacaoFuncionalidade {
    private final GestaoFinanceiraFuncionalidade ctx;
    private CobrancaId cobrancaId;

    public ResolverContestacaoFuncionalidade(GestaoFinanceiraFuncionalidade ctx) {
        this.ctx = ctx;
    }

    @Dado("uma cobrança contestada pelo estudante {int} no contrato {int}")
    public void cobrancaContestadasPeloEstudante(int estudanteId, int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(estudanteId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.contestar(cobrancaId, new EstudanteId(estudanteId), "Valor diverge do contrato");
    }

    @Dado("uma cobrança aberta sem contestação para o contrato {int}")
    public void cobrancaAbertaSemContestacao(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(700 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
    }

    @Dado("uma cobrança com contestação já resolvida para o contrato {int}")
    public void cobrancaComContestacaoJaResolvida(int contratoId) {
        ctx.verificadorMatricula.setMatricula(true);
        var cobranca = ctx.servico.gerarCobranca(new ContratoId(contratoId), new EstudanteId(800 + contratoId),
                new PeriodoLetivoId(1), new BigDecimal("1500.00"), LocalDate.of(2025, 2, 10));
        cobrancaId = cobranca.getId();
        ctx.servico.contestar(cobrancaId, new EstudanteId(800 + contratoId), "Contestação inicial");
        ctx.servico.indeferirContestacao(cobrancaId, "Primeira resolução");
    }

    @Quando("o setor financeiro indefere a contestação com parecer {string}")
    public void indefere(String parecer) {
        ctx.servico.indeferirContestacao(cobrancaId, parecer);
    }

    @Quando("o setor financeiro defere a contestação com {int} por cento e parecer {string}")
    public void deferePercentual(int pct, String parecer) {
        ctx.servico.deferirContestacao(cobrancaId, ModoAjuste.PERCENTUAL, new BigDecimal(pct), parecer);
    }

    @Quando("o setor financeiro defere a contestação com o valor {double} e parecer {string}")
    public void defereValor(double valor, String parecer) {
        ctx.servico.deferirContestacao(cobrancaId, ModoAjuste.VALOR,
                BigDecimal.valueOf(valor).setScale(2, java.math.RoundingMode.HALF_UP), parecer);
    }

    @Quando("o setor financeiro tenta indeferir a contestação")
    public void tentaIndeferir() {
        try {
            ctx.servico.indeferirContestacao(cobrancaId, "parecer");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Quando("o setor financeiro tenta indeferir a contestação novamente")
    public void tentaIndeferirNovamente() {
        try {
            ctx.servico.indeferirContestacao(cobrancaId, "novo parecer");
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Entao("a contestação deve ter status {string}")
    public void contestacaoDeveTerStatus(String status) {
        var contestacao = ctx.repositorio.obter(cobrancaId).getContestacao();
        Assertions.assertNotNull(contestacao);
        Assertions.assertEquals(StatusContestacao.valueOf(status), contestacao.getStatus());
    }

    @E("a cobrança deve retornar ao status ABERTA após resolução")
    public void cobrancaDeveRetornarAoStatusAbertaAposResolucao() {
        Assertions.assertEquals(StatusCobranca.ABERTA, ctx.repositorio.obter(cobrancaId).getStatus());
    }

    @E("o valor atual da cobrança permanece {double}")
    public void valorAtualPermanece(double valor) {
        Assertions.assertEquals(0, BigDecimal.valueOf(valor).setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(ctx.repositorio.obter(cobrancaId).getValorAtual()));
    }
}
```

> Nota: o passo "o sistema deve rejeitar informando {string}" já existe em `PassosCompartilhadosFinanceira` e continua válido (lê `ctx.excecao`).

- [ ] **Step 5: Rodar todos os testes do módulo e ver passar**

Run: `mvn -q -pl dominio-gestao-financeira test`
Expected: PASS — feature de resolução (deferir/indeferir/valor) + `CobrancaTest` + demais features e testes do módulo (incluindo os de bolsa do Sub-A).

---

