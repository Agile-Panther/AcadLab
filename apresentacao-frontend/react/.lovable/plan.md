## Objetivo

Substituir o padrão atual de "telas empilhadas por tabs" pelo **fluxo real** de cada feature do backlog. Cada rota passa a abrir num **estado realista do usuário "Maria Santos"** (mockado), com transições reais entre etapas em vez de tabs paralelas que mostram tudo ao mesmo tempo.

## Princípios do redesign (aplicados a todas as 14 features)

- **Estado inicial = estado mais provável do usuário**, não "tela 1 do protótipo". Ex.: matrícula abre na visão geral da matrícula já confirmada de Maria, não no formulário de montar plano.
- **Transições por ação, não por tab.** Tabs continuam permitidas só quando representam *visões paralelas* do mesmo objeto (ex.: extrato | comprovantes). Para *etapas*, vira fluxo (botões avançam, breadcrumbs/stepper mostram onde está).
- **Mock com estado React.** Cada página tem um `useState` central com o "estado do mundo" (matrícula confirmada? plano em rascunho? solicitação pendente?), e os componentes reagem.
- **Sem alterar componentes atômicos**, design tokens ou shell (Sidebar/TopBar/AppShell). Só a estrutura interna das rotas.

## Mapa de fluxos por rota

```text
/matricula
  Estado padrão: Maria já tem matrícula confirmada de 2025.2
  Visão geral (grade + KPIs + ações) → [Solicitar Ajuste | Trancar Disciplina | Trancar Período | Nova Matrícula 2026.1]
    Nova Matrícula → Stepper: Montar Plano → Validação → Confirmação → Sucesso (grade)
    Ajuste → Modal/Drawer com incluir/remover dentro da janela
    Exceção → Form inline quando validação bloqueia

/gestao-curricular (Coordenador)
  Lista de matrizes do curso → seleção → Detalhe da matriz (disciplinas + grafo pré-req)
    Ações: Ativar/Desativar, Adicionar disciplina (só se inativa), Configurar pré-req

/periodo-letivo (Secretaria)
  Lista de períodos (vigente em destaque) → Detalhe com janelas acadêmicas (timeline)
    Ações por status: Editar (não iniciado), Cancelar (não iniciado), Encerrar (vigente sem pendências)
    Wizard "Novo período" → datas → janelas → confirmação

/oferta-turmas (Coordenador)
  Grade de turmas do período vigente (filtros) → clique abre Drawer da turma
    Ações: Editar, Cancelar (com remanejamento), Nova Turma (wizard: disciplina → prof+sala+horário → capacidade)
    Sub-views: Salas | Professores em tabs (paralelas, OK)

/gestao-pedagogica (Professor)
  Estado padrão: lista das turmas do professor no período vigente
  Selecionar turma → Diário da turma com sub-fluxo:
    Visão geral | Aulas (registrar) | Frequência (por aula) | Avaliações | Notas | Fechamento
  Fechamento bloqueado até notas/frequência completas

/historico-academico (Estudante)
  Visão consolidada (CR, integralização %, periodo a periodo, expandível)
  Ações: Emitir oficial, Solicitar correção (form), Ver aproveitamentos

/secretaria-virtual (Estudante)
  Estado padrão: Minhas solicitações (lista com status)
    [+ Nova Solicitação] → Wizard: tipo → form dinâmico → anexos → revisão → protocolo
    Clique em solicitação → Detalhe com timeline + ação contextual (complementar/cancelar)

/integralizacao (Estudante/Coordenador)
  Estudante: progresso curricular + botão "Solicitar análise de conclusão"
    Após solicitar → status "Em análise" → resultado (apto/inapto + pendências)
  Coordenador: lista de solicitações → checklist gerado → aprovar/rejeitar

/atividades-complementares (Estudante)
  Visão geral: saldo por categoria + lista de atividades (status badge)
    [+ Submeter] → Wizard: categoria → dados → comprovante → confirmação
    Atividade indeferida → ação "Solicitar revisão" inline

/permanencia (Estudante)
  Estado padrão: "Meus benefícios" (ativos) + "Editais abertos"
    Edital → Detalhe → Inscrever-se (wizard com critérios + docs)
    Benefício ativo → Renovar / Ver status
    Inscrição indeferida → Interpor recurso

/psicopedagogico (Estudante)
  Estado padrão: "Meu caso" (se houver) com timeline de atendimentos OU CTA "Solicitar apoio"
    Solicitar → form → confirmação → status "Em triagem"

/mobilidade (Estudante)
  Estado padrão: "Minha mobilidade" se existir, senão CTA "Solicitar mobilidade"
    Wizard: instituição → plano de estudos (disciplinas + equivalências) → docs → envio
    Após autorização → Tela "Em curso" → "Anexar comprovantes" → "Concluída"

/financeiro (Estudante)
  Visão padrão: Resumo (próxima cobrança em destaque) + extrato
    Cobrança → Detalhe → [Pagar | Contestar]
    Pagamento confirmado → Emitir comprovante
    Contestação → form + acompanhamento

/estagios (Estudante)
  Lista de oportunidades elegíveis (filtradas pelo perfil) + "Minhas candidaturas"
    Oportunidade → Detalhe → Candidatar-se (confirma requisitos atendidos)
    Candidatura em análise → poder cancelar
    Encaminhamento → status especial
```

## Implementação técnica

- Cada rota vira um componente com `useState` central: `view` ("overview" | "wizard" | "detail"), `selectedId`, e mocks de dados realistas no topo do arquivo.
- Reutilizar `SectionTitle`, `StatsRow`, `DataTable`, `StatusBadge`, `SuccessBanner`, `ValidationCallout`, `FormField`, `ActionBar`, `ScheduleGrid` já existentes.
- Introduzir **2 helpers novos** em `src/components/acadlab/molecules/`:
  - `Stepper.tsx` — barra de etapas para wizards (Plano → Validação → Confirmação → Sucesso).
  - `EmptyHero.tsx` — estado vazio com CTA único (usado em mobilidade/psicopedagógico/permanência sem benefício).
- `FeaturePage` (tabs) continua disponível, mas é usado **só onde tabs fazem sentido** (ex.: oferta-turmas: Turmas|Salas|Professores). Para o resto, as rotas montam seu próprio layout sob `AppShell`.
- Nada de lógica de backend. Tudo mockado em memória; transições são pure React state.

## Escopo fora

- Não mexer em Sidebar, TopBar, design tokens, perfil, index/dashboard.
- Não adicionar Lovable Cloud / backend.
- Não tocar `routeTree.gen.ts` (gerado).
- Não alterar componentes atômicos existentes (apenas consumir).

## Entregáveis

- 2 novos componentes (`Stepper`, `EmptyHero`) + export em `molecules/index.ts`.
- 14 arquivos de rota reescritos (um por feature) com fluxo realista.
- Mocks centralizados por rota (no topo do arquivo, sem novo módulo de dados).
