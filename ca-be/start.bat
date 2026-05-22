@echo off
cd /d %~dp0

docker compose up -d db

echo Waiting for PostgreSQL to be ready...
:wait
docker exec pgvector pg_isready -U postgres >nul 2>&1
if errorlevel 1 (
    timeout /t 1 /nobreak >nul
    goto wait
)

echo Restoring database from uos_db_backup.sql...
powershell -Command "[System.IO.File]::ReadAllText('uos_db_backup.sql', [System.Text.Encoding]::Unicode) | & docker exec -i pgvector psql -U postgres postgres"

echo Starting app...
docker compose up --build -d app

echo Done. App running at http://localhost:8080
