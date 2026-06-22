#!/bin/sh
set -e
cd /app
mvn -pl apresentacao-backend -am clean package -DskipTests -q
exec java -jar apresentacao-backend/target/acadlab-apresentacao-backend-0.0.1-SNAPSHOT.jar
