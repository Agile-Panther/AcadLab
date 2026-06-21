# Contrato de Desenvolvimento — AcadLab

> **Propósito:** Este documento é o acordo técnico da equipe. Define a organização de pastas, o mapeamento entre features e módulos, as entidades compartilhadas e quem é responsável por cada implementação. Também serve como guia obrigatório para DDD, BDD (Gherkin/Cucumber) e TDD. **Siga este documento antes de escrever qualquer linha de código.**

---

## Índice

1. [Mapa de Features e Donos](#1-mapa-de-features-e-donos)
2. [Estrutura de Módulos Maven](#2-estrutura-de-módulos-maven)
3. [Organização Interna de um Módulo de Domínio](#3-organização-interna-de-um-módulo-de-domínio)
4. [Módulos com Features Pendentes de Criação](#4-módulos-com-features-pendentes-de-criação)
5. [Entidades Compartilhadas entre Features](#5-entidades-compartilhadas-entre-features)
6. [Regras de Consumo entre Domínios](#6-regras-de-consumo-entre-domínios)
7. [Guia DDD — Domain-Driven Design](#7-guia-ddd--domain-driven-design)
8. [Guia BDD — Gherkin + Cucumber](#8-guia-bdd--gherkin--cucumber)
9. [Guia TDD — Test-Driven Development](#9-guia-tdd--test-driven-development)
10. [Definition of Done por Feature](#10-definition-of-done-por-feature)
11. [Padrão de Commits](#11-padrão-de-commits)

---

## 1. Mapa de Features e Donos

| Feature | Título | Dono | Módulo Maven |
|---------|--------|------|--------------|
| **F-01** | Gestão Curricular do Curso | Julia | `dominio-curriculo` |
| **F-02** | Planejamento Acadêmico do Período Letivo | Claudia | `dominio-oferta-academica` ¹ |
| **F-03** | Planejamento e Oferta de Turmas | Clara | `dominio-oferta-academica` ¹ |
| **F-04** | Montagem e Ajuste de Matrícula | Vinicius | `dominio-matricula` |
| **F-05** | Gestão Pedagógica da Turma | Claudia | `dominio-gestao-pedagogica` |
| **F-06** | Gestão do Histórico Acadêmico | Julia | `dominio-historico-academico` |
| **F-07** | Secretaria Virtual Acadêmica | Bernardo | `dominio-secretaria-virtual` |
| **F-08** | Validação de Integralização e Colação | Bernardo | `dominio-integralizacao-curricular` ² |
| **F-09** | Gestão de Atividades Complementares | Jera | `dominio-atividades-complementares` |
| **F-10** | Permanência Acadêmica e Bolsas | Matheus | `dominio-permanencia-academica` |
| **F-11** | Apoio Psicopedagógico | Matheus | `dominio-permanencia-academica` |
| **F-12** | Mobilidade Acadêmica | Vinicius | `dominio-mobilidade-academica` |
| **F-13** | Gestão Financeira Acadêmica | Jera | `dominio-gestao-financeira` |
| **F-14** | Centro de Estágios e Oportunidades | Clara | `dominio-estagios` |

> ¹ F-02 e F-03 compartilham o módulo `dominio-oferta-academica`. Cada feature tem seu próprio sub-pacote. Ver [Seção 4](#4-módulos-com-features-pendentes-de-criação).
>
> ² F-08 requer criação de novo módulo. Ver [Seção 4](#4-módulos-com-features-pendentes-de-criação).

---

## 2. Estrutura de Módulos Maven

```
acadlab-pai/                          ← POM pai (groupId: school.cesar)
│
├── dominio-compartilhado/            ← IDs, exceções base, eventos de domínio base
├── dominio-curriculo/                ← F-01 (Julia)
├── dominio-oferta-academica/         ← F-02 (Claudia) + F-03 (Clara)
├── dominio-matricula/                ← F-04 (Vinicius)
├── dominio-gestao-pedagogica/        ← F-05 (Claudia)
├── dominio-historico-academico/      ← F-06 (Julia)
├── dominio-secretaria-virtual/       ← F-07 (Bernardo)
├── dominio-integralizacao-curricular/← F-08 (Bernardo) — CRIAR ²
├── dominio-atividades-complementares/← F-09 (Jera)
├── dominio-permanencia-academica/    ← F-10 + F-11 (Matheus)
├── dominio-mobilidade-academica/     ← F-12 (Vinicius)
├── dominio-gestao-financeira/        ← F-13 (Jera)
├── dominio-estagios/                 ← F-14 (Clara)
│
├── aplicacao/                        ← Casos de uso, orquestração entre domínios
├── infraestrutura/                   ← JPA, adapters de persistência
├── apresentacao-backend/             ← Controllers REST (Spring Boot)
└── apresentacao-frontend/            ← React/Vite
```

### Dependências entre módulos

Regra geral: **módulos de domínio dependem apenas de `dominio-compartilhado`**. O módulo `aplicacao` é o único autorizado a orquestrar entre domínios diferentes.

```
dominio-compartilhado
       ↑
  (todos os domínios dependem dele)

aplicacao
  ↑ depende de todos os domínios (lê portas/interfaces)

infraestrutura
  ↑ depende de aplicacao + domínios (implementa repositórios)

apresentacao-backend
  ↑ depende de aplicacao
```

---

## 3. Organização Interna de um Módulo de Domínio

Siga exatamente esta estrutura de pacotes. Use F-11 (já implementada) como referência canônica.

```
dominio-{nome}/
└── src/
    ├── main/
    │   └── java/school/cesar/acadlab/dominio/{contexto}/
    │       │
    │       ├── {agregado}/               ← um sub-pacote por agregado
    │       │   ├── {Agregado}.java       ← Entidade raiz do agregado
    │       │   ├── {AgregadoId}.java     ← Value Object de identidade
    │       │   ├── {AgregadoRepositorio}.java  ← interface do repositório
    │       │   └── {StatusAgregado}.java ← enum de estado (se houver)
    │       │
    │       ├── {entidade-interna}/       ← entidades que não são raiz
    │       │   └── {Entidade}.java
    │       │
    │       ├── {valor}/                  ← value objects complexos
    │       │   └── {Valor}.java
    │       │
    │       ├── {Contexto}Servico.java    ← serviço de domínio principal
    │       ├── ConsultaServico.java      ← serviço de consulta (leitura)
    │       └── {OutroServico}.java       ← serviços adicionais se necessário
    │
    └── test/
        ├── java/school/cesar/acadlab/dominio/{contexto}/
        │   ├── {Agregado}Test.java        ← testes unitários do agregado
        │   └── {Contexto}Funcionalidade.java  ← step definitions Cucumber
        │
        └── resources/
            └── features/
                └── {contexto}/
                    ├── {us01-nome}.feature
                    ├── {us02-nome}.feature
                    └── ...
```

### Exemplo concreto (F-11):

```
dominio-permanencia-academica/
└── src/main/java/school/cesar/acadlab/dominio/apoiopsicopedagogico/
    ├── caso/
    │   ├── Caso.java
    │   ├── CasoId.java
    │   ├── CasoRepositorio.java
    │   └── StatusCaso.java
    ├── solicitacao/
    │   ├── SolicitacaoApoio.java
    │   ├── SolicitacaoApoioId.java
    │   └── SolicitacaoApoioRepositorio.java
    ├── triagem/
    │   ├── Triagem.java
    │   └── PrioridadeTriagem.java
    ├── ApoioServico.java
    ├── TriagemServico.java
    ├── AtendimentoServico.java
    └── ConsultaServico.java
```

---

## 4. Módulos com Features Pendentes de Criação

### F-02 e F-03 — `dominio-oferta-academica` (Claudia + Clara)

Este módulo é compartilhado. F-02 (Claudia) implementa `PeriodoLetivo` e `JanelaAcademica`. F-03 (Clara) implementa `Turma`, `Sala` e `Professor`. **Separação obrigatória por sub-pacotes:**

```
dominio-oferta-academica/
└── src/main/java/school/cesar/acadlab/dominio/
    ├── periodoletivo/          ← dono: NETO (F-02)
    │   ├── periodoletivo/
    │   │   ├── PeriodoLetivo.java
    │   │   ├── PeriodoLetivoId.java
    │   │   ├── PeriodoLetivoRepositorio.java
    │   │   └── StatusPeriodoLetivo.java
    │   ├── janelaacademica/
    │   │   ├── JanelaAcademica.java
    │   │   └── TipoJanela.java
    │   └── PeriodoLetivoServico.java
    │
    └── ofertaturmas/           ← dono: CLARA (F-03)
        ├── turma/
        │   ├── Turma.java
        │   ├── TurmaId.java
        │   └── TurmaRepositorio.java
        ├── sala/
        │   ├── Sala.java
        │   └── SalaRepositorio.java
        ├── professor/
        │   ├── Professor.java
        │   └── ProfessorRepositorio.java
        └── OfertaTurmaServico.java
```

**Regra crítica:** Claudia implementa `PeriodoLetivo` primeiro, pois `Turma` (Clara) depende de `PeriodoLetivoId`. Clara não cria `PeriodoLetivo` — apenas consome o ID como valor.

**Claudia deve entregar antes de Clara iniciar a implementação de `Turma`.**

---

### F-08 — `dominio-integralizacao-curricular` (Bernardo)

Este módulo **ainda não existe no `pom.xml`**. Bernardo é responsável por criá-lo seguindo o padrão dos módulos existentes.

**Passos para criação:**
1. Criar o diretório `dominio-integralizacao-curricular/`
2. Criar o `pom.xml` do módulo (copiar de outro módulo de domínio e ajustar o `artifactId`)
3. Adicionar `<module>dominio-integralizacao-curricular</module>` no `pom.xml` pai
4. Adicionar dependências de `dominio-curriculo` e `dominio-historico-academico` como `provided` ou via interface

```
dominio-integralizacao-curricular/
└── src/main/java/school/cesar/acadlab/dominio/integralizacao/
    ├── integralizacao/
    │   ├── IntegralizacaoCurricular.java
    │   ├── IntegralizacaoId.java
    │   └── IntegralizacaoRepositorio.java
    ├── colacao/
    │   ├── ColacaoDeGrau.java
    │   └── ColacaoId.java
    ├── checklist/
    │   └── ItemChecklist.java
    ├── IntegralizacaoServico.java
    └── ConsultaIntegralizacaoServico.java
```

---

## 5. Entidades Compartilhadas entre Features

Esta seção define **quem cria** cada entidade compartilhada e **quem apenas consome**.

### 5.1 Mapa de Propriedade

| Entidade | Módulo | Dono | Consumidores |
|----------|--------|------|--------------|
| `EstudanteId` | `dominio-compartilhado` | **Todos (já existe como VO)** | Todos os módulos |
| `PeriodoLetivo` / `JanelaAcademica` | `dominio-oferta-academica` | **Claudia (F-02)** | F-03, F-04, F-05, F-06, F-07, F-13 |
| `MatrizCurricular` / `Disciplina` | `dominio-curriculo` | **Julia (F-01)** | F-03, F-04, F-06, F-08, F-09, F-12 |
| `Turma` | `dominio-oferta-academica` | **Clara (F-03)** | F-04, F-05, F-06 |
| `Matricula` | `dominio-matricula` | **Vinicius (F-04)** | F-05, F-06, F-13, F-14 |
| `HistoricoAcademico` | `dominio-historico-academico` | **Julia (F-06)** | F-08, F-12 |
| `AtividadeComplementar` | `dominio-atividades-complementares` | **Jera (F-09)** | F-08 |
| `Beneficio` / `Edital` | `dominio-permanencia-academica` | **Matheus (F-10)** | F-13 |

### 5.2 O que vai em `dominio-compartilhado`

O módulo `dominio-compartilhado` contém **apenas** artefatos sem lógica de negócio própria:

- Value Objects de identidade usados em múltiplos domínios (`EstudanteId`, `CursoId`, etc.)
- Exceções base do domínio (`DominiException`, `RegraDeNegocioVioladaException`)
- Interfaces de evento de domínio (`EventoDominio`)
- Enums transversais (ex.: `TipoUsuario`, `StatusGeral`)

**Não colocar** em `dominio-compartilhado`: entidades, serviços, regras de negócio, repositórios.

### 5.3 Dependências entre features — quando comunicar

Se sua feature depende de dados de outro domínio, siga estas regras:

1. **Use apenas o ID** da entidade de outro domínio dentro do seu agregado. Exemplo: `Matricula` guarda `TurmaId`, não uma referência a `Turma`.
2. **Consultas cross-domínio** só acontecem na camada `aplicacao`, nunca dentro de um módulo de domínio.
3. **Validações que exigem dados externos** (ex.: checar se pré-requisito foi cumprido) são implementadas como porta/interface no seu domínio e injetadas via construtor ou Spring. O módulo `aplicacao` liga as implementações.

---

## 6. Regras de Consumo entre Domínios

### Regra 1 — Nunca importe um módulo de domínio dentro de outro módulo de domínio

```java
// ❌ PROIBIDO em dominio-matricula
import school.cesar.acadlab.dominio.curriculo.MatrizCurricular;

// ✅ CORRETO — use apenas o ID como Value Object
import school.cesar.acadlab.dominio.compartilhado.MatrizCurricularId;
```

### Regra 2 — Consultas cross-domínio usam porta (interface)

Declare uma interface no seu domínio para expressar a consulta necessária:

```java
// Em dominio-matricula — declare a porta
public interface ConsultaPreRequisitosPorta {
    boolean estudanteAprovouPreRequisitos(EstudanteId estudante, DisciplinaId disciplina);
}

// Em aplicacao — implemente a porta ligando os domínios
@Component
public class ConsultaPreRequisitosAdapter implements ConsultaPreRequisitosPorta {
    private final HistoricoAcademicoRepositorio historico;
    private final CurriculoRepositorio curriculo;
    // ...
}
```

### Regra 3 — Eventos de domínio para notificações assíncronas

Quando sua feature precisa notificar outra feature de um estado (ex.: matrícula confirmada → financeiro gera cobrança), use eventos de domínio, não chamadas diretas:

```java
// Em dominio-matricula — publique o evento
public class Matricula {
    public void confirmar() {
        // lógica...
        registrarEvento(new MatriculaConfirmadaEvento(this.id, this.estudanteId));
    }
}
```

---

## 7. Guia DDD — Domain-Driven Design

### 7.1 Agregados e Raízes de Agregado

Cada agregado tem **uma única raiz** que controla o acesso a todos os seus objetos internos. Regras de negócio vivem no agregado, não nos serviços.

```java
// ✅ CORRETO — regra de negócio no agregado
public class Caso {
    public void encerrar(String conclusao) {
        if (atendimentos.isEmpty()) {
            throw new RegraDeNegocioVioladaException("RN-07: Caso sem atendimentos não pode ser encerrado");
        }
        this.status = StatusCaso.ENCERRADO;
        this.conclusao = conclusao;
        registrarEvento(new CasoEncerradoEvento(this.id));
    }
}

// ❌ ERRADO — regra de negócio no serviço
public class CasoServico {
    public void encerrar(CasoId id, String conclusao) {
        Caso caso = repositorio.buscar(id);
        if (caso.getAtendimentos().isEmpty()) { // regra vazou para o serviço
            throw new Exception("...");
        }
        caso.setStatus(StatusCaso.ENCERRADO); // modelo anêmico
    }
}
```

### 7.2 Value Objects

Use Value Objects para representar conceitos sem identidade própria. São **imutáveis**.

```java
public final class CasoId {
    private final UUID valor;

    public CasoId(UUID valor) {
        Objects.requireNonNull(valor, "CasoId não pode ser nulo");
        this.valor = valor;
    }

    public static CasoId gerar() {
        return new CasoId(UUID.randomUUID());
    }

    @Override
    public boolean equals(Object o) { /* comparação por valor */ }
    @Override
    public int hashCode() { /* baseado em valor */ }
}
```

### 7.3 Repositórios

Defina interfaces no domínio. A implementação fica em `infraestrutura`.

```java
// Em dominio-xxx — apenas interface
public interface CasoRepositorio {
    void salvar(Caso caso);
    Optional<Caso> buscarPorId(CasoId id);
    List<Caso> buscarPorEstudante(EstudanteId estudanteId);
}
```

### 7.4 Serviços de Domínio

Use serviços apenas quando a lógica envolve **mais de um agregado** ou não cabe naturalmente em nenhum deles.

```java
// ✅ Serviço coordena entre dois agregados do MESMO domínio
public class ApoioServico {
    public Caso solicitarApoio(EstudanteId estudanteId) {
        boolean temCasoAtivo = casoRepositorio
            .buscarCasoAtivoDoEstudante(estudanteId).isPresent();

        SolicitacaoApoio solicitacao = new SolicitacaoApoio(EstudanteId, LocalDateTime.now());
        solicitacaoRepositorio.salvar(solicitacao);

        if (temCasoAtivo) {
            throw new RegraDeNegocioVioladaException("Estudante já possui caso ativo");
        }

        Caso caso = Caso.abrir(estudanteId);
        casoRepositorio.salvar(caso);
        return caso;
    }
}
```

### 7.5 Eventos de Domínio

Registre eventos dentro dos métodos do agregado. O framework (Spring) os despacha.

```java
public abstract class AgregadoBase {
    private final List<EventoDominio> eventos = new ArrayList<>();

    protected void registrarEvento(EventoDominio evento) {
        eventos.add(evento);
    }

    public List<EventoDominio> coletarEventos() {
        List<EventoDominio> copia = List.copyOf(eventos);
        eventos.clear();
        return copia;
    }
}
```

---

## 8. Guia BDD — Gherkin + Cucumber

### 8.1 Estrutura obrigatória de arquivos de feature

Um arquivo `.feature` por User Story. Nome do arquivo = código da US em kebab-case.

```
src/test/resources/features/{contexto}/
    us01-solicitar-apoio.feature
    us02-realizar-triagem.feature
    us03-registrar-atendimento.feature
    ...
```

### 8.2 Template de cenário Gherkin (em português)

Use o idioma do domínio (linguagem ubíqua). Nunca use termos técnicos como "repositório", "banco de dados" ou "entidade" nos cenários.

```gherkin
# language: pt

Funcionalidade: Solicitar apoio psicopedagógico
  Como estudante
  Quero solicitar apoio psicopedagógico
  Para receber orientação quando dificuldades estiverem afetando minha vida acadêmica

  Cenário: Estudante sem caso ativo solicita apoio com sucesso
    Dado um estudante sem caso psicopedagógico ativo
    Quando o estudante solicita apoio psicopedagógico
    Então o sistema registra a solicitação com sucesso
    E um novo caso é aberto com status "EM_ABERTO"

  Cenário: Estudante com caso ativo não pode abrir nova solicitação
    Dado um estudante com caso psicopedagógico ativo
    Quando o estudante tenta solicitar apoio psicopedagógico
    Então o sistema rejeita a solicitação
    E a mensagem de erro informa que já existe caso ativo

  Cenário: Caso encerrado pode ser reaberto via nova solicitação formal
    Dado um estudante com caso psicopedagógico encerrado
    Quando o estudante solicita apoio psicopedagógico
    Então o caso encerrado é reaberto
    E o status do caso é atualizado para "EM_ABERTO"
```

### 8.3 Regras para escrever cenários

- **Dado** → estado inicial do mundo (pré-condição)
- **Quando** → ação do ator (único por cenário)
- **Então** → resultado observável
- **E / Mas** → continuação de Dado, Quando ou Então
- Um cenário = uma regra de negócio = um comportamento verificável
- Cenários devem ser independentes entre si (sem dependência de execução anterior)
- Use `Esquema do Cenário` + `Exemplos` para múltiplos casos com mesma lógica

```gherkin
  Esquema do Cenário: Triagem com diferentes prioridades
    Dado uma solicitação de apoio aguardando triagem
    Quando o psicopedagogo realiza triagem com prioridade "<prioridade>"
    Então a prioridade do caso é registrada como "<prioridade>"

    Exemplos:
      | prioridade |
      | BAIXA      |
      | MEDIA      |
      | ALTA       |
```

### 8.4 Step Definitions

Crie uma classe `{Contexto}Funcionalidade.java` por feature. Use `@SpringBootTest` com perfil de teste.

```java
@CucumberContextConfiguration
@SpringBootTest
public class ApoioFuncionalidade {

    @Autowired
    private ApoioServico apoioServico;

    @Autowired
    private CasoRepositorio casoRepositorio;

    private EstudanteId estudanteId;
    private Caso casoCriado;
    private Exception excecaoCapturada;

    @Dado("um estudante sem caso psicopedagógico ativo")
    public void estudanteSemCasoAtivo() {
        this.estudanteId = EstudanteId.gerar();
        // repositório começa limpo (H2 em memória)
    }

    @Quando("o estudante solicita apoio psicopedagógico")
    public void estudanteSolicitaApoio() {
        try {
            this.casoCriado = apoioServico.solicitarApoio(estudanteId);
        } catch (Exception e) {
            this.excecaoCapturada = e;
        }
    }

    @Entao("o sistema registra a solicitação com sucesso")
    public void solicitacaoRegistradaComSucesso() {
        assertNotNull(casoCriado);
        assertNull(excecaoCapturada);
    }

    @E("um novo caso é aberto com status {string}")
    public void casoAbertoComStatus(String statusEsperado) {
        assertEquals(StatusCaso.valueOf(statusEsperado), casoCriado.getStatus());
    }
}
```

### 8.5 Runner do Cucumber

Cada módulo deve ter um `RunCucumberTest.java`:

```java
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "school.cesar.acadlab.dominio.{contexto}")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports.html")
public class RunCucumberTest {}
```

---

## 9. Guia TDD — Test-Driven Development

### 9.1 O ciclo obrigatório

**Vermelho → Verde → Refatorar.** Nunca escreva código de produção sem um teste falhando primeiro.

```
1. Escreva o teste (falha — Vermelho)
2. Escreva o mínimo de código para o teste passar (Verde)
3. Refatore sem quebrar o teste (Refatorar)
4. Repita para a próxima regra de negócio
```

### 9.2 Tipos de teste e onde ficam

| Tipo | Classe | Localização | Escopo |
|------|--------|-------------|--------|
| **Unitário** | `{Agregado}Test.java` | `src/test/java/.../dominio/` | Agregado isolado (sem Spring) |
| **BDD/Integração** | `{Contexto}Funcionalidade.java` | `src/test/java/.../dominio/` | Feature completa (com Spring + H2) |

### 9.3 Testes unitários — template

```java
class CasoTest {

    @Test
    void deveLancarExcecaoAoEncerrarCasoSemAtendimentos() {
        // Arrange
        Caso caso = Caso.abrir(EstudanteId.gerar());

        // Act & Assert
        assertThrows(
            RegraDeNegocioVioladaException.class,
            () -> caso.encerrar("conclusão"),
            "RN-07: Caso sem atendimentos não pode ser encerrado"
        );
    }

    @Test
    void deveAlterarStatusParaEncerradoAposEncerramentoValido() {
        // Arrange
        Caso caso = Caso.abrir(EstudanteId.gerar());
        caso.registrarAtendimento(new Atendimento(/* ... */));

        // Act
        caso.encerrar("conclusão final");

        // Assert
        assertEquals(StatusCaso.ENCERRADO, caso.getStatus());
    }
}
```

### 9.4 Ordem de implementação sugerida por feature

Para cada User Story, siga esta ordem:

1. Escreva o arquivo `.feature` com os cenários (Gherkin)
2. Escreva os testes unitários do agregado principal (`{Agregado}Test.java`)
3. Implemente o agregado até os testes passarem
4. Escreva as step definitions (`{Contexto}Funcionalidade.java`)
5. Implemente o serviço de domínio e o repositório (interface) até os cenários BDD passarem
6. A implementação do repositório vai em `infraestrutura` — apenas depois que o domínio estiver verde

### 9.5 Configuração de teste (H2 em memória)

Cada módulo que precise de repositório nos testes deve ter `src/test/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

---

## 10. Definition of Done por Feature

Uma feature só está concluída quando **todos** os itens abaixo estiverem atendidos:

### Código

- [ ] Todos os agregados identificados no backlog estão implementados com regras de negócio internas
- [ ] Todos os Value Objects de identidade estão criados (imutáveis, com `equals`/`hashCode`)
- [ ] Repositórios definidos como interfaces no domínio (implementações em `infraestrutura`)
- [ ] Serviços de domínio criados apenas onde necessário (lógica entre agregados)
- [ ] Eventos de domínio registrados para estados relevantes
- [ ] Nenhuma referência a outros módulos de domínio (apenas `dominio-compartilhado`)

### Testes

- [ ] Um arquivo `.feature` por User Story do backlog
- [ ] Todos os cenários das regras de negócio (RN) cobertos em Gherkin
- [ ] Step definitions implementadas e passando
- [ ] Testes unitários para cada regra de negócio do agregado
- [ ] Todos os testes rodando com `mvn test` sem falhas
- [ ] Relatório Cucumber gerado em `target/cucumber-reports.html`

### Integração

- [ ] Branch nomeada conforme padrão: `feature/f-{XX}-{nome-kebab-case}`
- [ ] PR aberto para `develop` com descrição das User Stories implementadas
- [ ] Aprovação de ao menos um membro do time `@agile-panther/team-aprovadores-develop`
- [ ] Nenhum conflito com `develop` antes do merge

---

## 11. Padrão de Commits

Todo commit do projeto segue **Conventional Commits** com prefixo em inglês e mensagem em português.

### Formato obrigatório

```
<tipo>(<escopo>): <descrição em português> <local ou feature>
```

O `(<escopo>)` é **opcional**. A `<descrição>` deve ser curta (uma linha) e terminar indicando o local ou a feature afetada.

### Tipos permitidos

| Tipo | Quando usar |
|------|-------------|
| `feat` | Nova funcionalidade ou adição de código de produção |
| `fix` | Correção de bug ou problema |
| `test` | Adição ou correção de testes (BDD, unitários, integração) |
| `refactor` | Refatoração sem mudança de comportamento |
| `docs` | Alteração em documentação |
| `chore` | Tarefas de manutenção: dependências, build, configuração |
| `style` | Formatação de código sem mudança de lógica |

### Exemplos corretos

```
feat: adiciona agregado e value objects de mobilidade acadêmica
feat: implementa persistência JPA para mobilidade acadêmica
feat: adiciona controlador REST e beans para mobilidade acadêmica
test: adiciona testes BDD e unitários para mobilidade acadêmica
fix: corrige carga horária mínima no plano de estudos
chore: adiciona dependência commons-lang3 ao módulo de mobilidade
docs: atualiza contrato com padrão de commits
```

### Com escopo (opcional)

Use o escopo quando quiser precisar o sub-contexto afetado dentro de uma feature ou módulo:

```
feat(mobilidade): adiciona método reconstituir no agregado
fix(jpa): corrige lambda não-final em DiarioTurmaJpa
test(cancelamento): adiciona cenário BDD para RN-7
```

### Regras

1. **Prefixo sempre em inglês** — `feat`, `fix`, `test`, etc.
2. **Descrição sempre em português** — nunca misture idiomas na mensagem.
3. **Uma linha, sem ponto final** — a mensagem deve ser curta e direta.
4. **Indique o local ou feature no final** — deixe claro onde a mudança se aplica (ex.: "para mobilidade acadêmica", "em DiarioTurmaJpa", "no módulo de matrícula").
5. **Sem co-autoria automática** — não adicione trailers `Co-authored-by` gerados por ferramentas.
6. **Um commit por checkpoint lógico** — prefira commits pequenos e frequentes a um único commit gigante.

---

## Apêndice — Dependências de Implementação (Sequência crítica)

Algumas features dependem de entidades criadas por outras. Respeite esta ordem:

```
F-01 (Julia) — MatrizCurricular, Disciplina
   ↓ depende de
F-02 (Claudia) — PeriodoLetivo, JanelaAcademica
   ↓ depende de
F-03 (Clara) — Turma (precisa de PeriodoLetivoId + DisciplinaId)
   ↓ depende de
F-04 (Vinicius) — Matricula (precisa de TurmaId + MatrizCurricularId)
   ↓ depende de
F-05 (Claudia) — Aula, Frequencia, Nota (precisa de MatriculaId + TurmaId)
F-06 (Julia) — HistoricoAcademico (precisa de MatriculaId consolidada)
   ↓ depende de
F-08 (Bernardo) — IntegralizacaoCurricular (precisa de HistoricoAcademico + AtividadeComplementar)
F-09 (Jera) — AtividadeComplementar (precisa de MatrizCurricularId para categorias)
```

Features com dependências externas devem usar **mocks/stubs** nos testes enquanto o módulo dependente não estiver disponível. Defina contratos (interfaces de porta) desde o início.

---

*Última atualização: 2026-06-02 | Qualquer alteração neste documento deve ser discutida em equipe e aprovada via PR.*
