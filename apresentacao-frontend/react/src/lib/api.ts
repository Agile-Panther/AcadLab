import { API_BASE_URL } from "./config";

class ApiError extends Error {
  constructor(
    message: string,
    public status: number,
  ) {
    super(message);
    this.name = "ApiError";
  }
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const res = await fetch(`${API_BASE_URL}/${path}`, {
    method,
    headers: body !== undefined ? { "Content-Type": "application/json" } : undefined,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  if (!res.ok) {
    throw new ApiError(await extractErrorMessage(res), res.status);
  }

  // 204 / corpo vazio
  const text = await res.text();
  return (text ? JSON.parse(text) : undefined) as T;
}

async function extractErrorMessage(res: Response): Promise<string> {
  try {
    const data = await res.clone().json();
    if (data && typeof data.message === "string" && data.message.trim()) {
      return data.message;
    }
  } catch {
    /* corpo não-JSON */
  }
  return `Erro ${res.status} ao comunicar com o servidor.`;
}

export const api = {
  get: <T>(path: string) => request<T>("GET", path),
  post: <T>(path: string, body?: unknown) => request<T>("POST", path, body ?? {}),
  put: <T>(path: string, body?: unknown) => request<T>("PUT", path, body ?? {}),
  delete: <T>(path: string) => request<T>("DELETE", path),
};

/**
 * Data de hoje no formato ISO (YYYY-MM-DD) esperado pelos LocalDate do backend.
 * Usa o calendário LOCAL (não UTC): `toISOString()` converte para UTC e, à noite
 * em fusos negativos (ex.: Brasil, UTC−3), retornaria o dia seguinte — fazendo
 * editais que encerram "hoje" parecerem fora do prazo.
 */
export function hojeIso(): string {
  const d = new Date();
  const ano = d.getFullYear();
  const mes = String(d.getMonth() + 1).padStart(2, "0");
  const dia = String(d.getDate()).padStart(2, "0");
  return `${ano}-${mes}-${dia}`;
}

export { ApiError };
