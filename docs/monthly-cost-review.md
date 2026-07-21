# Monthly cloud cost review

## Purpose

The review explains material cost movement, assigns ownership and agrees engineering actions without reducing reliability or security blindly. It is not a meeting to celebrate a lower total bill when the change is caused by an outage or deferred work.

## 1. Establish a comparable baseline

Compare equivalent periods and record:

- number of days in each period;
- currency and tax treatment;
- credits, refunds and one-time fees;
- reservation or savings-plan purchases;
- major launches, migrations or incidents;
- changes in traffic, customers, retention or data volume.

Normalize where useful—for example, cost per request, tenant, transaction, cluster, active user or ingested GB.

## 2. Start at service level

For each cloud service, calculate:

- current-period cost;
- prior-period cost;
- absolute variance;
- percentage variance;
- share of total cost;
- accountable engineering owner.

Investigate high absolute impact first. A 500% increase in a tiny line item may matter less than a 12% increase in the largest service.

## 3. Find the usage driver

Move from service to the dimensions that explain the bill:

- account, subscription or project;
- region and availability zone;
- resource tag and cost centre;
- usage type and operation;
- purchase option;
- data transfer path;
- log table, metric series or retention tier;
- instance family, storage class or throughput tier.

A useful finding has a causal form:

> CloudWatch cost increased because custom metric cardinality doubled after a new unbounded label was introduced—not because “monitoring became expensive.”

## 4. Classify the variance

Use one of these categories:

- **expected growth:** traffic or customer usage increased;
- **planned architecture:** a launch, migration or resilience improvement added cost;
- **price or commitment:** contract, tier or purchase-option change;
- **waste:** idle, orphaned or over-provisioned resources;
- **defect:** retry storm, log loop, data duplication or configuration error;
- **allocation gap:** spend exists but has no accountable owner;
- **timing:** delayed billing, credits or month-length effects.

## 5. Evaluate actions as engineering decisions

For every proposed saving, record:

| Field | Question |
|---|---|
| Change | What will be altered? |
| Saving hypothesis | Which measured usage and rate produce the estimate? |
| Reliability impact | Does redundancy, headroom or recovery capability reduce? |
| Security impact | Does the action remove logging, inspection or protection? |
| Delivery cost | How much engineering effort and risk are required? |
| Owner | Who implements and verifies it? |
| Verification | Which bill and operational metrics prove success? |

Do not call reduced log retention or fewer availability zones an “optimization” without documenting the resulting risk.

## 6. Review commitments separately

Before buying reservations, savings plans or commitment tiers:

- establish a stable eligible baseline;
- exclude temporary and uncertain workloads;
- model utilization and coverage separately;
- understand account-sharing and scope rules;
- compare commitment discount with flexibility lost;
- assign an owner for ongoing utilization.

## 7. Close the loop

Every action should have:

- owner;
- target date;
- expected monthly saving or avoidance;
- implementation reference;
- verification date;
- realized result.

Report estimated and realized savings separately. A recommendation is not a saving until usage and billing confirm it.

## Suggested executive summary

Keep the summary decision-oriented:

1. total cost and normalized unit cost;
2. three largest drivers of movement;
3. confirmed anomalies or defects;
4. committed actions and expected impact;
5. reliability/security trade-offs requiring leadership decision;
6. forecast and material uncertainty.
