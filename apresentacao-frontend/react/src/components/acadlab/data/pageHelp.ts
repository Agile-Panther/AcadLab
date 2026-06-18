export type PageHelp = {
  title: string;
  summary: string;
  bullets?: string[];
};

export const pageHelp: Record<string, PageHelp> = {
  "/": {
    title: "Início",
    summary:
      "Visão geral do AcadLab com acesso rápido aos módulos acadêmicos, indicadores e atalhos.",
    bullets: [
      "Use a barra lateral para navegar entre módulos.",
      "Clique em seu nome no rodapé da barra para abrir o perfil.",
    ],
  },
  "/perfil": {
    title: "Meu Perfil",
    summary:
      "Informações pessoais, acadêmicas e profissionais do usuário. O conteúdo se adapta ao tipo de perfil ativo.",
    bullets: [
      "Alterne entre perfis para visualizar variações por papel.",
      "Edite dados pessoais e preferências de contato.",
    ],
  },
  "/matricula": {
    title: "Matrícula",
    summary:
      "Realize e acompanhe a matrícula em disciplinas, valide pré-requisitos e gerencie ajustes do período.",
    bullets: [
      "Confirme disciplinas obrigatórias antes das eletivas.",
      "Verifique conflitos de horário antes de salvar.",
    ],
  },
  "/oferta-turmas": {
    title: "Oferta de Turmas",
    summary:
      "Planeje e publique a oferta de turmas por período, atribua docentes e salas e revise a capacidade.",
    bullets: [
      "Use a grade para detectar choques de horário.",
      "Publique somente após validar capacidade e docente.",
    ],
  },
  "/gestao-curricular": {
    title: "Gestão Curricular",
    summary:
      "Estruture cursos, matrizes curriculares, disciplinas e pré-requisitos.",
    bullets: ["Mantenha versionamento ao alterar matrizes vigentes."],
  },
  "/periodo-letivo": {
    title: "Período Letivo",
    summary:
      "Configure calendário acadêmico, datas-chave e janelas de matrícula e avaliação.",
  },
  "/historico-academico": {
    title: "Histórico Acadêmico",
    summary:
      "Consulte notas, frequências e progressão do estudante ao longo dos períodos.",
  },
  "/integralizacao": {
    title: "Integralização",
    summary:
      "Acompanhe o cumprimento da matriz e o tempo restante para conclusão do curso.",
  },
  "/atividades-complementares": {
    title: "Atividades Complementares",
    summary:
      "Cadastre, valide e contabilize horas de atividades complementares exigidas pelo curso.",
  },
  "/estagios": {
    title: "Estágios e Oportunidades",
    summary:
      "Gerencie vagas, candidaturas, encaminhamentos e validação de estágios.",
    bullets: [
      "Cadastre vagas com critérios objetivos de elegibilidade.",
      "Acompanhe o ciclo de candidatura até a seleção.",
    ],
  },
  "/mobilidade": {
    title: "Mobilidade Acadêmica",
    summary:
      "Programas de intercâmbio, equivalências e acompanhamento de alunos em mobilidade.",
  },
  "/permanencia": {
    title: "Permanência",
    summary:
      "Monitore indicadores de risco e ações de apoio para reduzir evasão e retenção.",
  },
  "/psicopedagogico": {
    title: "Apoio Psicopedagógico",
    summary:
      "Atendimentos, acompanhamentos e encaminhamentos do núcleo psicopedagógico.",
  },
  "/gestao-pedagogica": {
    title: "Gestão Pedagógica",
    summary:
      "Indicadores de desempenho, planos de ensino e acompanhamento docente.",
  },
  "/secretaria-virtual": {
    title: "Secretaria Virtual",
    summary:
      "Solicitações, documentos e protocolos atendidos pela secretaria.",
  },
  "/financeiro": {
    title: "Financeiro",
    summary:
      "Mensalidades, boletos, acordos e situação financeira do estudante.",
  },
};

export function getPageHelp(pathname: string): PageHelp {
  return (
    pageHelp[pathname] ?? {
      title: "Sobre esta página",
      summary:
        "Use o menu lateral para navegar e as abas do topo para alternar entre etapas desta área.",
    }
  );
}
