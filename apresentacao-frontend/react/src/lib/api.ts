// Typed API client — all requests are proxied by Vite to http://localhost:8080

async function parseErrorMessage(res: Response): Promise<string> {
  try {
    const json = await res.json();
    if (json.mensagem) return json.mensagem;
  } catch { /* fallback */ }
  return `${res.status} ${res.statusText}`;
}

async function get<T>(path: string): Promise<T | null> {
  const res = await fetch(`/backend/${path}`);
  if (res.status === 404) return null;
  if (!res.ok) throw new Error(await parseErrorMessage(res));
  return res.json() as Promise<T>;
}

async function post<T>(path: string, body?: unknown): Promise<T> {
  const res = await fetch(`/backend/${path}`, {
    method: "POST",
    headers: body !== undefined ? { "Content-Type": "application/json" } : {},
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) throw new Error(await parseErrorMessage(res));
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

async function put<T>(path: string, body?: unknown): Promise<T> {
  const res = await fetch(`/backend/${path}`, {
    method: "PUT",
    headers: body !== undefined ? { "Content-Type": "application/json" } : {},
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });
  if (!res.ok) throw new Error(await parseErrorMessage(res));
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

async function del<T>(path: string): Promise<T> {
  const res = await fetch(`/backend/${path}`, { method: "DELETE" });
  if (!res.ok) throw new Error(await parseErrorMessage(res));
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
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

export interface CandidaturaResumo {
  id: number;
  oportunidadeId: number;
  estudanteId: number;
  status: string;
}

// ─── API ──────────────────────────────────────────────────────────────────────

export const api = {
  periodos: {
    listByCurso: (cursoId: number) =>
      get<PeriodoLetivoResumo[]>(`periodos-letivos/curso/${cursoId}`).then(r => r ?? []),
    criar: (body: { cursoId: number; ano: number; semestre: number; dataInicio: string; dataFim: string }) =>
      post<void>(`periodos-letivos`, body),
  },
  turmas: {
    listByPeriodo: (periodoId: number) =>
      get<TurmaResumo[]>(`turmas/periodo/${periodoId}`).then(r => r ?? []),
    getById: (id: number) => get<TurmaResumo>(`turmas/${id}`),
    listSalas: () => get<SalaResumo[]>(`salas`).then(r => r ?? []),
    listProfessores: () => get<ProfessorResumo[]>(`professores`).then(r => r ?? []),
  },
  curriculo: {
    listByCurso: (cursoId: number) =>
      get<MatrizCurricularResumo[]>(`curriculo/curso/${cursoId}`).then(r => r ?? []),
    getById: (id: number) => get<MatrizCurricularResumo>(`curriculo/${id}`),
  },
  matricula: {
    getById: (id: number) => get<MatriculaResumo>(`matriculas/${id}`),
    iniciar: (body: { estudanteId: number; periodoLetivoId: number; limiteCreditos: number }) =>
      post<void>(`matriculas`, body),
    confirmar: (id: number, vagasPorTurma: Record<number, number>) =>
      put<void>(`matriculas/${id}/confirmar`, vagasPorTurma),
    trancarPeriodo: (id: number, body: object) =>
      put<void>(`matriculas/${id}/trancar-periodo`, body),
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
    listByEstudante: (estudanteId: number) =>
      get<SolicitacaoAcademicaResumo[]>(`solicitacoes/estudante/${estudanteId}`).then(r => r ?? []),
    listPendentes: () =>
      get<SolicitacaoAcademicaResumo[]>(`solicitacoes/pendentes`).then(r => r ?? []),
    listTodas: () =>
      get<SolicitacaoAcademicaResumo[]>(`solicitacoes/todas`).then(r => r ?? []),
    getById: (id: number) =>
      get<SolicitacaoAcademicaResumo>(`solicitacoes/${id}`),
    estatisticas: () =>
      get<Record<string, number>>(`solicitacoes/estatisticas`).then(r => r ?? {}),
    // ─── Estudante ───
    abrir: (body: {
      estudanteId: number; periodoLetivoId: number; tipo: string;
      descricao: string; documentos: { tipo: string; nomeArquivo: string }[];
    }) => post<number>(`solicitacoes`, body),
    complementar: (id: number, body: { tipo: string; nomeArquivo: string }) =>
      put<void>(`solicitacoes/${id}/complementar`, body),
    cancelar: (id: number) =>
      put<void>(`solicitacoes/${id}/cancelar`),
    // ─── Secretaria ───
    iniciarAnalise: (id: number, analistaId: number) =>
      put<void>(`solicitacoes/${id}/iniciar-analise`, { analistaId }),
    deferir: (id: number, body: { analistaId: number; justificativa: string; impactoAcademico: boolean }) =>
      put<void>(`solicitacoes/${id}/deferir`, body),
    indeferir: (id: number, body: { analistaId: number; justificativa: string }) =>
      put<void>(`solicitacoes/${id}/indeferir`, body),
    solicitarComplementacao: (id: number, analistaId: number) =>
      put<void>(`solicitacoes/${id}/solicitar-complementacao`, { analistaId }),
    concluir: (id: number) =>
      put<void>(`solicitacoes/${id}/concluir`),
    vincularEConcluir: (id: number) =>
      put<void>(`solicitacoes/${id}/vincular-e-concluir`),
  },
  integralizacao: {
    listAll: () => get<IntegralizacaoResumo[]>(`integralizacoes`).then(r => r ?? []),
    getByEstudante: (estudanteId: number) =>
      get<IntegralizacaoResumo>(`integralizacoes/estudante/${estudanteId}`),
  },
  atividades: {
    listByEstudante: (estudanteId: number) =>
      get<AtividadeComplementarResumo[]>(`atividades-complementares/estudante/${estudanteId}`).then(r => r ?? []),
    submeter: (body: { estudanteId: number; categoriaId: number; descricao: string; horasSubmetidas: number }) =>
      post<void>(`atividades-complementares`, body),
  },
  permanencia: {
    listEditais: () => get<EditalResumo[]>(`permanencia/editais`).then(r => r ?? []),
    getEditalById: (id: number) =>
      get<EditalResumo>(`permanencia/editais/${id}`),
    listBeneficios: (estudanteId: number) =>
      get<unknown[]>(`permanencia/estudantes/${estudanteId}/beneficios`).then(r => r ?? []),
  },
  apoio: {
    listCasos: () => get<CasoResumo[]>(`apoio/casos`).then(r => r ?? []),
    getCasoById: (id: number) => get<CasoResumo>(`apoio/casos/${id}`),
  },
  mobilidade: {
    getByEstudante: (estudanteId: number) =>
      get<MobilidadeAcademicaResumo>(`mobilidades/estudante/${estudanteId}`),
    getById: (id: number) =>
      get<MobilidadeAcademicaResumo>(`mobilidades/${id}`),
    solicitar: (body: { estudanteId: number; instituicaoDestino: string; status: string }) =>
      post<void>(`mobilidades`, body),
  },
  cobrancas: {
    getByContrato: (contratoId: number) =>
      get<CobrancaResumo[]>(`cobrancas/contrato/${contratoId}`).then(r => r ?? []),
  },
  oportunidades: {
    listAll: () => get<OportunidadeResumo[]>(`oportunidades`).then(r => r ?? []),
    getById: (id: number) => get<OportunidadeResumo>(`oportunidades/${id}`),
    criar: (body: { empresaId: number; descricao: string; cargaHorariaTotal: number }) =>
      post<number>(`oportunidades`, body),
    publicar: (id: number, setorId: number) =>
      put<void>(`oportunidades/${id}/publicar`, { setorId }),
    candidatar: (id: number, estudanteId: number) =>
      put<number>(`oportunidades/${id}/candidatura`, { estudanteId }),
    excluir: (id: number) => del<void>(`oportunidades/${id}`),
  },
  candidaturas: {
    listAll: () => get<CandidaturaResumo[]>(`candidaturas`).then(r => r ?? []),
    listByEstudante: (estudanteId: number) =>
      get<CandidaturaResumo[]>(`candidaturas/estudante/${estudanteId}`).then(r => r ?? []),
    deferir: (id: number) => put<void>(`candidaturas/${id}/deferir`),
    indeferir: (id: number) => put<void>(`candidaturas/${id}/indeferir`),
    cancelar: (id: number) => put<void>(`candidaturas/${id}/cancelar`),
  },
  estagios: {
    listByEstudante: (estudanteId: number) =>
      get<unknown[]>(`estagios/estudante/${estudanteId}`).then(r => r ?? []),
  },
};
