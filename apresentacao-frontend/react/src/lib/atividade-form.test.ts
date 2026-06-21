import { describe, expect, it } from "vitest";
import { validarSubmissaoAtividade } from "./atividade-form";

describe("validarSubmissaoAtividade", () => {
  const atividadeValida = {
    categoriaId: 3,
    descricao: "Curso de arquitetura de software",
    horas: 20,
    dataRealizacao: "2026-06-20",
    identificadorCertificado: "certificado.pdf",
  };

  it.each([
    [{ ...atividadeValida, categoriaId: null }, "Selecione uma categoria."],
    [{ ...atividadeValida, descricao: " " }, "Informe a descrição da atividade."],
    [{ ...atividadeValida, horas: 0 }, "Informe uma carga horária maior que zero."],
    [{ ...atividadeValida, dataRealizacao: "" }, "Informe a data de realização."],
    [{ ...atividadeValida, identificadorCertificado: " " }, "Selecione o comprovante."],
  ])("rejeita formulário incompleto", (entrada, mensagem) => {
    expect(validarSubmissaoAtividade(entrada)).toBe(mensagem);
  });

  it("aceita formulário completo", () => {
    expect(validarSubmissaoAtividade(atividadeValida)).toBeNull();
  });
});
