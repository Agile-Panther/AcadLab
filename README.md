# AcadLab — Sistema de Gerenciamento Universitário

> Sistema acadêmico completo para Instituições de Ensino Superior — da estrutura curricular à conclusão formal do estudante — construído com Domain-Driven Design, Arquitetura Limpa, persistência JPA, API REST, testes BDD em Cucumber e frontend React completamente integrado ao backend.

---

## Para o professor

As seções [Mapa por Integrante](#mapa-por-integrante) e [Padrões de Projeto — Mapa de Arquivos](#padrões-de-projeto--mapa-de-arquivos) foram criadas para localizar rapidamente, por aluno, os arquivos de cada funcionalidade e os arquivos onde cada padrão de projeto está implementado.

O mapa de histórias do usuário está disponível no Miro: [User Story Map — AcadLab](https://miro.com/app/board/uXjVHD2I-jQ=/?share_link_id=491267891599)

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
| **Julia Torres** | F-01 · Gestão Curricular · F-06 · Histórico Acadêmico | Iterator | `dominio-curriculo` · `dominio-historico-academico` |
| **Maria Claudia** | F-02 · Período Letivo · F-05 · Gestão Pedagógica | Template Method | `dominio-oferta-academica` · `dominio-gestao-pedagogica` |
| **Maria Clara** | F-03 · Oferta de Turmas · F-14 · Estágios | Decorator | `dominio-oferta-academica` · `dominio-estagios` |
| **Vinicius** | F-04 · Matrícula · F-12 · Mobilidade Acadêmica | Strategy | `dominio-matricula` · `dominio-mobilidade-academica` |
| **Bernardo** | F-07 · Secretaria Virtual · F-08 · Integralização | Proxy | `dominio-secretaria-virtual` · `dominio-integralizacao-curricular` |
| **Matheus** | F-10 · Permanência Acadêmica · F-11 · Apoio Psicopedagógico | Observer | `dominio-permanencia-academica` · `dominio-apoio-psicopedagogico` |
| **Jera** | F-09 · Atividades Complementares · F-13 · Gestão Financeira | Observer | `dominio-atividades-complementares` · `dominio-gestao-financeira` |

---

## Padrões de Projeto — Mapa de Arquivos

São **6 padrões distintos** implementados. Observer aparece em dois contextos independentes (Matheus e Jera), totalizando 7 implementações. Decorator também aparece em dois contextos (Turma e Oportunidade).

O prefixo `src/main/java/school/cesar/acadlab/` é omitido nos caminhos abaixo para brevidade; todos os arquivos partem dessa raiz dentro do respectivo módulo Maven.

---

### Iterator — Julia Torres

**Intenção:** fornecer uma forma de percorrer os registros do histórico acadêmico sem expor a estrutura interna da coleção (`List<RegistroDisciplina>`).

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface do iterador | `dominio-historico-academico/dominio/historicoacademico/iterador/IteradorHistorico.java` |
| Iterador concreto | `dominio-historico-academico/dominio/historicoacademico/iterador/IteradorRegistrosDisciplina.java` |
| Agregado iterável (fornece o iterador) | `dominio-historico-academico/dominio/historicoacademico/historico/HistoricoAcademico.java` |
| Serviço consumidor do iterador | `dominio-historico-academico/dominio/historicoacademico/ConsultaHistoricoServico.java` |

**Como aparece no código:** `HistoricoAcademico.iteradorRegistros()` retorna uma instância de `IteradorRegistrosDisciplina` que encapsula a lista interna com `Collections.unmodifiableList`. `ConsultaHistoricoServico` usa apenas `temProximo()` / `proximo()`, sem tocar a coleção diretamente.

---

### Template Method — Maria Claudia

**Intenção:** definir o esqueleto do fluxo pedagógico — abertura → registro de aulas → frequência → avaliações → lançamento de notas → fechamento do resultado — no serviço base, delegando cada passo ao agregado `DiarioTurma`.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Template (sequência fixa de passos orquestrada pelo serviço) | `dominio-gestao-pedagogica/dominio/gestaopedagogica/DiarioTurmaServico.java` |
| Passo 1 — registrar aula | `dominio-gestao-pedagogica/dominio/gestaopedagogica/diario/DiarioTurma.java` (método `registrarAula`) |
| Passo 2 — registrar frequência | `dominio-gestao-pedagogica/dominio/gestaopedagogica/diario/DiarioTurma.java` (método `registrarFrequencia`) |
| Passo 3 — adicionar avaliação | `dominio-gestao-pedagogica/dominio/gestaopedagogica/diario/DiarioTurma.java` (método `adicionarAvaliacao`) |
| Passo 4 — lançar nota | `dominio-gestao-pedagogica/dominio/gestaopedagogica/diario/DiarioTurma.java` (método `lancarNota`) |
| Passo 5 — fechar resultado | `dominio-gestao-pedagogica/dominio/gestaopedagogica/diario/DiarioTurma.java` (método `fecharResultado`) |
| Passo opcional — nota de recuperação | `dominio-gestao-pedagogica/dominio/gestaopedagogica/diario/DiarioTurma.java` (método `lancarNotaRecuperacao`) |

**Como aparece no código:** `DiarioTurmaServico` define a sequência invariável de chamadas e aplica pré-condições (ex.: só aceita nota se o diário não estiver fechado); `DiarioTurma` implementa cada passo individual com suas próprias regras de negócio.

---

### Decorator — Maria Clara

**Intenção:** adicionar comportamentos opcionais — lista de espera e modalidade online — às turmas ofertadas, e critério de elegibilidade às oportunidades de estágio, sem alterar as classes base.

#### Contexto A — Oferta de Turmas

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface componente | `dominio-oferta-academica/dominio/ofertaturmas/turma/decorator/TurmaOferecida.java` |
| Decorador abstrato | `dominio-oferta-academica/dominio/ofertaturmas/turma/decorator/TurmaDecorador.java` |
| Decorador concreto: lista de espera | `dominio-oferta-academica/dominio/ofertaturmas/turma/decorator/TurmaComListaEspera.java` |
| Decorador concreto: turma online (EAD) | `dominio-oferta-academica/dominio/ofertaturmas/turma/decorator/TurmaOnline.java` |
| Serviço que constrói e aplica os decoradores | `dominio-oferta-academica/dominio/ofertaturmas/OfertaTurmaServico.java` |

#### Contexto B — Oportunidades de Estágio

| Papel no padrão | Arquivo `.java` |
|---|---|
| Decorador abstrato | `dominio-estagios/dominio/estagios/oportunidade/OportunidadeDecorador.java` |
| Decorador concreto: elegibilidade | `dominio-estagios/dominio/estagios/oportunidade/OportunidadeComCriterioElegibilidade.java` |
| Serviço que aplica o decorador | `dominio-estagios/dominio/estagios/EstagioServico.java` |

**Como aparece no código:** `TurmaDecorador` implementa `TurmaOferecida` e mantém uma referência `protected final TurmaOferecida turma`, delegando todos os métodos ao componente encapsulado e sobrescrevendo apenas o comportamento adicionado. `OfertaTurmaServico` envolve a `Turma` concreta nos decoradores adequados conforme a modalidade ou configuração.

---

### Strategy — Vinicius

**Intenção:** encapsular as regras de elegibilidade para adicionar itens ao plano de matrícula em estratégias intercambiáveis, permitindo trocar a política de validação (regular vs. por exceção) sem modificar o serviço principal. O mesmo padrão governa o fluxo de solicitação de mobilidade acadêmica.

#### Contexto A — Matrícula (F-04)

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface da estratégia | `dominio-matricula/dominio/matricula/matricula/EstrategiaMatricula.java` |
| Estratégia concreta: matrícula regular | `dominio-matricula/dominio/matricula/matricula/ValidacaoRegular.java` |
| Estratégia concreta: matrícula por exceção | `dominio-matricula/dominio/matricula/matricula/ValidacaoExcecao.java` |
| Contexto (injeta e usa a estratégia) | `dominio-matricula/dominio/matricula/MatriculaServico.java` |
| Agregado que armazena a estratégia ativa | `dominio-matricula/dominio/matricula/matricula/Matricula.java` |

**Como aparece no código:** `EstrategiaMatricula` declara `validarAdicao(...)`. `ValidacaoRegular` exige pré-requisitos cumpridos e ausência de pendências; `ValidacaoExcecao` ignora essas restrições (exceção aprovada pela coordenação). `Matricula` guarda a estratégia e a chama em `adicionarItem(...)`.

#### Contexto B — Mobilidade Acadêmica (F-12)

| Papel no padrão | Arquivo `.java` |
|---|---|
| Serviço de domínio (orquestra a estratégia) | `dominio-mobilidade-academica/dominio/mobilidadeacademica/MobilidadeAcademicaServico.java` |
| Agregado principal | `dominio-mobilidade-academica/dominio/mobilidadeacademica/mobilidade/MobilidadeAcademica.java` |
| Item do plano de estudos | `dominio-mobilidade-academica/dominio/mobilidadeacademica/mobilidade/ItemPlanoEstudos.java` |
| Repositório do domínio | `dominio-mobilidade-academica/dominio/mobilidadeacademica/mobilidade/MobilidadeAcademicaRepositorio.java` |
| Serviço de aplicação | `aplicacao/aplicacao/mobilidadeacademica/MobilidadeAcademicaServicoAplicacao.java` |
| DTO de resumo | `aplicacao/aplicacao/mobilidadeacademica/MobilidadeAcademicaResumo.java` |
| DTO de item do plano | `aplicacao/aplicacao/mobilidadeacademica/ItemPlanoResumo.java` |

**Como aparece no código:** `MobilidadeAcademica` encapsula o ciclo de vida da solicitação (solicitada → aprovada → em andamento → concluída / cancelada). `MobilidadeAcademicaServico` orquestra as transições de estado e valida pré-condições; o controlador REST expõe os endpoints de solicitação e cancelamento conectados ao frontend via `api.mobilidade`.

---

### Proxy — Bernardo

**Intenção:** interpor uma camada de pré-condições e controle de acesso antes de delegar às operações reais — verificando janela do calendário acadêmico na secretaria virtual e validando portas externas antes de calcular integralização.

#### Contexto A — Secretaria Virtual

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface sujeito | `dominio-secretaria-virtual/dominio/secretariavirtual/SolicitacaoServico.java` |
| Proxy (pré-condições + delegação) | `dominio-secretaria-virtual/dominio/secretariavirtual/SolicitacaoServicoProxy.java` |
| Sujeito real | `dominio-secretaria-virtual/dominio/secretariavirtual/SolicitacaoServicoReal.java` |

#### Contexto B — Integralização Curricular

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface sujeito | `dominio-integralizacao-curricular/dominio/integralizacao/IntegralizacaoOperacoes.java` |
| Proxy (consulta portas externas antes de delegar) | `dominio-integralizacao-curricular/dominio/integralizacao/IntegralizacaoServicoProxy.java` |
| Sujeito real | `dominio-integralizacao-curricular/dominio/integralizacao/IntegralizacaoServico.java` |

**Como aparece no código:** `SolicitacaoServicoProxy` verifica `CalendarioAcademicoPorta.estaDentroDoPrazo(...)` e a ausência de duplicidade antes de chamar `servicoReal.abrirSolicitacao(...)`. `IntegralizacaoServicoProxy` consulta `ConsultaPeriodoLetivoPorta`, `ConsultaPendenciasPorta` e `ConsultaRequisitosIntegralizacaoPorta` antes de delegar ao `IntegralizacaoServico`. Ambos os proxies são registrados como `@Bean` em `BackendAplicacao.java` e injetados nos respectivos controladores REST no lugar do sujeito real.

Erros lançados pelos proxies (e por qualquer serviço de domínio) são capturados centralmente por `apresentacao-backend/apresentacao/GlobalExceptionHandler.java`, que mapeia `IllegalArgumentException` → HTTP 400 e `IllegalStateException` → HTTP 409 antes de retornar ao frontend.

---

### Observer — Matheus Veríssimo

**Intenção:** notificar automaticamente outros componentes quando o estado de um benefício de permanência acadêmica ou de um caso psicopedagógico muda, sem criar dependência direta entre os contextos.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface do publicador (Subject) | `dominio-compartilhado/dominio/evento/EventoBarramento.java` |
| Interface do observador (Observer) | `dominio-compartilhado/dominio/evento/EventoObservador.java` |
| Publisher: ciclo de vida de benefícios | `dominio-permanencia-academica/dominio/permanenciaacademica/BeneficioServico.java` |
| Entidade que emite eventos de benefício | `dominio-permanencia-academica/dominio/permanenciaacademica/Beneficio.java` |
| Publisher: triagem psicopedagógica | `dominio-apoio-psicopedagogico/dominio/apoiopsicopedagogico/ApoioServico.java` |
| Publisher: atendimento psicopedagógico | `dominio-apoio-psicopedagogico/dominio/apoiopsicopedagogico/AtendimentoServico.java` |
| Publisher: triagem | `dominio-apoio-psicopedagogico/dominio/apoiopsicopedagogico/TriagemServico.java` |

**Como aparece no código:** `Beneficio.suspender()` retorna um `EventoDominio`; `BeneficioServico` o repassa ao `EventoBarramento.postar(evento)`. Qualquer componente que implemente `EventoObservador<BeneficioSuspenso>` e se registre no barramento recebe a notificação sem que `BeneficioServico` precise conhecer os assinantes.

---

### Observer — Jera

**Intenção:** notificar outros contextos quando cobranças são geradas ou liquidadas e quando atividades complementares são deferidas ou indeferidas, usando o mesmo barramento de eventos do shared kernel.

| Papel no padrão | Arquivo `.java` |
|---|---|
| Interface do publicador (Subject) | `dominio-compartilhado/dominio/evento/EventoBarramento.java` |
| Interface do observador (Observer) | `dominio-compartilhado/dominio/evento/EventoObservador.java` |
| Publisher: gestão financeira | `dominio-gestao-financeira/dominio/gestaofinanceira/CobrancaServico.java` |
| Entidade que emite eventos de cobrança | `dominio-gestao-financeira/dominio/gestaofinanceira/Cobranca.java` |
| Publisher: atividades complementares | `dominio-atividades-complementares/dominio/atividadescomplementares/AtividadeComplementarServico.java` |

**Como aparece no código:** `CobrancaServico` publica eventos ao registrar pagamentos ou ao gerar cobranças em lote; `AtividadeComplementarServico` publica eventos quando defere ou indefere horas complementares. O barramento (`EventoBarramento`) desacopla emissores de receptores — nenhum publicador conhece seus assinantes.

---

## Funcionalidades

| F | Funcionalidade | Responsável | Padrão | Resumo |
|---|---|---|---|---|
| **F-01** | Gestão Curricular do Curso | Julia Torres | Iterator | Criar e versionar matriz curricular com disciplinas, pré-requisitos, correquisitos e ciclo de vida ativo/inativo |
| **F-02** | Planejamento do Período Letivo | Maria Claudia | Template Method | Definir semestre acadêmico com janelas que habilitam ou bloqueiam operações por intervalo de datas |
| **F-03** | Oferta de Turmas | Maria Clara | Decorator | Configurar turmas com professor, sala e horário; adicionar comportamentos dinâmicos sem alterar a classe base |
| **F-04** | Montagem e Ajuste de Matrícula | Vinicius | Strategy | Plano de matrícula, confirmação, ajustes e trancamento via wizard integrado ao backend; validação de elegibilidade por estratégia intercambiável (regular / por exceção) |
| **F-05** | Gestão Pedagógica da Turma | Maria Claudia | Template Method | Diário de turma: registro de aulas, frequência, avaliações, notas e resultado final com recuperação |
| **F-06** | Gestão do Histórico Acadêmico | Julia Torres | Iterator | Consolidação de resultados, aproveitamentos externos, retificações e acompanhamentos; histórico oficial via Iterator |
| **F-07** | Secretaria Virtual Acadêmica | Bernardo | Proxy | Abertura e tramitação de protocolos acadêmicos com controle de permissões via Proxy |
| **F-08** | Validação de Integralização e Colação | Bernardo | Proxy | Análise de cumprimento de requisitos curriculares e registro formal da colação de grau |
| **F-09** | Atividades Complementares | Jera | Observer | Submissão e análise de horas extracurriculares; eventos de deferimento notificam outros contextos |
| **F-10** | Permanência Acadêmica e Bolsas | Matheus | Observer | Editais, inscrições, análise, gestão de benefícios e registro de ações de permanência com notificação automática de mudanças de estado |
| **F-11** | Apoio Psicopedagógico | Matheus | Observer | Solicitação, triagem, atendimentos e encerramento de caso com sigilo de atendimento |
| **F-12** | Mobilidade Acadêmica | Vinicius | Strategy | Solicitação de intercâmbio, plano de estudos com equivalências, aprovação pela coordenação e registro de resultados no histórico; frontend integrado ao backend via `api.mobilidade` |
| **F-13** | Gestão Financeira Acadêmica | Jera | Observer | Cobranças, pagamentos, descontos, contestações e comprovantes; eventos de pagamento notificam outros contextos |
| **F-14** | Centro de Estágios e Oportunidades | Maria Clara | Decorator | Publicação de vagas, candidatura, formalização do estágio e entrega de relatórios |

<details>
<summary><strong>Regras de negócio selecionadas por funcionalidade</strong></summary>

### F-01 — Gestão Curricular (Julia Torres)
- A mesma disciplina não pode aparecer mais de uma vez na mesma matriz curricular
- A matriz só pode ser ativada se a carga horária e os créditos das disciplinas forem suficientes para o mínimo exigido
- Apenas uma versão da matriz pode estar ativa por curso ao mesmo tempo
- Pré-requisitos cíclicos são rejeitados — o sistema percorre o grafo e detecta caminhos circulares
- Correquisitos devem pertencer à mesma matriz curricular

### F-02 — Período Letivo (Maria Claudia)
- Janela acadêmica define o intervalo em que uma operação é permitida; fora da janela, a operação é bloqueada
- O período encerrado torna os registros imutáveis sem reabertura formal autorizada

### F-04 — Matrícula (Vinicius)
- O plano de matrícula é provisório — não gera vínculo acadêmico até a confirmação
- A elegibilidade é verificada por estratégia (regular, exceção, mobilidade), sem alterar o serviço principal

### F-05 — Gestão Pedagógica (Maria Claudia)
- Reprovação por frequência é aplicada quando a presença fica abaixo do mínimo, independentemente da média

### F-06 — Histórico Acadêmico (Julia Torres)
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
├── dominio-compartilhado/            ← Shared Kernel — EstudanteId, CursoId, EventoBarramento
├── dominio-curriculo/                ← F-01 (Julia Torres) — Iterator pattern
├── dominio-oferta-academica/         ← F-02 (Maria Claudia) + F-03 (Maria Clara) — sub-pacotes separados
├── dominio-matricula/                ← F-04 (Vinicius) — Strategy pattern
├── dominio-gestao-pedagogica/        ← F-05 (Maria Claudia) — Template Method
├── dominio-historico-academico/      ← F-06 (Julia Torres) — Iterator pattern
├── dominio-secretaria-virtual/       ← F-07 (Bernardo) — Proxy pattern
├── dominio-integralizacao-curricular/← F-08 (Bernardo) — Proxy pattern
├── dominio-atividades-complementares/← F-09 (Jera) — Observer pattern
├── dominio-permanencia-academica/    ← F-10 (Matheus) — Observer pattern
├── dominio-apoio-psicopedagogico/    ← F-11 (Matheus) — Observer pattern
├── dominio-mobilidade-academica/     ← F-12 (Vinicius) — Strategy pattern
├── dominio-gestao-financeira/        ← F-13 (Jera) — Observer pattern
├── dominio-estagios/                 ← F-14 (Maria Clara) — Decorator pattern
│
├── aplicacao/                        ← Orquestração cross-domínio e DTOs (*Resumo, *Detalhe)
│     ├── matricula/                  ← MatriculaServicoAplicacao, MatriculaResumo, ItemResumo
│     └── mobilidadeacademica/        ← MobilidadeAcademicaServicoAplicacao, MobilidadeAcademicaResumo, ItemPlanoResumo
├── infraestrutura/                   ← JPA, @Entity, *RepositorioImpl
├── apresentacao-backend/             ← 21 Controllers REST (Spring Boot) + GlobalExceptionHandler
└── apresentacao-frontend/            ← React + Vite (17 telas integradas ao backend)
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
| F-04 · Matrícula | `montar_plano_matricula` · `confirmar_matricula` · `ajustar_matricula` · `trancar_disciplina` · `solicitar_excecao` · `trancar_periodo` |
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

## Telas do Frontend

O frontend React (Vite + TanStack Router + React Query) possui **17 rotas**, todas conectadas ao backend via cliente de API tipado (`src/lib/api.ts`).

| Rota | Funcionalidade |
|---|---|
| `/` | Home — visão geral do estudante |
| `/gestao-curricular` | F-01 · Matrizes curriculares e disciplinas |
| `/periodo-letivo` | F-02 · Períodos letivos e janelas acadêmicas |
| `/oferta-turmas` | F-03 · Turmas, professores e salas |
| `/matricula` | F-04 · Wizard de montagem, ajuste e trancamento de matrícula |
| `/gestao-pedagogica` | F-05 · Diário de turma, frequência e notas |
| `/historico-academico` | F-06 · Histórico oficial e situação discente |
| `/secretaria-virtual` | F-07 · Protocolos acadêmicos |
| `/integralizacao` | F-08 · Análise de integralização e colação |
| `/atividades-complementares` | F-09 · Submissão e análise de horas extracurriculares |
| `/permanencia` | F-10 · Editais, bolsas e ações de permanência |
| `/psicopedagogico` | F-11 · Triagem, atendimentos e encerramento de caso |
| `/mobilidade` | F-12 · Solicitação de mobilidade e plano de estudos |
| `/financeiro` | F-13 · Cobranças, pagamentos e extratos |
| `/estagios` | F-14 · Oportunidades, candidaturas e relatórios |
| `/perfil` | Perfil do estudante |

---

## Migrações de Banco de Dados

O Flyway gerencia o schema PostgreSQL com **12 versões** em `apresentacao-backend/src/main/resources/db/migration/`:

| Versão | Arquivo | Descrição |
|---|---|---|
| V1 | `V1__seed.sql` | Seed inicial — cursos, disciplinas, períodos, turmas e estudante demo |
| V2 | `V2__dados_permanencia_apoio.sql` | Dados de editais, benefícios e casos psicopedagógicos |
| V3 | `V3__ajusta_caso_estudante_demo.sql` | Correção de vínculo estudante × caso |
| V4 | `V4__categorias_atividade.sql` | Categorias de atividades complementares |
| V5 | `V5__bolsas.sql` | Bolsas vinculadas a benefícios |
| V6 | `V6__contestacao_seed.sql` | Contestação de cobrança demo |
| V7 | `V7__contabilizacao_atividade_complementar.sql` | Contabilização de horas complementares |
| V8 | `V8__acordo.sql` | Acordo de pagamento demo |
| V9 | `V9__descricao_edital.sql` | Campo descrição no edital |
| V10 | `V10__prazos_permanencia_relativos.sql` | Prazos de permanência baseados em data relativa |
| V11 | `V11__seed_itens_matriz_curricular.sql` | Itens da matriz curricular para demo |
| V12 | `V12__corrige_solicitacoes_secretaria.sql` | Correção de solicitações acadêmicas do estudante demo |

---

## Arquitetura

O projeto segue **Arquitetura Limpa** com módulos Maven separados por camada:

```
┌──────────────────────────────────────────────────────────┐
│  apresentacao-backend (Spring Boot REST)                 │
│    *Controlador.java (21 controllers)                    │
│    GlobalExceptionHandler.java (400/409 → frontend)      │
│  apresentacao-frontend (React + Vite — 17 telas)         │
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
│  dominio-permanencia-academica  dominio-apoio-psicopedagogico │
│  dominio-estagios  ...                                    │
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
| Linguagem | Java 21 |
| Framework Web | Spring Boot 4.0.5 |
| Persistência | Spring Data JPA / Hibernate 7 |
| Banco de dados | PostgreSQL 16 (Docker) |
| Testes BDD | Cucumber 7.34.3 + JUnit 6.0.3 + Mockito 5.23.0 |
| Build | Maven multi-módulo |
| Utilitários | Apache Commons Lang3 |
| Containerização | Docker Compose (backend + frontend + postgres) |
| Frontend | React + Vite (TanStack Router + React Query) |

---

## Como Rodar

**Pré-requisitos:** Docker e Docker Compose

```bash
# Subir todos os serviços (backend, frontend, postgres) com hot reload
docker compose up

# Backend disponível em http://localhost:8080
# Frontend disponível em http://localhost:5173
```

**Sem Docker (desenvolvimento local):**

```bash
# Pré-requisitos: Java 21+, Maven 3.9+, PostgreSQL local

# 1. Build de todos os módulos
mvn install -DskipTests

# 2. Subir o backend
cd apresentacao-backend
mvn spring-boot:run

# 3. Executar todos os testes BDD
mvn test
```

---

## Equipe

| Integrante | Funcionalidades | Padrão |
|---|---|---|
| Julia Torres   | F-01 · Gestão Curricular · F-06 · Histórico Acadêmico | Iterator |
| Matheus Veríssimo | F-10 · Permanência Acadêmica · F-11 · Apoio Psicopedagógico | Observer |
| Vinicius | F-04 · Matrícula · F-12 · Mobilidade Acadêmica | Strategy |
| Bernardo | F-07 · Secretaria Virtual · F-08 · Integralização e Colação | Proxy |
| Maria Clara    | F-03 · Oferta de Turmas · F-14 · Estágios | Decorator |
| Maria Claudia  | F-02 · Período Letivo · F-05 · Gestão Pedagógica | Template Method |
| Jera | F-09 · Atividades Complementares · F-13 · Gestão Financeira | Observer |


---

*Projeto acadêmico — CESAR School, 2026.1*
