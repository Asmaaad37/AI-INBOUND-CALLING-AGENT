#!/bin/bash
set -e

SQL_FILE="uos_db_backup.sql"
UTF8_SQL_FILE="uos_db_backup_utf8.sql"

echo "Starting PostgreSQL..."
docker compose up -d db

DB_CONTAINER=$(docker compose ps -q db)

echo "Waiting for PostgreSQL to be ready..."
until docker exec "$DB_CONTAINER" pg_isready -U postgres > /dev/null 2>&1; do
  sleep 2
done

echo "Starting application..."
docker compose up -d app

echo "Done."
echo "Application running at: http://localhost:8080"