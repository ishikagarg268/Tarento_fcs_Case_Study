# Tarento Fulfilment Case Study (FCS)

A Java 17 / Quarkus REST application for the Tarento fulfilment case study. It covers warehouse lifecycle operations, location constraints, fulfilment assignments, product endpoints, and synchronization of Store changes with a legacy integration.

## Repository layout

```text
.
├── README.md                         # Project overview and runbook
├── case-study/
│   ├── BRIEFING.md                   # Case-study context
│   └── CASE_STUDY.md                 # Design and case-study answers
├── .github/workflows/ci.yml          # GitHub Actions verification
└── java-assignment/
    ├── CODE_ASSIGNMENT.md            # Original assignment
    ├── QUESTIONS.md                  # Code-base questions and answers
    ├── pom.xml
    └── src/
        ├── main/java/                # REST adapters, domain and persistence
        ├── main/resources/           # Quarkus config, seed data and OpenAPI
        └── test/java/                # Unit and endpoint tests
```

## Requirements

- JDK 17 (the Maven compiler is configured for Java 17)
- Docker Desktop / Docker Engine for Quarkus Dev Services to start PostgreSQL during tests and development, **or** a reachable PostgreSQL instance with the datasource configured
- Git, if you intend to run or update the GitHub Actions workflow

Check your Java version with `java -version`. Docker-backed tests require the Docker daemon to be running, not merely the Docker CLI installed.

## Build and test

Run commands from `java-assignment/`.

**Linux / macOS / Git Bash**

```bash
./mvnw --batch-mode clean verify
```

**Windows Command Prompt**

```bat
mvnw.cmd --batch-mode clean verify
```

The `verify` lifecycle runs the Maven tests and generates the JaCoCo coverage report at `target/site/jacoco/` (relative to `java-assignment/`) when the build reaches the report phase. A failing test or unavailable database container can prevent that report from being generated.

## Run locally

From `java-assignment/`:

```bash
./mvnw quarkus:dev
```

On Windows CMD, use `mvnw.cmd quarkus:dev`. The API is served at `http://localhost:8080`; the Quarkus landing page is available at `/index.html`. Warehouse API details are defined in `src/main/resources/openapi/warehouse-openapi.yaml` (relative to `java-assignment/`).

The project uses Quarkus datasource Dev Services for non-production profiles when no explicit datasource URL is configured. In the `prod` profile, datasource values are configured in `src/main/resources/application.properties`; replace local example settings with environment-specific secrets and connection details before deployment.

## Main behaviour and validation

- Warehouse creation validates required fields, non-negative stock/capacity, stock not exceeding capacity, known locations, per-location warehouse limits, and aggregate location capacity.
- Warehouse replacement preserves the business-unit code and existing stock, archives the previous record, and creates the replacement within one transaction.
- Archived warehouses are retained for history and excluded from active lookups and active capacity calculations.
- Fulfilment assignments validate that the Store, Product, and active Warehouse exist; duplicates are rejected; limits are enforced: at most 3 warehouses per Store, 2 warehouses for a Product within a Store, and 5 product types per Warehouse.
- Store changes are synchronized to the legacy gateway after the database transaction commits. For production-grade delivery guarantees, use a transactional outbox and retry policy.
- Warehouse and fulfilment validation are kept in dedicated validator classes rather than embedded in REST resource methods.

## Continuous integration

The workflow in `.github/workflows/ci.yml` runs `./mvnw --batch-mode clean verify` on pushes, pull requests, and manual dispatch, then attempts to upload the JaCoCo report. The Maven wrapper script must retain its executable bit in Git for Linux runners. If you see `./mvnw: Permission denied`, fix it with:

```bash
git update-index --chmod=+x java-assignment/mvnw
git commit -m "Make Maven wrapper executable"
git push
```

## Important production follow-ups

This repository is a case-study implementation, not a complete production deployment. Before production use, add schema migrations instead of `drop-and-create`, authentication and authorization, consistent structured API error responses, request/transaction observability, database-level constraints for invariants, and reliable asynchronous delivery for legacy synchronization. Review concurrency behaviour around location-capacity and fulfilment-limit checks; application-level checks alone may race under concurrent requests.

## Further documentation

- Assignment requirements: [`java-assignment/CODE_ASSIGNMENT.md`](java-assignment/CODE_ASSIGNMENT.md)
- Code-base questions: [`java-assignment/QUESTIONS.md`](java-assignment/QUESTIONS.md)
- Case-study discussion: [`case-study/CASE_STUDY.md`](case-study/CASE_STUDY.md)
- OpenAPI contract: [`java-assignment/src/main/resources/openapi/warehouse-openapi.yaml`](java-assignment/src/main/resources/openapi/warehouse-openapi.yaml)
