// Typed API client — all requests are proxied by Vite to http://localhost:8080

async function get<T>(path: string): Promise<T> {
  const res = await fetch(`/backend/${path}`);
  if (!res.ok) throw new Error(`${res.status} ${res.statusText}`);
  return res.json() as Promise<T>;
}

// ─── Types ────────────────────────────────────────────────────────────────────

export interface PeriodoLetivoResumo {
  id: number;
  cursoId: number;
  ano: number;
  semestre: number;
  dataInicio: string;
  dataFim: string;
  status: string;
  janelas: { tipo: string; inicio: string; fim: string }[];
}

export interface TurmaResumo {
  id: number;
  periodoLetivoId: number;
  disciplinaId: number;
  professorId: number | null;
  salaId: number | null;
  modalidade: string;
  capacidade: number;
  status: string;
}

export interface SalaResumo {
  id: number;
  nome: string;
  capacidade: number;
  ativa: boolean;
}

export interface ProfessorResumo {
  id: number;
  nome: string;
  ativo: boolean;
}

export interface MatrizCurricularResumo {
  id: number;
  cursoId: number;
  nome: string;
  status: string;
}

export interface MatriculaResumo {
  id: number;
  estudanteId: number;
  periodoLetivoId: number;
  status: string;
}

export interface DiarioTurmaResumo {
  id: number;
  turmaId: number;
  periodoLetivoId: number;
  professorResponsavelId: number;
  dataInicioPeriodo: string;
  dataFimPeriodo: string;
  mediaMinima: number;
  frequenciaMinima: number;
  status: string;
}

export interface RegistroDisciplinaResumo {
  id: number;
  disciplinaId: number;
  turmaId: number;
  periodoLetivoId: number;
  nota: number;
  frequencia: number;
  situacao: string;
}

export interface AproveitamentoResumo {
  id: number;
  disciplinaEquivalenteId: number;
  cargaHorariaExterna: number;
  cargaHorariaRequerida: number;
  instituicaoOrigem: string;
  disciplinaOrigem: string;
}

export interface AcompanhamentoResumo {
  id: number;
  observacao: string;
  data: string;
}

export interface HistoricoAcademicoResumo {
  id: number;
  estudanteId: number;
  matrizCurricularId: number;
  situacaoDiscente: string;
  registros: RegistroDisciplinaResumo[];
  aproveitamentos: AproveitamentoResumo[];
  acompanhamentos: AcompanhamentoResumo[];
}

export interface SolicitacaoAcademicaResumo {
  id: number;
  estudanteId: number;
  periodoLetivoId: number;
  tipo: string;
  status: string;
  descricao: string;
  protocoloId: number;
  dataAbertura: string;
  justificativaAnalise: string | null;
  dataAnalise: string | null;
  analistaId: number | null;
  possuiImpactoAcademico: boolean;
  alteracoesVinculadas: boolean;
  documentos: { tipo: string; nomeArquivo: string; dataAnexo: string }[];
}

export interface IntegralizacaoResumo {
  id: number;
  estudanteId: number;
  matrizCurricularId: number;
  status: string;
  observacao: string | null;
  aprovadorId: number | null;
  dataAprovacao: string | null;
  itensChecklist: { tipo: string; descricao: string; cumprido: boolean }[];
}

export interface AtividadeComplementarResumo {
  id: number;
  estudanteId: number;
  categoriaId: number;
  descricao: string;
  horasSubmetidas: number;
  horasAprovadas: number;
  status: string;
}

export interface EditalResumo {
  id: number;
  programa: string;
  vagas: number;
  prazoInscricaoInicio: string;
  prazoInscricaoFim: string;
  prazoRecursoInicio: string | null;
  prazoRecursoFim: string | null;
  prazoRenovacao: string | null;
  status: string;
}

export interface CasoResumo {
  id: number;
  estudanteId: number;
  responsavelId: number | null;
  status: string;
}

export interface MobilidadeAcademicaResumo {
  id: number;
  estudanteId: number;
  instituicaoDestino: string;
  status: string;
}

export interface CobrancaResumo {
  id: number;
  contratoId: number;
  estudanteId: number;
  periodoLetivoId: number;
  valorBase: number;
  valorAtual: number;
  vencimento: string;
  versao: number;
  status: string;
}

export interface OportunidadeResumo {
  id: number;
  empresaId: number;
  descricao: string;
  cargaHorariaTotal: number;
  status: string;
}

// ─── API ──────────────────────────────────────────────────────────────────────

export const api = {
  periodos: {
    listByCurso: (cursoId: number) =>
      get<PeriodoLetivoResumo[]>(`periodos-letivos/curso/${cursoId}`),
  },
  turmas: {
    listByPeriodo: (periodoId: number) =>
      get<TurmaResumo[]>(`turmas/periodo/${periodoId}`),
    getById: (id: number) => get<TurmaResumo>(`turmas/${id}`),
    listSalas: () => get<SalaResumo[]>(`salas`),
    listProfessores: () => get<ProfessorResumo[]>(`professores`),
  },
  curriculo: {
    listByCurso: (cursoId: number) =>
      get<MatrizCurricularResumo[]>(`curriculo/curso/${cursoId}`),
    getById: (id: number) => get<MatrizCurricularResumo>(`curriculo/${id}`),
  },
  matricula: {
    getById: (id: number) => get<MatriculaResumo>(`matriculas/${id}`),
  },
  diarios: {
    getByTurma: (turmaId: number) =>
      get<DiarioTurmaResumo>(`diarios/turma/${turmaId}`),
  },
  historico: {
    getByEstudante: (estudanteId: number) =>
      get<HistoricoAcademicoResumo>(`historicos/estudante/${estudanteId}`),
    getById: (id: number) => get<HistoricoAcademicoResumo>(`historicos/${id}`),
  },
  solicitacoes: {
    listPendentes: () =>
      get<SolicitacaoAcademicaResumo[]>(`solicitacoes/pendentes`),
    listTodas: () =>
      get<SolicitacaoAcademicaResumo[]>(`solicitacoes/todas`),
    getById: (id: number) =>
      get<SolicitacaoAcademicaResumo>(`solicitacoes/${id}`),
  },
  integralizacao: {
    listAll: () => get<IntegralizacaoResumo[]>(`integralizacoes`),
    getByEstudante: (estudanteId: number) =>
      get<IntegralizacaoResumo>(`integralizacoes/estudante/${estudanteId}`),
  },
  atividades: {
    listByEstudante: (estudanteId: number) =>
      get<AtividadeComplementarResumo[]>(
        `atividades-complementares/estudante/${estudanteId}`
      ),
  },
  permanencia: {
    listEditais: () => get<EditalResumo[]>(`permanencia/editais`),
    getEditalById: (id: number) =>
      get<EditalResumo>(`permanencia/editais/${id}`),
    listBeneficios: (estudanteId: number) =>
      get<unknown[]>(`permanencia/estudantes/${estudanteId}/beneficios`),
  },
  apoio: {
    listCasos: () => get<CasoResumo[]>(`apoio/casos`),
    getCasoById: (id: number) => get<CasoResumo>(`apoio/casos/${id}`),
  },
  mobilidade: {
    getByEstudante: (estudanteId: number) =>
      get<MobilidadeAcademicaResumo>(`mobilidades/estudante/${estudanteId}`),
    getById: (id: number) =>
      get<MobilidadeAcademicaResumo>(`mobilidades/${id}`),
  },
  cobrancas: {
    getByContrato: (contratoId: number) =>
      get<CobrancaResumo[]>(`cobrancas/contrato/${contratoId}`),
  },
  oportunidades: {
    listAll: () => get<OportunidadeResumo[]>(`oportunidades`),
    getById: (id: number) => get<OportunidadeResumo>(`oportunidades/${id}`),
  },
};
