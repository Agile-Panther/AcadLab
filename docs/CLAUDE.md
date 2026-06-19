# AcadLab — Sistema de Gerenciamento Universitário

> Sistema acadêmico completo para Instituições de Ensino Superior — construído com Domain-Driven Design, Arquitetura Limpa, persistência JPA, API REST e testes BDD em Cucumber.

---

## Visão Geral

O **AcadLab** gerencia todo o ecossistema de processos de uma IES: da definição do currículo à conclusão formal do estudante. O domínio encapsula as regras que determinam quem pode se matricular, quando notas podem ser lançadas, quais disciplinas estão bloqueadas e se um estudante está apto a se formar.

O sistema se organiza em torno de três eixos:

- **Estrutura do curso:** currículo, disciplinas, pré-requisitos, carga horária — base para matrículas, históricos e colações.
- **Trajetória do estudante:** histórico acadêmico com notas, frequência, aproveitamentos e situação discente.
- **Operação do período letivo:** janelas acadêmicas que bloqueiam ou habilitam operações por data.

---

## Mapa por Integrante

Cada integrante é responsável por **2 features** e por seu padrão de projeto.

| Integrante | Features | Padrão de Projeto | Módulos |
|---|---|---|---|
| **Julia** | F-01 · F-06 | **Iterator** | `dominio-curriculo` · `dominio-historico-academico` |
| **Neto** | F-02 · F-05 | **Template Method** | `dominio-oferta-academica` · `dominio-gestao-pedagogica` |
| **Clara** | F-03 · F-14 | **Decorator** | `dominio-oferta-academica` · `dominio-estagios` |
| **Vinicius** | F-04 · F-12 | **Strategy** | `dominio-matricula` · `dominio-mobilidade-academica` |
| **Bernardo** | F-07 · F-08 | **Proxy** | `dominio-secretaria-virtual` · `dominio-integralizacao-curricular` |
| **Matheus** | F-10 · F-11 | **Observer** | `dominio-permanencia-academica` (dois bounded contexts) |
| **Jera** | F-09 · F-13 | **Observer** | `dominio-atividades-complementares` · `dominio-gestao-financeira` |

> **6 padrões distintos, 7 implementações** — Iterator, Template Method, Decorator, Strategy, Proxy e Observer (usado em dois contextos independentes), satisfazendo o requisito mínimo de 6.

---

## Padrões de Projeto — Mapa de Arquivos

| Padrão | Integrante | Contexto de Aplicação | Camada |
|---|---|---|---|
| **Iterator** | Julia | `IteradorHistorico<T>` · `IteradorRegistrosDisciplina` em `dominio-historico-academico` | Domínio |
| **Template Method** | Neto | Fluxo de gestão pedagógica: abertura → aulas → frequência → avaliações → resultado | Domínio |
| **Decorator** | Clara | `TurmaComListaEspera`, `TurmaOnline` sobre oferta de turmas; enriquecimento de oportunidades de estágio | Domínio |
| **Strategy** | Vinicius | Estratégias de validação de elegibilidade para matrícula (regular, por exceção, por mobilidade) | Domínio |
| **Proxy** | Bernardo | `SolicitacaoServicoProxy`, `IntegralizacaoServicoProxy` — controle de permissões e pré-condições | Domínio |
| **Observer** | Matheus | Notificações de benefícios de permanência e casos psicopedagógicos | Domínio |
| **Observer** | Jera | Eventos financeiros (pagamento confirmado, cobrança vencida) e deferimento de atividades complementares | Domínio |

---

## Estrutura de Módulos Maven

```
acadlab-pai/                              ← POM pai (groupId: school.cesar, v0.0.1-SNAPSHOT)
│
├── dominio-compartilhado/                ← Shared Kernel — IDs, exceções base, eventos de domínio
├── dominio-curriculo/                    ← F-01 (Julia)
├── dominio-oferta-academica/             ← F-02 (Neto) + F-03 (Clara) — dois sub-pacotes
├── dominio-matricula/                    ← F-04 (Vinicius)
├── dominio-gestao-pedagogica/            ← F-05 (Neto)
├── dominio-historico-academico/          ← F-06 (Julia)
├── dominio-secretaria-virtual/           ← F-07 (Bernardo)
├── dominio-integralizacao-curricular/    ← F-08 (Bernardo)
├── dominio-atividades-complementares/    ← F-09 (Jera)
├── dominio-permanencia-academica/        ← F-10 + F-11 (Matheus) — dois bounded contexts
├── dominio-mobilidade-academica/         ← F-12 (Vinicius)
├── dominio-gestao-financeira/            ← F-13 (Jera)
├── dominio-estagios/                     ← F-14 (Clara)
│
├── aplicacao/                            ← Casos de uso, orquestração cross-domínio, DTOs (*Resumo)
├── infraestrutura/                       ← JPA, adaptadores de persistência (*Jpa, *RepositorioImpl)
├── apresentacao-backend/                 ← Controllers REST Spring Boot (*Controlador)
└── apresentacao-frontend/                ← React + Vite
```

### Regra de Dependência

```
dominio-compartilhado  ←  todos os domínios dependem dele
       ↑
  dominio-* (isolados entre si — nunca um domínio importa outro)
       ↑
  aplicacao  (único autorizado a orquestrar entre domínios)
       ↑
  infraestrutura  (implementa repositórios, conhece JPA)
       ↑
  apresentacao-backend  (depende de aplicacao — nunca acessa domínio diretamente)
```

---

## Funcionalidades

| F | Título | Responsável | Padrão | Módulo |
|---|---|---|---|---|
| **F-01** | Gestão Curricular do Curso | Julia | Iterator | `dominio-curriculo` |
| **F-02** | Planejamento Acadêmico do Período Letivo | Neto | Template Method | `dominio-oferta-academica` |
| **F-03** | Planejamento e Oferta de Turmas | Clara | Decorator | `dominio-oferta-academica` |
| **F-04** | Montagem e Ajuste de Matrícula | Vinicius | Strategy | `dominio-matricula` |
| **F-05** | Gestão Pedagógica da Turma | Neto | Template Method | `dominio-gestao-pedagogica` |
| **F-06** | Gestão do Histórico Acadêmico | Julia | Iterator | `dominio-historico-academico` |
| **F-07** | Secretaria Virtual Acadêmica | Bernardo | Proxy | `dominio-secretaria-virtual` |
| **F-08** | Validação de Integralização e Colação | Bernardo | Proxy | `dominio-integralizacao-curricular` |
| **F-09** | Gestão de Atividades Complementares | Jera | Observer | `dominio-atividades-complementares` |
| **F-10** | Permanência Acadêmica e Bolsas | Matheus | Observer | `dominio-permanencia-academica` |
| **F-11** | Apoio Psicopedagógico | Matheus | Observer | `dominio-permanencia-academica` |
| **F-12** | Mobilidade Acadêmica | Vinicius | Strategy | `dominio-mobilidade-academica` |
| **F-13** | Gestão Financeira Acadêmica | Jera | Observer | `dominio-gestao-financeira` |
| **F-14** | Centro de Estágios e Oportunidades | Clara | Decorator | `dominio-estagios` |

---

## Bounded Contexts por Domínio

### dominio-curriculo — F-01 (Julia)

| Agregado | Responsabilidade |
|---|---|
| **MatrizCurricular** | Estrutura formal do curso: disciplinas, cargas horárias, pré-requisitos, correquisitos, equivalências. Apenas uma versão pode estar ativa por curso. |
| **ItemMatriz** | Vínculo entre disciplina e matriz, com carga horária e tipo (obrigatória/optativa). |

Features: `criar_matriz_curricular` · `configurar_prerequisitos` · `gerenciar_disciplinas_matriz` · `gerenciar_status_matriz`

---

### dominio-oferta-academica — F-02 (Neto) + F-03 (Clara)

Módulo compartilhado com sub-pacotes obrigatoriamente separados:

| Agregado | Dono | Responsabilidade |
|---|---|---|
| **PeriodoLetivo** | Neto | Semestre acadêmico com janelas que habilitam operações por data. |
| **JanelaAcademica** | Neto | Intervalo de datas dentro do período. Fora da janela, a operação é bloqueada. |
| **Turma** | Clara | Oferta de uma disciplina em um período letivo com professor, sala e horário. |
| **Sala** | Clara | Espaço físico com capacidade. |
| **Professor** | Clara | Docente vinculado a turmas. |

Features (F-02): `us01_cadastrar_periodo_letivo` · `us02_definir_janelas_academicas` · `us03_encerrar_periodo_letivo` · `us05_editar_periodo_letivo` · `us06_cancelar_periodo_letivo`

Features (F-03): `us01_gerenciar_salas` · `us02_gerenciar_professores` · `us03_ofertar_turma` · `us04_definir_professor_horario_sala` · `us_turma_decorator`

---

### dominio-matricula — F-04 (Vinicius)

| Agregado | Responsabilidade |
|---|---|
| **Matricula** | Vínculo formal do estudante com turmas de um período letivo. |
| **ItemMatricula** | Cada disciplina/turma selecionada no plano de matrícula. |

Features: `montar_plano_matricula` · `confirmar_matricula` · `ajustar_matricula` · `trancar_disciplina` · `solicitar_excecao`

---

### dominio-gestao-pedagogica — F-05 (Neto)

| Agregado | Responsabilidade |
|---|---|
| **DiarioTurma** | Registro de aulas, frequência, avaliações e resultados de todos os estudantes de uma turma. |

Features: `us01_registrar_aulas` · `us02_registrar_frequencia` · `us03_gerenciar_avaliacoes` · `us04_lancar_notas` · `us07_nota_recuperacao`

---

### dominio-historico-academico — F-06 (Julia)

| Agregado / Entidade | Responsabilidade |
|---|---|
| **HistoricoAcademico** | Raiz do agregado. Fonte de verdade da trajetória do estudante. |
| **RegistroDisciplina** | Resultado consolidado de uma turma encerrada (nota, frequência, situação). |
| **Aproveitamento** | Reconhecimento de disciplinas cursadas em outra instituição. |
| **Retificacao** | Alteração formal de uma situação acadêmica consolidada. |
| **AcompanhamentoAcademico** | Registro de acompanhamento pela secretaria. |

Serviços: `HistoricoAcademicoServico` · `ConsultaHistoricoServico` (RN-10: histórico oficial = apenas registros de períodos encerrados, acessados via Iterator)

Features: `us01_consolidar_resultados` · `us02_consultar_historico_oficial` · `us03_registrar_acompanhamento` · `us04_atualizar_situacao_discente` · `us05_registrar_aproveitamento` · `us06_retificar_resultado`

---

### dominio-secretaria-virtual — F-07 (Bernardo)

| Agregado | Responsabilidade |
|---|---|
| **SolicitacaoAcademica** | Protocolo formal de pedidos acadêmicos (histórico, declarações, revisões). |
| **Protocolo** | Identificador rastreável da solicitação. |
| **Documento** | Arquivo anexado à solicitação. |

Serviços: `SolicitacaoServico` (via `SolicitacaoServicoProxy`) · `AnaliseServico` · `ConsultaServico`

Features: `us01_abrir_solicitacao` · `us02_analisar_solicitacao` · `us03_complementar_solicitacao` · `us04_acompanhar_solicitacoes` · `us05_cancelar_solicitacao`

---

### dominio-integralizacao-curricular — F-08 (Bernardo)

| Agregado | Responsabilidade |
|---|---|
| **IntegralizacaoCurricular** | Análise de cumprimento de todos os requisitos para conclusão do curso. |
| **ColacaoDeGrau** | Registro formal da colação após aprovação da integralização. |
| **ItemChecklist** | Cada requisito verificado na análise (carga horária, optativas, atividades complementares). |

Serviços: `IntegralizacaoServico` (via `IntegralizacaoServicoProxy`) · `ColacaoServico` · `ConsultaIntegralizacaoServico`

Features: `us01_solicitar_analise` · `us02_analisar_integralizacao` · `us03_aprovar_aptidao` · `us04_registrar_colacao`

---

### dominio-atividades-complementares — F-09 (Jera)

| Agregado | Responsabilidade |
|---|---|
| **AtividadeComplementar** | Submissão e análise de horas complementares para integralização curricular. |

Features: `us01_submeter_atividade` · `us02_analisar_atividade` · `us03_solicitar_revisao` · `us04_saldo_horas` · `us05_cancelar_atividade`

---

### dominio-permanencia-academica — F-10 + F-11 (Matheus)

Dois bounded contexts no mesmo módulo Maven, separados por sub-pacote:

**Context: permanenciaacademica (F-10)**

| Agregado | Responsabilidade |
|---|---|
| **Edital** | Processo seletivo para bolsas e auxílios estudantis. |
| **Inscricao** | Candidatura de um estudante a um edital. |
| **Beneficio** | Auxílio concedido após deferimento, com ciclo de vida (ativo/suspenso/cancelado). |

Features: `criar_edital` · `inscrever` · `analisar_inscricao` · `publicar_resultado` · `beneficio` · `registrar_acao_permanencia`

**Context: apoiopsicopedagogico (F-11)**

| Agregado | Responsabilidade |
|---|---|
| **Caso** | Acompanhamento psicopedagógico individual com sigilo de atendimento. |
| **SolicitacaoApoio** | Pedido formal de abertura de caso. |
| **Triagem** | Avaliação inicial de prioridade pelo psicopedagogo. |
| **Atendimento** | Sessão de acompanhamento registrada no caso. |

Features: `solicitar_apoio` · `realizar_triagem` · `registrar_atendimento` · `consultar_casos` · `encerrar_caso`

---

### dominio-mobilidade-academica — F-12 (Vinicius)

| Agregado | Responsabilidade |
|---|---|
| **MobilidadeAcademica** | Programa de intercâmbio do estudante em instituição externa. |
| **ItemPlanoEstudos** | Disciplina cursada externamente com equivalência na matriz. |

Features: `solicitar_mobilidade` · `analisar_plano_estudos` · `registrar_resultado_mobilidade` · `cancelar_mobilidade`

---

### dominio-gestao-financeira — F-13 (Jera)

| Agregado | Responsabilidade |
|---|---|
| **Cobranca** | Cobrança gerada para o estudante (mensalidade, taxa). |
| **Pagamento** | Registro de quitação de uma cobrança. |
| **Contestacao** | Impugnação formal de uma cobrança pelo estudante. |
| **AplicacaoDesconto** | Desconto ou bolsa aplicado sobre uma cobrança. |

Features: `us01_contestar_cobranca` · `us02_gerar_cobranca` · `us03_aplicar_desconto` · `us04_registrar_pagamento` · `us05_extrato_comprovante` · `us07_cancelar_pagamento`

---

### dominio-estagios — F-14 (Clara)

| Agregado | Responsabilidade |
|---|---|
| **Oportunidade** | Vaga publicada por uma empresa parceira. |
| **Estagio** | Vínculo formalizado entre estudante e empresa. |
| **Relatorio** | Relatório de atividades submetido pelo estagiário. |

Features: `candidatar_oportunidade` · `confirmar_candidatura` · `encerrar_estagio` · `submeter_relatorio`

---

## API REST — Endpoints por Controlador

| Controlador | Base Path | Operações principais |
|---|---|---|
| `MatriculaControlador` | `backend/matriculas` | `GET /{id}` · `POST` · `POST /{id}/itens` · `PUT /{id}/confirmar` · `PUT /{id}/itens/{turmaId}/trancar` · `POST /{id}/excecoes` |
| `DiarioTurmaControlador` | `backend/diarios` | `GET turma/{turmaId}` · `POST` · `POST {id}/aulas` · `POST {id}/frequencias` · `POST {id}/estudantes/{eId}/notas/{avId}` · `POST {id}/estudantes/{eId}/fechar` |
| `PeriodoLetivoControlador` | `backend/periodos-letivos` | `GET curso/{cursoId}` · `POST` · `POST {id}/janela` · `POST {id}/iniciar` · `POST {id}/encerrar` · `PUT {id}` · `DELETE {id}/cancelar` |
| `MatrizCurricularControlador` | `backend/curriculo` | `GET curso/{cursoId}` · `GET {id}` · `POST` · `POST {id}/disciplinas` · `PUT {id}/prerequisitos` · `PUT {id}/ativar` · `PUT {id}/desativar` |
| `AtividadeComplementarControlador` | `backend/atividades-complementares` | `GET estudante/{eId}` · `GET estudante/{eId}/saldo` · `POST submeter` · `POST {id}/deferir` · `POST {id}/indeferir` · `POST {id}/solicitar-revisao` |
| `HistoricoAcademicoControlador` | `backend/historicos` | `GET {id}` · `GET estudante/{eId}` · `GET estudante/{eId}/oficial` · `POST` · `POST {id}/registros` · `PUT {id}/situacao` · `POST {id}/aproveitamentos` · `PUT {id}/registros/{rId}/retificar` |
| `SolicitacaoAcademicaControlador` | `backend/solicitacoes` | `GET estudante/{eId}` · `GET {id}` · `GET pendentes` · `POST` · `PUT {id}/complementar` · `PUT {id}/cancelar` · `PUT {id}/deferir` · `PUT {id}/indeferir` |
| `ApoioPsicopedagogicoControlador` | `backend/apoio` | `GET casos/{id}` · `GET casos` · `GET estudantes/{eId}/caso-ativo` · `POST solicitacoes` · `POST casos/{cId}/triagem` · `POST casos/{cId}/atendimentos` · `PUT casos/{cId}/encerrar` |
| `MobilidadeAcademicaControlador` | `backend/mobilidades` | `GET estudante/{eId}` · `GET {id}` · `POST` · `PUT {id}/autorizar` · `PUT {id}/iniciar` · `POST {id}/plano` · `PUT {id}/plano/{dId}/resultado` · `POST {id}/cancelamento` |
| `PermanenciaAcademicaControlador` | `backend/permanencia` | `GET editais` · `POST editais` · `PUT editais/{id}/resultado` · `POST editais/{eId}/inscricoes` · `PUT inscricoes/{id}/deferir` · `GET estudantes/{eId}/beneficios` · `PUT beneficios/{id}/suspender` |
| `IntegralizacaoControlador` | `backend/integralizacoes` | Análise de aptidão, registro de colação |
| `CobrancaControlador` | `backend/gestao-financeira` | CRUD de cobranças, pagamentos, descontos, contestações |
| `EstagioControlador` | `backend/estagios` | Gestão do ciclo de vida de estágios |
| `OportunidadeControlador` | `backend/oportunidades` | Publicação e candidatura a vagas |
| `TurmaControlador` | `backend/oferta-academica` | Oferta e gestão de turmas |

---

## Camada de Aplicação

A camada `aplicacao` expõe **serviços de leitura** (`*ServicoAplicacao`) e **DTOs** (`*Resumo`) para os controladores, desacoplando o domínio da apresentação.

| Serviço de Aplicação | DTO correspondente |
|---|---|
| `HistoricoAcademicoServicoAplicacao` | `HistoricoAcademicoResumo` · `RegistroDisciplinaResumo` · `AproveitamentoResumo` · `AcompanhamentoResumo` |
| `MatriculaServicoAplicacao` | `MatriculaResumo` |
| `PeriodoLetivoServicoAplicacao` | `PeriodoLetivoResumo` |
| `DiarioTurmaServicoAplicacao` | `DiarioTurmaResumo` |
| `MobilidadeAcademicaServicoAplicacao` | `MobilidadeAcademicaResumo` |
| `IntegralizacaoServicoAplicacao` | `IntegralizacaoResumo` · `ItemChecklistResumo` · `ColacaoResumo` |
| `SolicitacaoAcademicaServicoAplicacao` | `SolicitacaoAcademicaResumo` · `DocumentoResumo` |
| `EstagioServicoAplicacao` | `EstagioResumo` |

**Padrão dual-interface no repositório JPA:** cada `*RepositorioImpl` em `infraestrutura` implementa simultaneamente a interface de domínio (`*Repositorio`) e a interface de leitura da camada de aplicação (`*RepositorioAplicacao`).

---

## Testes BDD

Todo domínio é coberto por testes comportamentais em **português** com Cucumber + JUnit 6.

```bash
mvn test
```

### Convenções de arquivo

| Item | Padrão |
|---|---|
| Feature file | `us{NN}_{nome-kebab}.feature` em `src/test/resources/school/cesar/acadlab/dominio/{contexto}/` |
| Header obrigatório | `#language: pt` na primeira linha |
| Keywords | `Funcionalidade` · `Cenário` · `Dado` · `Quando` · `Então` · `E` · `Mas` |
| Step definitions | Classe `{Contexto}Funcionalidade.java` no mesmo pacote base do módulo |
| Runner | `RunCucumberTest.java` com `@Suite` · `@IncludeEngines("cucumber")` · `@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty")` |

### Mapeamento Feature → Módulo

| Feature | Módulo | Responsável |
|---|---|---|
| `criar_matriz_curricular` · `configurar_prerequisitos` · `gerenciar_status_matriz` | `dominio-curriculo` | Julia |
| `us01_cadastrar_periodo_letivo` … `us06_cancelar_periodo_letivo` | `dominio-oferta-academica` | Neto |
| `us01_gerenciar_salas` … `us_turma_decorator` | `dominio-oferta-academica` | Clara |
| `montar_plano_matricula` … `solicitar_excecao` | `dominio-matricula` | Vinicius |
| `us01_registrar_aulas` … `us07_nota_recuperacao` | `dominio-gestao-pedagogica` | Neto |
| `us01_consolidar_resultados` … `us06_retificar_resultado` | `dominio-historico-academico` | Julia |
| `us01_abrir_solicitacao` … `us05_cancelar_solicitacao` | `dominio-secretaria-virtual` | Bernardo |
| `us01_solicitar_analise` … `us04_registrar_colacao` | `dominio-integralizacao-curricular` | Bernardo |
| `us01_submeter_atividade` … `us05_cancelar_atividade` | `dominio-atividades-complementares` | Jera |
| `criar_edital` · `inscrever` · `beneficio` · `registrar_acao_permanencia` | `dominio-permanencia-academica` | Matheus |
| `solicitar_apoio` · `realizar_triagem` · `registrar_atendimento` · `encerrar_caso` | `dominio-permanencia-academica` | Matheus |
| `solicitar_mobilidade` … `cancelar_mobilidade` | `dominio-mobilidade-academica` | Vinicius |
| `us01_contestar_cobranca` … `us07_cancelar_pagamento` | `dominio-gestao-financeira` | Jera |
| `candidatar_oportunidade` … `submeter_relatorio` | `dominio-estagios` | Clara |

---

## Arquitetura

```
┌──────────────────────────────────────────────────────────────────┐
│   apresentacao-backend (Spring Boot REST)                        │
│   apresentacao-frontend (React + Vite)                           │
└────────────────────────────┬─────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────┐
│                       aplicacao                                  │
│   *ServicoAplicacao · *Resumo (DTOs) · *RepositorioAplicacao     │
└────────────────────────────┬─────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────┐
│                     infraestrutura                               │
│   *RepositorioImpl (dual-interface JPA) · *Jpa (@Entity)         │
│   EventoBarramento · package-info.java                           │
└────────────────────────────┬─────────────────────────────────────┘
                             │
┌────────────────────────────▼─────────────────────────────────────┐
│                     dominio-*  (Java puro)                       │
│   Agregados · Value Objects · *Repositorio (interface)           │
│   *Servico · Padrões de projeto · Eventos de domínio             │
└──────────────────────────────────────────────────────────────────┘
                             ↑
┌────────────────────────────┴─────────────────────────────────────┐
│                  dominio-compartilhado                           │
│   EstudanteId · CursoId · EventoDominio · EventoObservador       │
└──────────────────────────────────────────────────────────────────┘
```

**Regras críticas:**
- O domínio não conhece Spring, JPA ou qualquer framework.
- Entidades `@Entity` existem **apenas** em `infraestrutura`.
- Um módulo de domínio nunca importa outro módulo de domínio — apenas `dominio-compartilhado`.
- Consultas cross-domínio são implementadas como portas (interfaces) no domínio e ligadas em `aplicacao`.

---

## Convenções de Código

### IDs de Value Objects

| Domínio | Getter do valor interno |
|---|---|
| `dominio-historico-academico` | `.getId()` |
| `dominio-estagios`, `dominio-mobilidade-academica` | `.getValor()` |

### Validações no domínio

Use `org.apache.commons.lang3.Validate`:
- `notNull(valor, "mensagem")` — para objetos obrigatórios
- `notBlank(string, "mensagem")` — para strings
- `isTrue(condicao, "RN-X: mensagem")` — para regras de negócio

### Beans em `BackendAplicacao`

Serviços de domínio e de aplicação são declarados como `@Bean` em `BackendAplicacao.java` (apresentacao-backend), **não** anotados com `@Service` nos domínios.

---

## Padrão de Commits

Formato: `<tipo>(<escopo-opcional>): <descrição em português>`

| Tipo | Quando usar |
|---|---|
| `feat` | Nova funcionalidade ou código de produção |
| `fix` | Correção de bug |
| `test` | Testes BDD, unitários, integração |
| `refactor` | Refatoração sem mudança de comportamento |
| `docs` | Documentação |
| `chore` | Dependências, build, configuração |
| `style` | Formatação sem mudança de lógica |

**Exemplos:**
```
feat(historico): implementa US02 - consulta de histórico oficial (RN-10)
fix(historico): corrige queries JPQL de próximo ID para coleções vazias
test(matricula): adiciona testes BDD para ajuste e trancamento de matrícula
chore(infraestrutura): adiciona package-info.java ao pacote JPA
```

**Regras:**
- Prefixo em inglês, descrição em português
- Uma linha, sem ponto final
- Sem `Co-authored-by` de ferramentas
- Um commit por checkpoint lógico

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework Web | Spring Boot 4.0.5 |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | PostgreSQL (Docker) |
| Testes BDD | Cucumber 7.34.3 + JUnit 6.0.3 + Mockito 5.23.0 |
| Build | Maven multi-módulo (`acadlab-pai`) |
| Utilitários | Apache Commons Lang3 (`Validate`) |
| Container | JIB Maven Plugin 3.5.1 (imagem `eclipse-temurin:17-jre`) |
| Frontend | React + Vite |

---

## Como Rodar

**Pré-requisitos:** Java 17+, Maven 3.8+, Docker

```bash
# 1. Build de todos os módulos
mvn install -DskipTests

# 2. Subir o backend
cd apresentacao-backend
mvn spring-boot:run

# 3. Executar todos os testes BDD
mvn test
```

> Para desenvolvimento local, crie `apresentacao-backend/src/main/resources/application-desenvolvimento.properties` apontando para o banco local e use `BackendDesenvolvimentoAplicacao` como entry point (perfil `desenvolvimento`).

---

## Equipe

| Integrante | Features | Padrão |
|---|---|---|
| Julia Teixeira | F-01 (Gestão Curricular) · F-06 (Histórico Acadêmico) | Iterator |
| Matheus Veríssimo | F-10 (Permanência Acadêmica) · F-11 (Apoio Psicopedagógico) | Observer |
| Vinicius | F-04 (Matrícula) · F-12 (Mobilidade Acadêmica) | Strategy |
| Bernardo | F-07 (Secretaria Virtual) · F-08 (Integralização e Colação) | Proxy |
| Clara | F-03 (Oferta de Turmas) · F-14 (Estágios) | Decorator |
| Neto | F-02 (Período Letivo) · F-05 (Gestão Pedagógica) | Template Method |
| Jera | F-09 (Atividades Complementares) · F-13 (Gestão Financeira) | Observer |

**Professor:** Saulo Meira Araujo — Disciplina de Requisitos, Projeto de Software e Validação · CESAR School

---

*Projeto acadêmico — CESAR School, 2026.1*
