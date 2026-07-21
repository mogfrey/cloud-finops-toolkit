-- Replace cur_database.cur_table with the Glue database and table for your
-- AWS Cost and Usage Report. This query uses unblended cost for transparency;
-- adapt it to your organization's amortized-cost methodology where required.

WITH monthly_usage AS (
    SELECT
        date_trunc('month', line_item_usage_start_date) AS usage_month,
        COALESCE(NULLIF(product_product_name, ''), line_item_product_code) AS service,
        line_item_usage_type AS usage_type,
        line_item_operation AS operation,
        line_item_availability_zone AS availability_zone,
        SUM(CAST(line_item_usage_amount AS DOUBLE)) AS usage_amount,
        MAX(line_item_usage_unit) AS usage_unit,
        SUM(CAST(line_item_unblended_cost AS DOUBLE)) AS unblended_cost
    FROM cur_database.cur_table
    WHERE line_item_usage_start_date >= date_add('month', -3, current_date)
      AND line_item_line_item_type IN (
          'Usage',
          'DiscountedUsage',
          'SavingsPlanCoveredUsage',
          'Fee',
          'RIFee',
          'SavingsPlanRecurringFee'
      )
    GROUP BY 1, 2, 3, 4, 5
),
ranked AS (
    SELECT
        *,
        ROW_NUMBER() OVER (
            PARTITION BY usage_month, service
            ORDER BY unblended_cost DESC
        ) AS cost_rank_within_service
    FROM monthly_usage
)
SELECT
    usage_month,
    service,
    usage_type,
    operation,
    availability_zone,
    ROUND(usage_amount, 3) AS usage_amount,
    usage_unit,
    ROUND(unblended_cost, 2) AS unblended_cost
FROM ranked
WHERE cost_rank_within_service <= 20
  AND ABS(unblended_cost) >= 0.01
ORDER BY usage_month DESC, unblended_cost DESC;
