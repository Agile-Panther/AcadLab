/** Converte data ISO (YYYY-MM-DD) do backend para DD/MM/AAAA. Retorna "—" se ausente. */
export function formatData(iso: string | null | undefined): string {
  if (!iso) return "—";
  const [ano, mes, dia] = iso.split("-");
  if (!ano || !mes || !dia) return iso;
  return `${dia}/${mes}/${ano}`;
}

/** Converte data-hora ISO (YYYY-MM-DDTHH:mm[:ss]) para DD/MM/AAAA às HH:mm. */
export function formatDataHora(iso: string | null | undefined): string {
  if (!iso) return "—";
  const [data, hora] = iso.split("T");
  if (!data || !hora) return formatData(iso);
  return `${formatData(data)} às ${hora.slice(0, 5)}`;
}

/** Agora no formato aceito por <input type="datetime-local"> (YYYY-MM-DDTHH:mm), em hora local. */
export function agoraParaInput(): string {
  const d = new Date();
  const local = new Date(d.getTime() - d.getTimezoneOffset() * 60000);
  return local.toISOString().slice(0, 16);
}

/** Converte data ISO (YYYY-MM-DD) para "MM/AAAA". Retorna "—" se ausente. */
export function formatValidade(iso: string | null | undefined): string {
  if (!iso) return "—";
  const [ano, mes] = iso.split("-");
  if (!ano || !mes) return iso;
  return `${mes}/${ano}`;
}

/** Formata número/decimal do backend como moeda BRL (ex.: 1420 → "R$ 1.420,00"). */
export function formatMoeda(valor: number | string | null | undefined): string {
  if (valor === null || valor === undefined) return "—";
  const n = typeof valor === "string" ? Number(valor) : valor;
  if (Number.isNaN(n)) return "—";
  return n.toLocaleString("pt-BR", { style: "currency", currency: "BRL" });
}
