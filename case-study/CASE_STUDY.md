# Case Study Scenarios to discuss

## Scenario 1: Cost Allocation and Tracking

**Objective:** Produce a trusted cost-per-order, cost-per-unit and cost-per-store view without double-counting shared costs.

I would first map the cost taxonomy and agree with Finance on cost ownership, accounting periods, currency, tax treatment and allocation rules. Direct costs (warehouse labour, rent, handling and transport) should be assigned to the warehouse/store where they originate. Shared overhead should use transparent drivers such as pallet-days, order lines, units handled, distance, occupied area or labour hours. The driver should reflect causality rather than simply being easy to measure.

Key questions:
- Which decisions must the reports support: pricing, network design, store profitability, staffing or budgeting?
- What is the source of truth for each cost, and how quickly is it available?
- Are costs required at warehouse, store, product, shipment or order level?
- How are returns, transfers, shared facilities, idle capacity and exceptional costs treated?
- Who owns allocation rules, and how are rule changes versioned and audited?

I would keep source transactions immutable, store allocation results with the rule version and effective dates, and reconcile allocated totals to the general ledger. Dashboards should expose both the allocated amount and the driver/assumptions so users can explain variances. I would validate the model against a small set of representative warehouses before rolling it out.

## Scenario 2: Cost Optimization Strategies

I would establish a baseline of total cost and service quality, segmented by warehouse, store, product, route and order type. Candidate initiatives include improving warehouse slotting and picking paths, consolidating shipments, increasing vehicle utilization, reducing inventory dwell and shrinkage, adjusting labour schedules to demand, and revisiting warehouse/store placement.

Prioritization should combine expected annual savings, implementation cost, time to value, confidence, operational risk and customer impact. A simple impact-versus-effort matrix helps shortlist ideas, but estimates should include one-time costs and ongoing operating costs. I would run a pilot with a control or comparable baseline, define measurable success criteria (cost per order, on-time delivery, fill rate, damage and returns), and expand only after results are validated.

Cost reductions must have guardrails: service levels, safety, compliance and employee workload should not deteriorate. Savings should be measured against a normalized baseline that accounts for volume and product mix, then tracked after implementation to confirm they persist. Finance and Operations should jointly sign off on realized savings.

## Scenario 3: Integration with Financial Systems

Integration connects operational events (receipts, picks, labour, dispatches and transfers) with financial records (invoices, payroll, freight bills and ledger postings). It reduces manual reconciliation, improves timeliness and allows operational changes to be evaluated in financial terms.

Before choosing an approach, I would identify source/target systems, ownership of each field, identifiers, required latency, volumes, data retention, security and reconciliation requirements. For near-real-time events, I would prefer an event or message-based integration where available; scheduled APIs or batch imports may be more suitable for invoices and accounting close. Each message should have a stable ID, schema version and idempotency key. Retries should use backoff, failures should go to a dead-letter/replay workflow, and monitoring should show lag and rejected records.

I would reconcile counts and monetary totals by period and legal entity, preserve source references, use least-privilege credentials and encrypt data in transit and at rest. Integration contracts and mappings need automated tests, contract/version management and a clear owner for incidents. “Real time” should be defined with the business; financial ledgers may require controlled posting windows even when operational data is streamed continuously.

## Scenario 4: Budgeting and Forecasting

Budgets translate business plans into expected costs and resource needs; forecasts update those expectations as demand and operating conditions change. Together they support capacity planning, cash management, staffing and early intervention when actuals diverge.

I would gather historical cost and volume data, seasonality, product mix, promotions, service-level targets, wage and fuel assumptions, rent/contract commitments, planned network changes and known one-off events. The model should distinguish fixed, variable and step-fixed costs, document assumptions, and allow scenarios such as base/upside/downside demand. Forecasts should be granular enough to drive decisions but not imply false precision.

The system should version approved budgets, preserve prior forecasts, compare actuals to budget and latest forecast, and explain variances by price, volume, mix and efficiency. Forecast accuracy should be measured over time (for example, weighted absolute percentage error where appropriate), with back-testing and bias checks. Finance and Operations should own assumptions together, with access controls and an approval trail.

## Scenario 5: Cost Control in Warehouse Replacement

Reusing a Business Unit Code must not erase the old warehouse's identity or financial history. The old warehouse should be archived with an effective end date, while the replacement is a new record linked to the same business unit and ideally to a `replaces`/`replacedBy` relationship. Cost transactions should reference an immutable warehouse record ID, not only the reusable business code; otherwise historical reports may incorrectly attribute old costs to the new operation.

Before replacement, I would compare remaining stock, capacity, location limits, transfer and shutdown costs, lease overlap, staffing, equipment and expected downtime. The replacement plan should include one-time transition costs and a budget baseline for the new operation. During cutover, stock quantities should reconcile and operational events should be timestamped to avoid gaps or double counting.

After cutover, report the old site's actual final costs separately from the new site's ramp-up costs, while retaining a business-unit-level roll-up for longitudinal comparisons. Audit history, access controls and an atomic replacement transaction protect the record from partial updates. Relevant measures include transition spend versus budget, cost per unit handled, utilization, service level and time to reach planned productivity.
