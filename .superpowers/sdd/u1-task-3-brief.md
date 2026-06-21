### Task 3: RN5 — `AutorizacaoDescontoPorBolsa` + BDD `aplicar_desconto_com_bolsa`

**Files:**
- Create: `.../dominio/gestaofinanceira/bolsa/AutorizacaoDescontoPorBolsa.java`
- Test: `.../test/.../gestaofinanceira/bolsa/AplicarDescontoComBolsaSteps.java`
- Test resources: `.../test/resources/.../gestaofinanceira/bolsa/aplicar_desconto_com_bolsa.feature`

**Interfaces:**
- Consumes: `BolsaRepositorio` (Task 2); `VerificadorAutorizacaoDesconto` (porta existente em `school.cesar.acadlab.dominio.gestaofinanceira`); `CobrancaServico` (existente).
- Produces (consumido Task 7): `AutorizacaoDescontoPorBolsa implements VerificadorAutorizacaoDesconto` — `autorizacaoValida(String)` = existe bolsa ATIVA cujo id == parse(autorizacaoId).

- [ ] **Step 1: Escrever a feature (RN5)**

`aplicar_desconto_com_bolsa.feature`:
```gherkin
# language: pt

Funcionalidade: Aplicar desconto respaldado por bolsa (RN5)

  Cenário: Desconto aceito com bolsa ativa
    Dado uma bolsa MERITO ativa de 10 por cento para o estudante 7
    E uma cobrança aberta de 1000.00 para o estudante 7 contra o contrato 70
    Quando aplico o desconto da bolsa ativa na cobrança
    Então o valor atual da cobrança deve ser 900.00 reais

  Cenário: Desconto recusado sem bolsa
    Dado uma cobrança aberta de 1000.00 para o estudante 8 contra o contrato 80
    Quando tento aplicar um desconto de 10 por cento com a autorização "999"
    Então o desconto é recusado por autorização inválida
```

- [ ] **Step 2: Rodar e ver falhar**

Run: `mvn -q -pl dominio-gestao-financeira test -Dtest=RunCucumberTest`
Expected: FALHA — `AutorizacaoDescontoPorBolsa` e passos indefinidos.

- [ ] **Step 3: Criar o serviço de domínio `AutorizacaoDescontoPorBolsa`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import school.cesar.acadlab.dominio.gestaofinanceira.VerificadorAutorizacaoDesconto;

public class AutorizacaoDescontoPorBolsa implements VerificadorAutorizacaoDesconto {
    private final BolsaRepositorio repositorio;

    public AutorizacaoDescontoPorBolsa(BolsaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean autorizacaoValida(String autorizacaoId) {
        if (autorizacaoId == null) return false;
        final int id;
        try {
            id = Integer.parseInt(autorizacaoId.trim());
        } catch (NumberFormatException e) {
            return false;
        }
        return repositorio.listar().stream()
                .anyMatch(b -> b.getId().valor() == id && b.getStatus() == StatusBolsa.ATIVA);
    }
}
```

- [ ] **Step 4: Criar os passos `AplicarDescontoComBolsaSteps`**

```java
package school.cesar.acadlab.dominio.gestaofinanceira.bolsa;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Quando;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.E;
import org.junit.jupiter.api.Assertions;
import school.cesar.acadlab.dominio.evento.EventoBarramento;
import school.cesar.acadlab.dominio.evento.EventoObservador;
import school.cesar.acadlab.dominio.gestaofinanceira.*;
import school.cesar.acadlab.dominio.gestaofinanceira.cobranca.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class AplicarDescontoComBolsaSteps {
    private final BolsaFuncionalidade bolsaCtx;
    private CobrancaServico cobrancaServico;
    private GestaoFinanceiraRepositorioTest cobrancaRepo;
    private CobrancaId cobrancaId;
    private String autorizacaoBolsa;
    private RuntimeException excecao;

    public AplicarDescontoComBolsaSteps(BolsaFuncionalidade bolsaCtx) {
        this.bolsaCtx = bolsaCtx;
    }

    private void inicializarCobrancaServico() {
        cobrancaRepo = new GestaoFinanceiraRepositorioTest();
        VerificadorMatriculaConfirmada matricula = (e, p) -> true;
        VerificadorAutorizacaoDesconto autorizacao = new AutorizacaoDescontoPorBolsa(bolsaCtx.repositorio);
        EventoBarramento barramento = new EventoBarramento() {
            @Override public <E> void adicionar(EventoObservador<E> o) {}
            @Override public <E> void postar(E ev) {}
        };
        cobrancaServico = new CobrancaServico(cobrancaRepo, matricula, autorizacao, barramento);
    }

    @Dado("uma bolsa {word} ativa de {int} por cento para o estudante {int}")
    public void bolsaAtiva(String tipo, int pct, int estudante) {
        var b = bolsaCtx.servico.conceder(new EstudanteId(estudante), TipoBolsa.valueOf(tipo),
                new BigDecimal(pct), LocalDate.of(2025, 12, 31));
        autorizacaoBolsa = String.valueOf(b.getId().valor());
    }

    @E("uma cobrança aberta de {double} para o estudante {int} contra o contrato {int}")
    @Dado("uma cobrança aberta de {double} para o estudante {int} contra o contrato {int}")
    public void cobrancaAberta(double valor, int estudante, int contrato) {
        if (cobrancaServico == null) inicializarCobrancaServico();
        var c = cobrancaServico.gerarCobranca(new ContratoId(contrato), new EstudanteId(estudante),
                new PeriodoLetivoId(1), BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP),
                LocalDate.of(2025, 2, 10));
        cobrancaId = c.getId();
    }

    @Quando("aplico o desconto da bolsa ativa na cobrança")
    public void aplicoDescontoBolsa() {
        cobrancaServico.aplicarDesconto(cobrancaId, new BigDecimal("10"), autorizacaoBolsa);
    }

    @Quando("tento aplicar um desconto de {int} por cento com a autorização {string}")
    public void tentoAplicar(int pct, String autorizacaoId) {
        try {
            cobrancaServico.aplicarDesconto(cobrancaId, new BigDecimal(pct), autorizacaoId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Entao("o valor atual da cobrança deve ser {double} reais")
    public void valorAtual(double valor) {
        Assertions.assertEquals(0, BigDecimal.valueOf(valor).setScale(2, RoundingMode.HALF_UP)
                .compareTo(cobrancaRepo.obter(cobrancaId).getValorAtual()));
    }

    @Entao("o desconto é recusado por autorização inválida")
    public void recusado() {
        Assertions.assertNotNull(excecao);
        Assertions.assertTrue(excecao.getMessage().contains("autorização inválida"));
    }
}
```

> Nota: a feature usa "estudante 7/8" distintos do `aplicar_desconto.feature` existente para não colidir em ids; cada cenário cria seu próprio `CobrancaServico` com o verificador baseado em bolsa.

- [ ] **Step 5: Rodar todos os testes do módulo e ver passar**

Run: `mvn -q -pl dominio-gestao-financeira test`
Expected: PASS — todas as features (bolsa + RN5 + 42 cobranças) e testes JUnit.

---

