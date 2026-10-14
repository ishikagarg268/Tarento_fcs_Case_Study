# Questions

Here we have 3 questions related to the code base for you to answer. It is not about right or wrong, but more about what's the reasoning behind your decisions.

1. In this code base, we have some different implementation strategies when it comes to database access layer and manipulation. If you would maintain this code base, would you refactor any of those? Why?

**Answer:**
```txt
Yes, I would gradually standardize the database access approach. The Product and Store implementations currently use Panache repositories/entities directly from the REST resources, while the Warehouse implementation separates the domain model, repository port, database adapter and use cases. I would prefer the Warehouse-style separation for business-critical functionality because it keeps business rules independent from the persistence technology and makes the use cases easier to unit test.

I would not refactor everything immediately because the existing Product and Store code is simple CRUD functionality and already works. I would introduce the separation incrementally when those areas require more business rules or significant changes. This would reduce unnecessary refactoring risk while giving the code base a more consistent structure over time. I would not refactor everything immediately because the existing Product and Store code is simple CRUD functionality and already works. I would introduce the separation incrementally when those areas require more business rules or significant changes. This would reduce unnecessary refactoring risk while giving the code base a more consistent structure over time. I would also keep transaction boundaries around the application operation so that related database changes are handled atomically.
```
----
2. When it comes to API spec and endpoints handlers, we have an Open API yaml file for the `Warehouse` API from which we generate code, but for the other endpoints - `Product` and `Store` - we just coded directly everything. What would be your thoughts about what are the pros and cons of each approach and what would be your choice?

**Answer:**
```txt
The OpenAPI-first approach provides a clear API contract before implementation starts. It makes the request and response models explicit, allows the API specification to be shared with consumers, and can generate interfaces and models consistently. The downside is that the generated code can add build-time complexity and developers need to keep the specification and implementation aligned. For smaller internal APIs, this can sometimes feel like additional overhead.

The code-first approach used by Product and Store is simpler to start with because developers can define the endpoint directly in Java and change the implementation quickly. However, the API contract can become less explicit and documentation can drift from the actual implementation. For this project, I would prefer OpenAPI-first for externally consumed or important APIs such as Warehouse, while code-first can remain reasonable for simple internal CRUD APIs. If the application grows and the APIs are consumed by multiple clients, I would gradually standardize on OpenAPI-first to provide consistent contracts and API documentation.
```
----
3. Given the need to balance thorough testing with time and resource constraints, how would you prioritize and implement tests for this project? Which types of tests would you focus on, and how would you ensure test coverage remains effective over time?

**Answer:**
```txt
I would prioritize unit tests around the business rules first because they provide fast feedback and cover the most important behaviour. In this project, that includes warehouse creation, duplicate business unit validation, location validation, maximum warehouse count, cumulative location capacity, archiving and warehouse replacement rules. I would then add REST/integration tests for the important API flows to verify the interaction between the resource, database and generated API contract. Tests for Store and Product CRUD operations would cover the main success and error scenarios.

To keep coverage effective over time, I would run the tests automatically in CI for every pull request and maintain a coverage threshold, while avoiding writing tests only to increase the percentage. I would also add regression tests whenever a production defect is fixed and review coverage when new business rules are introduced. This gives a balance between meaningful functional coverage, fast unit-test execution and a smaller number of integration tests that verify the complete application behaviour.
```