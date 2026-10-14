# Questions

## 1. Database access and manipulation strategies

**Answer:**

I would gradually standardize persistence behind repository/adaptor interfaces. The warehouse domain already has a `WarehouseStore` port and a Panache-backed adapter, which makes use cases easier to test without a database. Product uses a `ProductRepository`, while Store uses active record methods directly on `PanacheEntity`. Active Record is concise and reasonable for a small CRUD resource, but it couples the HTTP/resource layer to persistence and makes business rules, unit tests and transaction boundaries harder to isolate.

For a maintainable codebase I would use a consistent repository approach for all aggregates, keep transaction boundaries at application-service/use-case level, and avoid leaking ORM entities beyond the persistence adapter. I would not refactor everything in one large change: first add characterization tests, then move Store operations behind a repository and migrate one endpoint at a time. I would also add database constraints for invariants such as unique active business-unit codes where practical, indexes for lookup fields, and migrations rather than relying on `drop-and-create` outside local/test environments.

## 2. OpenAPI-generated Warehouse API versus hand-coded Product and Store APIs

**Answer:**

An OpenAPI-first workflow gives consumers a versioned contract, consistent request/response schemas, generated interfaces/clients, and early contract validation. It is especially useful when multiple teams or external clients integrate with the service. Costs include build/code-generation complexity, generated code that should not be edited manually, and the need to keep the specification and implementation aligned.

Hand-coded endpoints are quick to start and offer direct control over implementation details, but contract drift, inconsistent status codes/error formats and duplicated documentation become more likely. For this service I would use OpenAPI as the source of truth for all public endpoints, generate interfaces/clients where beneficial, and keep business logic in hand-written resources/use cases/adapters. I would validate the specification in CI and add contract tests so generated signatures and actual behavior stay aligned.

## 3. Test prioritization and coverage

**Answer:**

I would prioritize tests by business risk and failure cost. First, unit tests cover domain rules and use cases: duplicate business-unit codes, unknown locations, location warehouse-count/capacity limits, stock greater than capacity, archive idempotency and replacement stock/capacity rules. These tests are fast and deterministic, using fake repositories and location resolvers.

Second, repository/integration tests verify persistence, transaction behavior, archived-versus-active lookups and replacement atomicity against a real PostgreSQL-compatible test database. REST tests cover successful CRUD paths, validation errors, response status codes and not-found behavior. For Store synchronization, a test should prove that the legacy gateway is invoked only after commit and is not invoked after rollback. The bonus fulfillment constraints need boundary tests at and beyond each maximum.

I would use JaCoCo reports in CI and enforce a coverage threshold, but treat coverage as a signal rather than proof of quality: branch coverage and assertions on business behavior matter more than executing lines. Pull requests should run formatting/compilation, unit and integration tests, and coverage checks; failures should block merge. I would review uncovered business-critical branches and add regression tests for every production bug. Test data should be isolated and deterministic, and slow integration tests can be separated from fast unit tests while still running on the main branch.
