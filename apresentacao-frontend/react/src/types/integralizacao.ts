export interface ItemChecklistResumo {
  tipo: string;
  descricao: string;
  cumprido: boolean;
}

export interface IntegralizacaoResumo {
  id: number;
  estudanteId: number;
  matrizCurricularId: number;
  status: 'EM_ANALISE' | 'APTO' | 'INAPTO';
  observacao: string | null;
  aprovadorId: number | null;
  dataAprovacao: string | null;
  itensChecklist: ItemChecklistResumo[];
}

export interface ColacaoResumo {
  id: number;
  estudanteId: number;
  integralizacaoId: number;
  dataAptidaoAprovada: string | null;
  dataCerimonia: string | null;
  horario: string | null;
  local: string | null;
  modalidade: string | null;
  observacoes: string | null;
}

export interface IniciarAnaliseRequest {
  estudanteId: number;
  matrizCurricularId: number;
}

export interface ItemChecklistRequest {
  tipo: string;
  descricao: string;
  cumprido: boolean;
}

export interface ResultadoRequest {
  resultado: string;
}

export interface AprovarAptidaoRequest {
  coordenadorId: number;
}

export interface RegistrarColacaoRequest {
  dataCerimonia: string;
  horario: string | null;
  local: string;
  modalidade: string | null;
  observacoes: string | null;
}

export interface EstudanteInfo {
  id: number;
  nome: string;
  matricula: string;
  curso: string;
  periodo: string;
}
