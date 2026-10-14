<<<<<<< HEAD
# Tarento / Fulfilment Warehouse Case Study

Java 17 + Quarkus implementation for the supplied case study. The implementation includes location resolution, warehouse creation/retrieval/archive/replacement validation, and post-commit synchronization of Store changes with the legacy gateway. Answers for the case study and code-base questions are included in `case-study/CASE_STUDY.md` and `java-assignment/QUESTIONS.md`.

## Requirements
- JDK 17+
- Docker (for PostgreSQL in dev/test) or a configured PostgreSQL database

## Run
From `java-assignment/`:

```bash
./mvnw clean verify
./mvnw quarkus:dev
```

The API runs on `http://localhost:8080`. Warehouse endpoints are defined in `src/main/resources/openapi/warehouse-openapi.yaml`. In production, configure `quarkus.datasource.*` environment-specific properties and use database migrations rather than the starter project's `drop-and-create` schema strategy.

## CI
GitHub Actions runs Maven verification on pushes and pull requests and publishes the JaCoCo report as an artifact. See `.github/workflows/ci.yml`.

## Design notes
- Warehouse lookup treats `businessUnitCode` as the API identifier and only returns active warehouses.
- Archiving preserves the historical database row; replacement archives the current row and creates a new row with the same business-unit code.
- Store-to-legacy calls are registered as transaction synchronizations and run only after a successful database commit.
- Before production, add an outbox for reliable external delivery (transaction callbacks alone cannot guarantee delivery if the process crashes after commit), authentication/authorization, migrations, structured error contracts, and observability.
=======
# Tarento_Case_Study
>>>>>>> f980d5a1e15d1557eeee687f1c1cb88463abf3df
