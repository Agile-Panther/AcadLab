import { describe, expect, it } from "vitest";
import { filtrarAtividades } from "./atividade-filtros";

const atividades = [
  { id: 1, estudanteId: 1, categoriaId: 1, descricao: "Hackathon CESAR", status: "DEFERIDA" as const },
  { id: 2, estudanteId: 1, categoriaId: 2, descricao: "Monitoria em Algoritmos", status: "DEFERIDA" as const },
  { id: 3, estudanteId: 2, categoriaId: 3, descricao: "Curso de Machine Learning", status: "PENDENTE" as const },
];

describe("filtrarAtividades", () => {
  it("busca por descrição sem diferenciar maiúsculas", () => {
    expect(filtrarAtividades(atividades, { busca: "hackathon", categoriaId: null, status: null }))
      .toHaveLength(1);
  });

  it("busca pelo protocolo", () => {
    expect(filtrarAtividades(atividades, { busca: "AC-2", categoriaId: null, status: null })[0]?.id)
      .toBe(2);
  });

  it("combina categoria e status", () => {
    expect(filtrarAtividades(atividades, { busca: "", categoriaId: 3, status: "PENDENTE" })[0]?.id)
      .toBe(3);
  });

  it("permite buscar pelo estudante na fila da coordenação", () => {
    expect(filtrarAtividades(atividades, { busca: "estudante #2", categoriaId: null, status: null })[0]?.id)
      .toBe(3);
  });
});
