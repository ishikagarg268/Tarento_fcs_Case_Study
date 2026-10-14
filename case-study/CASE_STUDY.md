# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking
**Situation**: The company needs to track and allocate costs accurately across different Warehouses and Stores. The costs include labor, inventory, transportation, and overhead expenses.

**Task**: Discuss the challenges in accurately tracking and allocating costs in a fulfillment environment. Think about what are important considerations for this, what are previous experiences that you have you could related to this problem and elaborate some questions and considerations

**Questions you may have and considerations:**

Before defining the solution, I would first clarify how each cost is currently recorded and which business entity should own the cost. For example, labor costs could be associated with a Warehouse or Store, transportation costs could be associated with movements between locations, and inventory costs could depend on the product, quantity and storage location. I would also clarify whether costs need to be tracked in real time or whether daily or monthly aggregation is sufficient.
Important questions would include: What are the source systems for labor, inventory and transportation costs? How are shared overhead costs allocated between Warehouses and Stores? What identifiers can consistently connect financial records to a Warehouse, Store or Product? How should refunds, corrections and cancelled transactions be handled? What currency and accounting period rules are required?
From a technical perspective, I would maintain a clear audit trail for cost records and avoid overwriting historical cost information. I would also define consistent identifiers and timestamps so that costs can be traced back to their source. Aggregated reporting data could be separated from transactional data to avoid affecting operational workloads. Before implementation, I would also confirm the expected reporting granularity, retention requirements and reconciliation process with the finance team.

## Scenario 2: Cost Optimization Strategies
**Situation**: The company wants to identify and implement cost optimization strategies for its fulfillment operations. The goal is to reduce overall costs without compromising service quality.

**Task**: Discuss potential cost optimization strategies for fulfillment operations and expected outcomes from that. How would you identify, prioritize and implement these strategies?

**Questions you may have and considerations:**

I would first establish a baseline for the current fulfillment costs and service levels. The analysis could include warehouse operating costs, labor utilization, transportation costs, inventory holding costs, storage capacity utilization and costs associated with delayed or inefficient fulfillment. I would also identify which costs are fixed and which are variable so that optimization opportunities can be evaluated correctly.
Potential areas for optimization could include improving warehouse capacity utilization, reducing unnecessary inventory movement, optimizing transportation routes, balancing inventory between locations and improving workforce planning. I would avoid optimizing purely for cost because reducing a cost may negatively affect delivery time, stock availability or customer service.
I would prioritize opportunities using measurable criteria such as expected cost reduction, implementation effort, operational risk and impact on service quality. Each change could initially be implemented as a controlled change and measured against the baseline. The system should provide metrics that allow the business to compare expected and actual savings and identify whether the optimization has created any negative operational impact.

## Scenario 3: Integration with Financial Systems
**Situation**: The Cost Control Tool needs to integrate with existing financial systems to ensure accurate and timely cost data. The integration should support real-time data synchronization and reporting.

**Task**: Discuss the importance of integrating the Cost Control Tool with financial systems. What benefits the company would have from that and how would you ensure seamless integration and data synchronization?

**Questions you may have and considerations:**

Integration with financial systems is important because the Cost Control Tool needs reliable financial data to provide accurate cost tracking and reporting. Before designing the integration, I would identify the systems involved, the ownership of each data field, the required synchronization frequency and which system is the source of truth for each type of financial information.
I would clarify whether all data needs to be synchronized in real time or whether some information can be processed in batches. I would also define how transactions are identified, how duplicate messages are detected, and how failures or partially processed records are handled. Financial data should have a clear audit trail so that differences between the Cost Control Tool and the financial system can be investigated and reconciled.
From a technical perspective, an API or event-driven integration could be used depending on the capabilities of the existing financial systems. For asynchronous processing, reliable message delivery, idempotency and retry handling would be important. Monitoring and reconciliation reports should identify failed, delayed or inconsistent records. Security should also be considered because financial information may contain sensitive business data.

## Scenario 4: Budgeting and Forecasting
**Situation**: The company needs to develop budgeting and forecasting capabilities for its fulfillment operations. The goal is to predict future costs and allocate resources effectively.

**Task**: Discuss the importance of budgeting and forecasting in fulfillment operations and what would you take into account designing a system to support accurate budgeting and forecasting?

**Questions you may have and considerations:**

I would first understand how budgets are currently created and which business dimensions are used for planning. Important dimensions could include Warehouse, Store, Product, location, cost category and accounting period. I would also determine which historical data is available and how frequently forecasts need to be recalculated.
The system should distinguish between planned, actual and forecasted costs. It should allow authorized users to create or update budgets while preserving previous versions so that changes can be audited. Actual costs should be compared with budgets to identify variances, and significant variances should be available for investigation.
For forecasting, I would consider historical cost trends, warehouse capacity, inventory levels, expected demand, transportation costs and seasonal changes where relevant to the business. I would also clarify how frequently forecasts should be updated and whether users need scenario-based forecasting. From a technical perspective, calculations that are expensive or based on large historical datasets could be processed asynchronously, while summarized results could be stored for faster reporting.

## Scenario 5: Cost Control in Warehouse Replacement
**Situation**: The company is planning to replace an existing Warehouse with a new one. The new Warehouse will reuse the Business Unit Code of the old Warehouse. The old Warehouse will be archived, but its cost history must be preserved.

**Task**: Discuss the cost control aspects of replacing a Warehouse. Why is it important to preserve cost history and how this relates to keeping the new Warehouse operation within budget?

**Questions you may have and considerations:**

The Business Unit Code identifies the operational unit, but replacing the Warehouse should not mean losing the historical information associated with the previous Warehouse. I would therefore treat the old Warehouse as an archived historical entity and create a new active Warehouse record using the same Business Unit Code. Historical cost records should remain associated with the old Warehouse identity so that previous spending can still be audited and reported.
Before replacement, I would clarify the old Warehouse's remaining stock, capacity, outstanding costs, open transportation activities and any financial commitments. The new Warehouse should be checked against its available capacity and operational budget before it becomes active. The replacement process should also clearly define when the old Warehouse stops receiving new costs and when the new Warehouse starts receiving them.
From a technical perspective, the replacement should be atomic so that the old Warehouse is not archived without successfully creating the new Warehouse. Historical records should not be physically deleted. The system should retain timestamps and identifiers that allow reports to distinguish between the old and new Warehouse instances even though they share the same Business Unit Code.
I would also define how budgets are transferred or recalculated during the replacement. The finance team should be able to compare historical costs from the old Warehouse with the new Warehouse's planned and actual costs. This makes it possible to monitor whether the replacement remains within budget without losing the historical cost information required for financial analysis and auditing.

## Instructions for Candidates
Before starting the case study, read the [BRIEFING.md](BRIEFING.md) to quickly understand the domain, entities, business rules, and other relevant details.

**Analyze the Scenarios**: Carefully analyze each scenario and consider the tasks provided. To make informed decisions about the project's scope and ensure valuable outcomes, what key information would you seek to gather before defining the boundaries of the work? Your goal is to bridge technical aspects with business value, bringing a high level discussion; no need to deep dive.