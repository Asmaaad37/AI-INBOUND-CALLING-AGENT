# Calling Agent Backend (ca-be)

A Spring Boot backend service for the Calling Agent application, powered by OpenAI and PostgreSQL with pgvector for vector-based search.

### Tech Stack

- **Java 21** with **Spring Boot**
- **PostgreSQL** with [pgvector](https://github.com/pgvector/pgvector) extension
- **OpenAI API** for AI capabilities
- **Docker & Docker Compose** for containerized deployment
- **Maven** for build management

### Prerequisites

- [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) installed
- (Optional) Set the `OPENAI_API_KEY` environment variable with your OpenAI API key
- (Optional) Set the `API_SECURITY_TOKEN` environment variable to override the default static API token

### Project Structure

```
ca-be/
├── src/                    # Application source code
├── docker-compose.yml      # Docker Compose services (db + app)
├── Dockerfile              # Multi-stage build for the Spring Boot app
├── pom.xml                 # Maven project configuration
├── uos_db_backup.sql       # Database backup file (UTF-16 encoded)
├── start.bat               # Windows deployment script
├── start.sh                # Linux deployment script
└── README.md
```

### Services

| Service | Container Name | Port |
|---------|---------------|------|
| PostgreSQL (pgvector) | `pgvector` | 5432 |
| Spring Boot App | `ca-be` | 8080 |

---

## Deployment

### Windows

1. Open a terminal (Command Prompt or PowerShell) and navigate to the project root:
   ```cmd
   cd path\to\ca-be
   ```

2. (Optional) Set environment variables:
   ```cmd
   set OPENAI_API_KEY=your-openai-api-key
   set API_SECURITY_TOKEN=your-api-token
   ```

3. Run the startup script:
   ```cmd
   start.bat
   ```

   This script will:
   - Start the PostgreSQL (pgvector) database container
   - Wait for PostgreSQL to be ready
   - Restore the database from `uos_db_backup.sql` (handles UTF-16 encoding)
   - Build and start the application container

4. Once complete, the app will be running at **http://localhost:8080**

### Linux

1. Open a terminal and navigate to the project root:
   ```bash
   cd path/to/ca-be
   ```

2. (Optional) Export environment variables:
   ```bash
   export OPENAI_API_KEY=your-openai-api-key
   export API_SECURITY_TOKEN=your-api-token
   ```

3. Make the startup script executable (first time only):
   ```bash
   chmod +x start.sh
   ```

4. Run the startup script:
   ```bash
   ./start.sh
   ```

   This script will:
   - Start the PostgreSQL (pgvector) database container
   - Wait for PostgreSQL to be ready
   - Restore the database from `uos_db_backup.sql` (converts from UTF-16 to UTF-8 via `iconv`)
   - Build and start the application container

5. Once complete, the app will be running at **http://localhost:8080**

---

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `OPENAI_API_KEY` | OpenAI API key for AI features | *(set in docker-compose.yml)* |
| `API_SECURITY_TOKEN` | Static token for API authentication | `my-static-api-token-2026` |

### Health Check

Verify the application is running:

```bash
curl http://localhost:8080/health
```

Expected response: `OK`

### Stopping the Application

```bash
docker compose down
```

To also remove the database volume:

```bash
docker compose down -v
```
