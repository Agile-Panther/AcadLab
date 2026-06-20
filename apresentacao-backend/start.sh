#!/bin/sh
set -e

rm -rf /root/.m2/repository/school
mvn install -DskipTests -q

touch /tmp/src_stamp

start_app() {
  mvn -pl apresentacao-backend spring-boot:run -Dspring-boot.run.fork=false &
  APP_PID=$!
}

start_app

while true; do
  sleep 2
  if find . -name '*.java' -path '*/src/main/java/*' -newer /tmp/src_stamp 2>/dev/null | grep -q .; then
    echo '[hotreload] Mudança detectada, recompilando...'
    mvn compile -pl dominio-compartilhado,dominio-matricula,aplicacao,infraestrutura,apresentacao-backend -DskipTests -q 2>&1 | tail -5
    touch /tmp/src_stamp
    echo '[hotreload] Reiniciando aplicação...'
    kill "$APP_PID" 2>/dev/null || true
    wait "$APP_PID" 2>/dev/null || true
    start_app
  fi
done
