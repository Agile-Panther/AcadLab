# AcadLab — Sistema de Gerenciamento Universitário

> Sistema acadêmico completo para Instituições de Ensino Superior — da estrutura curricular à conclusão formal do estudante — construído com Domain-Driven Design, Arquitetura Limpa, persistência JPA, API REST e testes BDD em Cucumber.

---

## Para o professor

As seções [Mapa por Integrante](#mapa-por-integrante) e [Padrões de Projeto — Mapa de Arquivos](#padrões-de-projeto--mapa-de-arquivos) foram criadas para localizar rapidamente, por aluno, os arquivos de cada funcionalidade e os arquivos onde cada padrão de projeto está implementado.

---

## Visão Geral

O AcadLab gerencia o ciclo de vida acadêmico de uma IES: da definição do currículo à colação de grau. O domínio encapsula as regras que determinam quem pode se matricular, quando notas podem ser lançadas, quais disciplinas estão bloqueadas e se um estudante está apto a se formar. Cada operação é governada por essas regras e pelo momento em que é realizada.

O sistema se organiza em três eixos:

- **Estrutura do curso** — o currículo define quais disciplinas existem, a ordem em que devem ser cursadas e a carga horária necessária para a conclusão.
- **Trajetória do estudante** — registrada no histórico acadêmico com notas, frequência, aproveitamentos e situação discente; é a fonte de verdade do sistema.
- **Operação do período letivo** — janelas acadêmicas habilitam ou bloqueiam operações (matrícula, lançamento de notas, ajustes) por intervalo de datas.

---

## Mapa por Integrante

| Integrante | Funcionalidades | Padrão de Projeto | Módulo(s) |
|---|---|---|---|
| **Julia** | F-01 · Gestão Curricular · F-06 · Histórico Acadêmico | Iterator | `dominio-curriculo` · `dominio-historico-academico` |
| **Neto** | F-02 · Período Letivo · F-05 · Gestão Pedagógica | Template Method | `dominio-oferta-academica` · `dominio-gestao-pedagogica` |
| **Clara** | F-03 · Oferta de Turmas · F-14 · Estágios | Decorator | `dominio-oferta-academica` · `dominio-estagios` |
| **Vinicius** | F-04 · Matrícula · F-12 · Mobilidade Acadêmica | Strategy | `dominio-matricula` · `dominio-mobilidade-academica` |
| **Bernardo** | F-07 · Secretaria Virtual · F-08 · Integralização | Proxy | `dominio-secretaria-virtual` · `dominio-integralizacao-curricular` |
| **Matheus** | F-10 · Permanência Acadêmica · F-11 · Apoio Psicopedagógico | Observer | `dominio-permanencia-academica` |
| **Jera** | F-09 · Atividades Complementares · F-13 · Gestão Financeira | Observer | `dominio-atividades-complementares` · `dominio-gestao-financeira` |

---

## Padrões de Projeto — Mapa de Arquivos

São **6 padrões distintos** implementados. Observer aparece em dois contextos independentes, totalizando 7 implementações.

### Iterator — Julia Teixeira

Percorre os registros do histórico acadêmico sem expor a estrutura interna da coleção.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface do iterador | `dominio-historico-academico/.../iterador/IteradorHistorico.java` |
| Iterador concreto | `dominio-historico-academico/.../iterador/IteradorRegistrosDisciplina.java` |
| Agregado iterável | `dominio-historico-academico/.../historico/HistoricoAcademico.java` |
| Serviço consumidor | `dominio-historico-academico/.../ConsultaHistoricoServico.java` |

### Template Method — Neto

Define o esqueleto do fluxo pedagógico — abertura → aulas → frequência → avaliações → resultado — fixando a sequência no serviço base e delegando os passos específicos ao diário de turma.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Template (sequência fixa de passos) | `dominio-gestao-pedagogica/.../DiarioTurmaServico.java` |
| Entidade com operações de cada passo | `dominio-gestao-pedagogica/.../diario/DiarioTurma.java` |

### Decorator — Clara

Adiciona comportamentos dinâmicos à Turma — lista de espera e modalidade online — sem alterar a classe base, através de wrappers que estendem a interface `TurmaOferecida`.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface componente | `dominio-oferta-academica/.../turma/decorator/TurmaOferecida.java` |
| Decorador abstrato | `dominio-oferta-academica/.../turma/decorator/TurmaDecorador.java` |
| Decorador concreto: lista de espera | `dominio-oferta-academica/.../turma/decorator/TurmaComListaEspera.java` |
| Decorador concreto: turma online | `dominio-oferta-academica/.../turma/decorator/TurmaOnline.java` |
| Serviço que usa os decoradores | `dominio-oferta-academica/.../OfertaTurmaServico.java` |

### Strategy — Vinicius

Encapsula as regras de elegibilidade para matrícula em estratégias intercambiáveis, permitindo trocar a política de validação sem modificar o serviço principal.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface da estratégia | `dominio-matricula/.../matricula/EstrategiaMatricula.java` |
| Estratégia concreta: matrícula regular | `dominio-matricula/.../matricula/ValidacaoRegular.java` |
| Estratégia concreta: matrícula por exceção | `dominio-matricula/.../matricula/ValidacaoExcecao.java` |
| Contexto (usa a estratégia) | `dominio-matricula/.../MatriculaServico.java` |

### Proxy — Bernardo

Interpõe uma camada de pré-condições e controle de acesso antes de delegar às operações reais, tanto na secretaria virtual quanto na integralização curricular.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface sujeito — secretaria | `dominio-secretaria-virtual/.../SolicitacaoServico.java` |
| Proxy — secretaria | `dominio-secretaria-virtual/.../SolicitacaoServicoProxy.java` |
| Sujeito real — secretaria | `dominio-secretaria-virtual/.../SolicitacaoServicoReal.java` |
| Interface sujeito — integralização | `dominio-integralizacao-curricular/.../IntegralizacaoOperacoes.java` |
| Proxy — integralização | `dominio-integralizacao-curricular/.../IntegralizacaoServicoProxy.java` |
| Sujeito real — integralização | `dominio-integralizacao-curricular/.../IntegralizacaoServico.java` |

### Observer — Matheus Veríssimo

Notifica automaticamente outros componentes quando o estado de benefícios de permanência ou de casos psicopedagógicos muda, via barramento de eventos do shared kernel.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface publicador de eventos | `dominio-compartilhado/.../evento/EventoBarramento.java` |
| Interface observador (subscriber) | `dominio-compartilhado/.../evento/EventoObservador.java` |
| Publisher: benefícios de permanência | `dominio-permanencia-academica/.../permanenciaacademica/BeneficioServico.java` |
| Publisher: apoio psicopedagógico | `dominio-permanencia-academica/.../apoiopsicopedagogico/ApoioServico.java` |

### Observer — Jera

Notifica outros contextos quando cobranças são geradas ou liquidadas e quando atividades complementares são deferidas, via o mesmo barramento de eventos do shared kernel.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface publicador de eventos | `dominio-compartilhado/.../evento/EventoBarramento.java` |
| Interface observador (subscriber) | `dominio-compartilhado/.../evento/EventoObservador.java` |
| Publisher: gestão financeira | `dominio-gestao-financeira/.../CobrancaServico.java` |
| Publisher: atividades complementares | `dominio-atividades-complementares/.../AtividadeComplementarServico.java` |

---

## Funcionalidades

| F | Funcionalidade | Responsável | Padrão | Resumo |
|---|---|---|---|---|
| **F-01** | Gestão Curricular do Curso | Julia | Iterator | Criar e versionar matriz curricular com disciplinas, pré-requisitos, correquisitos e ciclo de vida ativo/inativo |
| **F-02** | Planejamento do Período Letivo | Neto | Template Method | Definir semestre acadêmico com janelas que habilitam ou bloqueiam operações por intervalo de datas |
| **F-03** | Oferta de Turmas | Clara | Decorator | Configurar turmas com professor, sala e horário; adicionar comportamentos dinâmicos sem alterar a classe base |
| **F-04** | Montagem e Ajuste de Matrícula | Vinicius | Strategy | Plano de matrícula, confirmação, ajustes e trancamento com validação de elegibilidade por estratégia intercambiável |
| **F-05** | Gestão Pedagógica da Turma | Neto | Template Method | Diário de turma: registro de aulas, frequência, avaliações, notas e resultado final com recuperação |
| **F-06** | Gestão do Histórico Acadêmico | Julia | Iterator | Consolidação de resultados, aproveitamentos externos, retificações e acompanhamentos; histórico oficial via Iterator |
| **F-07** | Secretaria Virtual Acadêmica | Bernardo | Proxy | Abertura e tramitação de protocolos acadêmicos com controle de permissões via Proxy |
| **F-08** | Validação de Integralização e Colação | Bernardo | Proxy | Análise de cumprimento de requisitos curriculares e registro formal da colação de grau |
| **F-09** | Atividades Complementares | Jera | Observer | Submissão e análise de horas extracurriculares; eventos de deferimento notificam outros contextos |
| **F-10** | Permanência Acadêmica e Bolsas | Matheus | Observer | Editais, inscrições, análise, gestão de benefícios e registro de ações de permanência com notificação automática de mudanças de estado |
| **F-11** | Apoio Psicopedagógico | Matheus | Observer | Solicitação, triagem, atendimentos e encerramento de caso com sigilo de atendimento |
| **F-12** | Mobilidade Acadêmica | Vinicius | Strategy | Intercâmbio externo com plano de estudos, equivalências e registro de resultados no histórico |
| **F-13** | Gestão Financeira Acadêmica | Jera | Observer | Cobranças, pagamentos, descontos, contestações e comprovantes; eventos de pagamento notificam outros contextos |
| **F-14** | Centro de Estágios e Oportunidades | Clara | Decorator | Publicação de vagas, candidatura, formalização do estágio e entrega de relatórios |

<details>
<summary><strong>Regras de negócio selecionadas por funcionalidade</strong></summary>

### F-01 — Gestão Curricular (Julia)
- A mesma disciplina não pode aparecer mais de uma vez na mesma matriz curricular
- A matriz só pode ser ativada se a carga horária e os créditos das disciplinas forem suficientes para o mínimo exigido
- Apenas uma versão da matriz pode estar ativa por curso ao mesmo tempo
- Pré-requisitos cíclicos são rejeitados — o sistema percorre o grafo e detecta caminhos circulares
- Correquisitos devem pertencer à mesma matriz curricular

### F-02 — Período Letivo (Neto)
- Janela acadêmica define o intervalo em que uma operação é permitida; fora da janela, a operação é bloqueada
- O período encerrado torna os registros imutáveis sem reabertura formal autorizada

### F-04 — Matrícula (Vinicius)
- O plano de matrícula é provisório — não gera vínculo acadêmico até a confirmação
- A elegibilidade é verificada por estratégia (regular, exceção, mobilidade), sem alterar o serviço principal

### F-05 — Gestão Pedagógica (Neto)
- Reprovação por frequência é aplicada quando a presença fica abaixo do mínimo, independentemente da média

### F-06 — Histórico Acadêmico (Julia)
- Apenas resultados de turmas encerradas podem ser consolidados (RN-1)
- O histórico oficial retorna somente registros consolidados de períodos encerrados, via Iterator (RN-10)
- Retificação exige responsável, justificativa e registra trilha de auditoria

### F-11 — Apoio Psicopedagógico (Matheus)
- Sigilo de atendimento: apenas o psicopedagogo e o próprio estudante acessam dados individuais de sessão
- Estudante com caso ativo não pode abrir nova solicitação

</details>

---

## Estrutura de Módulos

```
acadlab-pai/
│
├── dominio-compartilhado/            ← Shared Kernel — EstudanteId, CursoId, eventos de domínio
├── dominio-curriculo/                ← F-01 (Julia)
├── dominio-oferta-academica/         ← F-02 (Neto) + F-03 (Clara) — sub-pacotes separados
├── dominio-matricula/                ← F-04 (Vinicius)
├── dominio-gestao-pedagogica/        ← F-05 (Neto)
├── dominio-historico-academico/      ← F-06 (Julia)
├── dominio-secretaria-virtual/       ← F-07 (Bernardo)
├── dominio-integralizacao-curricular/← F-08 (Bernardo)
├── dominio-atividades-complementares/← F-09 (Jera)
├── dominio-permanencia-academica/    ← F-10 + F-11 (Matheus) — dois bounded contexts
├── dominio-mobilidade-academica/     ← F-12 (Vinicius)
├── dominio-gestao-financeira/        ← F-13 (Jera)
├── dominio-estagios/                 ← F-14 (Clara)
│
├── aplicacao/                        ← Orquestração cross-domínio e DTOs (*Resumo)
├── infraestrutura/                   ← JPA, adaptadores de persistência
├── apresentacao-backend/             ← Controllers REST (Spring Boot)
└── apresentacao-frontend/            ← React + Vite
```

> Os módulos `dominio-*` não possuem dependências de framework — apenas Java puro. Nenhum módulo de domínio importa outro: comunicação cross-domínio ocorre exclusivamente via camada `aplicacao`.

---

## Atores do Sistema

| Ator | Papel |
|---|---|
| **Estudante** | Sujeito central. Realiza matrículas, recebe avaliações, acumula histórico e progride até a conclusão. |
| **Professor** | Conduz a turma. Registra aulas, frequência, avaliações e resultado final dos estudantes. |
| **Coordenador Acadêmico** | Define e mantém o currículo. Autoriza exceções e libera estudantes para colação. |
| **Secretaria Acadêmica** | Opera os processos formais: matrículas, períodos letivos, protocolos e integralização. |
| **Setor Financeiro** | Gerencia cobranças, descontos, bolsas e comprovantes de pagamento. |
| **Assistência Estudantil** | Cuida dos programas de permanência e suporte ao estudante em vulnerabilidade. |
| **Psicopedagogo** | Realiza triagem, agendamento e acompanhamento psicopedagógico. |
| **Empresa Parceira** | Publica oportunidades de estágio e vagas profissionais. |

---

## Testes BDD

Todo o domínio é coberto por testes comportamentais em **português** com Cucumber + JUnit 6.

```bash
mvn test
```

### Mapeamento: Funcionalidade → Features BDD

| Funcionalidade | Features |
|---|---|
| F-01 · Gestão Curricular | `criar_matriz_curricular` · `configurar_prerequisitos` · `gerenciar_disciplinas_matriz` · `gerenciar_status_matriz` |
| F-02 · Período Letivo | `us01_cadastrar_periodo_letivo` · `us02_definir_janelas_academicas` · `us03_encerrar_periodo_letivo` · `us05_editar_periodo_letivo` · `us06_cancelar_periodo_letivo` |
| F-03 · Oferta de Turmas | `us01_gerenciar_salas` · `us02_gerenciar_professores` · `us03_ofertar_turma` · `us04_definir_professor_horario_sala` · `us_turma_decorator` |
| F-04 · Matrícula | `montar_plano_matricula` · `confirmar_matricula` · `ajustar_matricula` · `trancar_disciplina` · `solicitar_excecao` |
| F-05 · Gestão Pedagógica | `us01_registrar_aulas` · `us02_registrar_frequencia` · `us03_gerenciar_avaliacoes` · `us04_lancar_notas` · `us07_nota_recuperacao` |
| F-06 · Histórico Acadêmico | `us01_consolidar_resultados` · `us02_consultar_historico_oficial` · `us03_registrar_acompanhamento` · `us04_atualizar_situacao_discente` · `us05_registrar_aproveitamento` · `us06_retificar_resultado` |
| F-07 · Secretaria Virtual | `us01_abrir_solicitacao` · `us02_analisar_solicitacao` · `us03_complementar_solicitacao` · `us04_acompanhar_solicitacoes` · `us05_cancelar_solicitacao` |
| F-08 · Integralização | `us01_solicitar_analise` · `us02_analisar_integralizacao` · `us03_aprovar_aptidao` · `us04_registrar_colacao` |
| F-09 · Atividades Complementares | `us01_submeter_atividade` · `us02_analisar_atividade` · `us03_solicitar_revisao` · `us04_saldo_horas` · `us05_cancelar_atividade` |
| F-10 · Permanência Acadêmica | `criar_edital` · `inscrever` · `analisar_inscricao` · `publicar_resultado` · `beneficio` · `registrar_acao_permanencia` |
| F-11 · Apoio Psicopedagógico | `solicitar_apoio` · `realizar_triagem` · `registrar_atendimento` · `consultar_casos` · `encerrar_caso` |
| F-12 · Mobilidade Acadêmica | `solicitar_mobilidade` · `analisar_plano_estudos` · `registrar_resultado_mobilidade` · `cancelar_mobilidade` |
| F-13 · Gestão Financeira | `us01_contestar_cobranca` · `us02_gerar_cobranca` · `us03_aplicar_desconto` · `us04_registrar_pagamento` · `us05_extrato_comprovante` · `us07_cancelar_pagamento` |
| F-14 · Estágios | `candidatar_oportunidade` · `confirmar_candidatura` · `encerrar_estagio` · `submeter_relatorio` |

---

## Arquitetura

O projeto segue **Arquitetura Limpa** com módulos Maven separados por camada:

```
┌──────────────────────────────────────────────────────────┐
│  apresentacao-backend (Spring Boot REST)                 │
│  apresentacao-frontend (React + Vite)                    │
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                    infraestrutura                        │  ← JPA, @Entity, *RepositorioImpl
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│                     aplicacao                            │  ← *ServicoAplicacao, DTOs (*Resumo)
└────────────────────────┬─────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────────┐
│  dominio-curriculo  dominio-matricula  dominio-historico  │
│  dominio-oferta-academica  dominio-gestao-pedagogica      │  ← Java puro, sem framework
│  dominio-secretaria-virtual  dominio-integralizacao       │
│  dominio-permanencia-academica  dominio-estagios  ...     │
└──────────────────────────────────────────────────────────┘
                         ↑
┌────────────────────────┴─────────────────────────────────┐
│                dominio-compartilhado                     │  ← Shared Kernel (IDs, eventos)
└──────────────────────────────────────────────────────────┘
```

**Regra de dependência:** camadas externas dependem das internas. Entidades `@Entity` ficam exclusivamente em `infraestrutura`. Módulos de domínio não se importam entre si — cross-domain só via `aplicacao`.

---

## Linguagem Onipresente

Termos com significado fixo no projeto — sem variações ou sinônimos informais:

| ✅ Usar | ❌ Evitar | Motivo |
|---|---|---|
| **Estudante** | Aluno | Linguagem ubíqua do domínio |
| **Matriz Curricular** | Grade curricular | Precisão do conceito |
| **Situação** (discente, da disciplina) | Status (genérico) | Sempre qualificar |
| **Histórico Acadêmico** | Histórico (genérico) | Refere-se à trajetória formal |
| **Deferimento / Aptidão para colação** | Aprovação (genérico) | Contexto sempre qualificado |
| **Matrícula Institucional** (ID) vs **Matrícula** (vínculo com turma) | Matrícula (genérico) | São conceitos distintos |

---

## Tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| Framework Web | Spring Boot 4.0.5 |
| Persistência | Spring Data JPA / Hibernate |
| Banco de dados | PostgreSQL (Docker) |
| Testes BDD | Cucumber 7.34.3 + JUnit 6.0.3 + Mockito 5.23.0 |
| Build | Maven multi-módulo |
| Utilitários | Apache Commons Lang3 |
| Containerização | JIB Maven Plugin 3.5.1 (`eclipse-temurin:17-jre`) |
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

> Para desenvolvimento local, crie `apresentacao-backend/src/main/resources/application-desenvolvimento.properties` com a URL do banco local e use `BackendDesenvolvimentoAplicacao` como entry point (perfil `desenvolvimento`).

---

## Equipe

| Integrante | Funcionalidades | Padrão |
|---|---|---|
| Julia Teixeira | F-01 · Gestão Curricular · F-06 · Histórico Acadêmico | Iterator |
| Matheus Veríssimo | F-10 · Permanência Acadêmica · F-11 · Apoio Psicopedagógico | Observer |
| Vinicius | F-04 · Matrícula · F-12 · Mobilidade Acadêmica | Strategy |
| Bernardo | F-07 · Secretaria Virtual · F-08 · Integralização e Colação | Proxy |
| Clara | F-03 · Oferta de Turmas · F-14 · Estágios | Decorator |
| Neto | F-02 · Período Letivo · F-05 · Gestão Pedagógica | Template Method |
| Jera | F-09 · Atividades Complementares · F-13 · Gestão Financeira | Observer |

**Professor:** Saulo Meira Araujo — Disciplina de Requisitos, Projeto de Software e Validação · CESAR School

---

*Projeto acadêmico — CESAR School, 2026.1*
