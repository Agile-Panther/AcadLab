import type { StatusAtividade } from "./atividades";

type AtividadeFiltravel = {
  id: number;
  estudanteId: number;
  categoriaId: number;
  descricao: string;
  status: StatusAtividade;
};

export type FiltrosAtividade = {
  busca: string;
  categoriaId: number | null;
  status: StatusAtividade | null;
};

const normalizar = (texto: string) =>
  texto
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .toLowerCase()
    .trim();

export function filtrarAtividades<T extends AtividadeFiltravel>(
  atividades: T[],
  filtros: FiltrosAtividade,
): T[] {
  const busca = normalizar(filtros.busca);
  return atividades.filter((atividade) => {
    const texto = normalizar(
      `AC-${atividade.id} ${atividade.descricao} Estudante #${atividade.estudanteId}`,
    );
    return (
      (!busca || texto.includes(busca)) &&
      (filtros.categoriaId == null || atividade.categoriaId === filtros.categoriaId) &&
      (filtros.status == null || atividade.status === filtros.status)
    );
  });
}
