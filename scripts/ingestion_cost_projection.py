#!/usr/bin/env python3
"""Project monthly telemetry ingestion cost from explicit usage and price inputs."""

from __future__ import annotations

import argparse
from decimal import Decimal, InvalidOperation


def decimal_value(value: str) -> Decimal:
    try:
        parsed = Decimal(value)
    except InvalidOperation as exc:
        raise argparse.ArgumentTypeError(f"Invalid decimal value: {value}") from exc
    if parsed < 0:
        raise argparse.ArgumentTypeError("Values must be zero or greater")
    return parsed


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--current-daily-gb", type=decimal_value, required=True)
    parser.add_argument("--additional-daily-gb", type=decimal_value, required=True)
    parser.add_argument("--price-per-gb", type=decimal_value, required=True)
    parser.add_argument("--days", type=int, default=30)
    parser.add_argument("--currency", default="USD")
    args = parser.parse_args()

    if args.days <= 0:
        parser.error("--days must be greater than zero")

    days = Decimal(args.days)
    current_monthly_gb = args.current_daily_gb * days
    additional_monthly_gb = args.additional_daily_gb * days
    projected_monthly_gb = current_monthly_gb + additional_monthly_gb

    baseline_cost = current_monthly_gb * args.price_per_gb
    incremental_cost = additional_monthly_gb * args.price_per_gb
    projected_cost = projected_monthly_gb * args.price_per_gb

    print("Telemetry ingestion projection")
    print("-" * 42)
    print(f"Period:                  {args.days} days")
    print(f"Current daily ingestion: {args.current_daily_gb:.2f} GB")
    print(f"Additional daily volume: {args.additional_daily_gb:.2f} GB")
    print(f"Projected daily volume:  {(args.current_daily_gb + args.additional_daily_gb):.2f} GB")
    print(f"Price assumption:        {args.currency} {args.price_per_gb:.4f}/GB")
    print()
    print(f"Baseline monthly volume: {current_monthly_gb:.2f} GB")
    print(f"Added monthly volume:    {additional_monthly_gb:.2f} GB")
    print(f"Projected monthly volume:{projected_monthly_gb:>10.2f} GB")
    print()
    print(f"Baseline cost:           {args.currency} {baseline_cost:.2f}")
    print(f"Incremental cost:        {args.currency} {incremental_cost:.2f}")
    print(f"Projected cost:          {args.currency} {projected_cost:.2f}")
    print()
    print("This is a linear ingestion estimate. Add commitment-tier, retention,")
    print("archive, query, export, tax and currency effects separately.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
