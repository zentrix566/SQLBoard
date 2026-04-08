# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

SQLBoard is a data operations management platform for operations and developers. It helps solve daily data operation pain points:
- Ad-hoc data query and modification (one-time operations)
- Save common operations (statistics, regular updates) for reuse
- Data export from production to testing with built-in desensitization
- Handles large data volumes efficiently
- All operations are logged for audit

## Common Commands

### Backend (Java Spring Boot)
```bash
cd backend
mvn clean install      # Build
mvn spring-boot:run    # Run development server
mvn test               # Run tests
```

### Frontend (Vue 3 + Vite)
```bash
cd frontend
npm install            # Install dependencies
npm run dev            # Start dev server
npm run build          # Production build
```

## Repository Structure

- `backend/` - Java Spring Boot backend
  - `backend/src/main/java/com/sqlboard/`
    - `config/` - Spring configuration (CORS, MyBatis Plus)
    - `controller/` - REST API controllers
      - `SqlController.java` - SQL execution and export
      - `DatabaseConnectionController.java` - Connection management
      - `SavedOperationController.java` - Saved operations management
    - `service/` - Business logic
      - `SqlExecutionService.java` - SQL query/update execution
      - `ExportService.java` - Large data export with desensitization
      - `DatabaseConnectionService.java` - Connection management
      - `SavedOperationService.java` - Saved operations management
    - `util/desensitization/` - Built-in desensitization utilities
    - `dto/` - Request/response DTOs
    - `entity/` - Database entities
    - `mapper/` - MyBatis Plus mappers
  - `backend/src/main/resources/`
    - `application.yml` - Configuration
    - `schema.sql` - Database initialization script

- `frontend/` - Vue 3 + Vite frontend
  - `frontend/src/`
    - `api/` - API client for backend
    - `views/` - Page components
      - `SqlExecute.vue` - Main SQL editor and execution page
      - `ConnectionList.vue` - Data source management
      - `SavedOperations.vue` - Saved common operations list
    - `router/` - Vue Router configuration

## Key Design Points

1. **Desensitization**: Already implemented in `com.sqlboard.util.desensitization`. Supports:
   - Chinese name, ID card, phone, email, bank card, address, full desensitization

2. **Large data export**: Uses Alipay EasyExcel with streaming writing to handle large datasets without OOM.

3. **Multiple databases**: Supports dynamic connections to MySQL and PostgreSQL.

4. **Operation audit**: All operations are logged to `sb_operation_history` table for tracing.
