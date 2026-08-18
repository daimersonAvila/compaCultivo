#!/usr/bin/env bash
set -e

if [ ! -f .env ]; then
  echo "No encontré .env — copiando .env.example. Llénalo antes de continuar."
  cp .env.example .env
  exit 1
fi

export $(grep -v '^#' .env | xargs)

echo "Levantando PostgreSQL con Docker..."
docker-compose up -d

echo "Esperando a que la base de datos esté lista..."
until docker exec compacultivo-db pg_isready -U admin -d compacultivo > /dev/null 2>&1; do
  sleep 1
done

echo "Arrancando Spring Boot en http://localhost:8080 ..."
mvn spring-boot:run
