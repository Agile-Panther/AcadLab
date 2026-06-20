CREATE TABLE IF NOT EXISTS ACORDO (
    id SERIAL PRIMARY KEY,
    estudanteId INT NOT NULL,
    prazo DATE NOT NULL,
    descontoPercentual INT NOT NULL CHECK (descontoPercentual BETWEEN 0 AND 50),
    observacoes TEXT,
    criadoEm TIMESTAMP NOT NULL DEFAULT NOW()
);
