/**
 * Identidades fixas usadas enquanto não há autenticação no sistema.
 * Correspondem a registros existentes no seed do banco (V1__seed.sql).
 * Centralizado aqui para facilitar a troca quando o login for implementado.
 */
export const USUARIO_ATUAL = {
  /** Estudante "Você" — Maria Santos (id=1 no seed). */
  estudanteId: 1,
  /** Psicopedagogo responsável pelos casos (id=10 no seed). */
  psicopedagogoId: 10,
  /** Assistência Estudantil que defere/indefere inscrições. */
  assistenciaId: 1,
  /** Contrato acadêmico do estudante atual (contrato_id=1 no seed). */
  contratoId: 1,
} as const;

/** Base da API do backend. Em docker usa VITE_API_TARGET; localmente cai em :8080. */
const env = import.meta.env as Record<string, string | undefined>;
export const API_BASE_URL = (env.VITE_API_TARGET ?? "http://localhost:8080") + "/backend";
