# Cloud FinOps Toolkit

A practical AWS and Azure FinOps portfolio for investigating cost changes, measuring telemetry ingestion and turning cloud spend into engineering decisions. The toolkit deliberately separates measured usage from pricing assumptions so reports remain explainable and adaptable.

## Included tools

- AWS Cost Explorer report grouped by service
- Athena query for Cost and Usage Report analysis
- Azure Log Analytics daily ingestion query
- Parameterized ingestion-cost projection for additional daily volume
- Monthly cost-review framework covering allocation, anomalies and architecture trade-offs
- Unit tests for the report transformation logic

## Repository layout

```text
.
├── scripts/aws_cost_report.py
├── scripts/ingestion_cost_projection.py
├── tests/test_aws_cost_report.py
├── queries/aws_cur_monthly_cost.sql
├── queries/azure_daily_ingestion.kql
├── examples/sample_aws_costs.json
├── requirements.txt
└── docs/monthly-cost-review.md
```

## AWS Cost Explorer report

Use an authenticated AWS profile with read-only Cost Explorer access:

```bash
python scripts/aws_cost_report.py \
  --profile portfolio-readonly \
  --start 2026-05-01 \
  --end 2026-07-01 \
  --granularity MONTHLY \
  --output report.csv
```

The end date is exclusive, matching the Cost Explorer API. The script groups unblended cost by service and supports paginated API responses.

To inspect generated sample data without AWS access:

```bash
python scripts/aws_cost_report.py --input examples/sample_aws_costs.json
```

## Azure ingestion query

Run `queries/azure_daily_ingestion.kql` in the target Log Analytics workspace. It reports daily billable ingestion by data type and a daily total for the last 30 days.

## Project additional ingestion cost

Supply the actual contracted ingestion price rather than relying on a public list price:

```bash
python scripts/ingestion_cost_projection.py \
  --current-daily-gb 42.5 \
  --additional-daily-gb 5 \
  --price-per-gb 2.30 \
  --days 30
```

This produces baseline, incremental and projected monthly ingestion cost. Currency is intentionally user-supplied because contract, commitment tier, region and tax treatment vary.

## FinOps principles demonstrated

1. **Start with usage and allocation before optimization.**
2. **Explain the driver, not merely the percentage change.**
3. **Separate one-time, recurring and commitment costs.**
4. **Evaluate reliability and security consequences before removing spend.**
5. **Assign cost to an engineering owner and business capability.**
6. **Verify realized savings after implementation.**

## Data-safety note

All sample costs, accounts, subscriptions and workloads are generated. No employer billing exports, account IDs, resource names, negotiated prices or production usage are included.
