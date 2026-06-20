ALTER TABLE atividade_complementar
    ADD COLUMN IF NOT EXISTS contabilizada_integralizacao BOOLEAN NOT NULL DEFAULT FALSE;
