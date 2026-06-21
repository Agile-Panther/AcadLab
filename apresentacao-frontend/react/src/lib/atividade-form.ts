export type SubmissaoAtividadeForm = {
  categoriaId: number | null;
  descricao: string;
  horas: number;
  dataRealizacao: string;
  identificadorCertificado: string;
};

export function validarSubmissaoAtividade(form: SubmissaoAtividadeForm): string | null {
  if (form.categoriaId == null) return "Selecione uma categoria.";
  if (!form.descricao.trim()) return "Informe a descrição da atividade.";
  if (!Number.isFinite(form.horas) || form.horas <= 0) {
    return "Informe uma carga horária maior que zero.";
  }
  if (!form.dataRealizacao) return "Informe a data de realização.";
  if (!form.identificadorCertificado.trim()) return "Selecione o comprovante.";
  return null;
}

export function validarHorasAprovadas(aprovadas: number, submetidas: number): string | null {
  if (!Number.isInteger(aprovadas) || aprovadas < 1) {
    return "Informe ao menos uma hora aprovada.";
  }
  if (aprovadas > submetidas) {
    return "As horas aprovadas não podem superar as submetidas.";
  }
  return null;
}

export function validarJustificativaIndeferimento(justificativa: string): string | null {
  return justificativa.trim() ? null : "Informe a justificativa do indeferimento.";
}
