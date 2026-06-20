#!/bin/bash
# Reseta o banco para estado inicial de teste (sem rebuild)
# Use após qualquer sessão de testes para garantir estado limpo

set -e

echo "→ Verificando se o banco está rodando..."
docker compose up -d db
sleep 2

echo "→ Rodando seed se necessário..."
PERIODOS=$(docker exec acadlab-db-1 psql -U acadlab -d acadlab -t -c "SELECT COUNT(*) FROM periodo_letivo;" 2>/dev/null | tr -d ' ')
if [ "$PERIODOS" = "0" ]; then
  echo "  Banco vazio — executando seed completo..."
  docker cp apresentacao-backend/src/main/resources/db/migration/V1__seed.sql acadlab-db-1:/tmp/seed.sql
  docker exec acadlab-db-1 psql -U acadlab -d acadlab -f /tmp/seed.sql > /dev/null 2>&1 || true
  echo "  Seed executado."
else
  echo "  Dados já existem — apenas resetando estado da matrícula."
fi

echo "→ Resetando estado da matrícula 1..."
docker exec acadlab-db-1 psql -U acadlab -d acadlab -c "
  DELETE FROM horario_item_matricula WHERE item_matricula_id IN (SELECT id FROM item_matricula WHERE matricula_id = 1);
  DELETE FROM item_matricula WHERE matricula_id = 1;
  UPDATE matricula SET status = 'EM_MONTAGEM' WHERE id = 1;
" > /dev/null

echo "→ Subindo todos os serviços..."
docker compose up -d

echo ""
echo "✓ Pronto! Estado resetado:"
docker exec acadlab-db-1 psql -U acadlab -d acadlab -t -c "
  SELECT 'Matrícula 1: ' || status FROM matricula WHERE id = 1;
  SELECT 'Itens: ' || COUNT(*) || ' × ' || status_item FROM item_matricula WHERE matricula_id = 1 GROUP BY status_item;
" 2>/dev/null
echo ""
echo "Acesse: http://localhost:5173"
