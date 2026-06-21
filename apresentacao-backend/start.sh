#!/bin/sh
set -e

# Sem o rm -rf: o volume maven_repo persiste os JARs entre reinicializações.
# mvn install é incremental — só recompila módulos com fontes mais recentes que target/.
# Na primeira vez (cache vazio) faz o build completo; nas demais é rápido.
mvn install -DskipTests -q -T 1C

touch /tmp/src_stamp

start_app() {
  mvn -pl apresentacao-backend spring-boot:run -Dspring-boot.run.fork=false &
  APP_PID=$!
}

start_app

while true; do
  sleep 1
  if find . -name '*.java' -path '*/src/main/java/*' -newer /tmp/src_stamp 2>/dev/null | grep -q .; then
    echo '[hotreload] Mudança detectada, recompilando...'
    mvn install -DskipTests -q -T 1C 2>&1 | tail -5
    touch /tmp/src_stamp
    echo '[hotreload] Reiniciando aplicação...'
    kill "$APP_PID" 2>/dev/null || true
    wait "$APP_PID" 2>/dev/null || true
    start_app
  fi
done
