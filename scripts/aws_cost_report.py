#!/usr/bin/env python3
"""Generate a service-level AWS cost report from Cost Explorer or sample JSON."""

from __future__ import annotations

import argparse
import csv
import json
import sys
from collections import defaultdict
from datetime import date
from decimal import Decimal, InvalidOperation
from pathlib import Path
from typing import Any, Iterable


def parse_iso_date(value: str) -> str:
    """Validate and return an ISO-8601 calendar date."""
    try:
        return date.fromisoformat(value).isoformat()
    except ValueError as exc:
        raise argparse.ArgumentTypeError(f"Invalid date '{value}'; use YYYY-MM-DD") from exc


def fetch_cost_explorer(
    *,
    start: str,
    end: str,
    granularity: str,
    profile: str | None = None,
    metric: str = "UnblendedCost",
) -> dict[str, Any]:
    """Fetch every page of a Cost Explorer service-grouped cost query."""
    try:
        import boto3
        from botocore.exceptions import BotoCoreError, ClientError
    except ImportError as exc:
        raise RuntimeError("boto3 is required for live AWS queries; install requirements.txt") from exc

    session = boto3.Session(profile_name=profile) if profile else boto3.Session()
    client = session.client("ce", region_name="us-east-1")

    request: dict[str, Any] = {
        "TimePeriod": {"Start": start, "End": end},
        "Granularity": granularity,
        "Metrics": [metric],
        "GroupBy": [{"Type": "DIMENSION", "Key": "SERVICE"}],
    }

    combined: dict[str, Any] = {"ResultsByTime": []}
    next_token: str | None = None

    try:
        while True:
            if next_token:
                request["NextPageToken"] = next_token
            response = client.get_cost_and_usage(**request)
            combined["ResultsByTime"].extend(response.get("ResultsByTime", []))
            next_token = response.get("NextPageToken")
            if not next_token:
                break
    except (BotoCoreError, ClientError) as exc:
        raise RuntimeError(f"AWS Cost Explorer query failed: {exc}") from exc

    return combined


def flatten_cost_response(
    response: dict[str, Any], metric: str = "UnblendedCost"
) -> list[dict[str, str]]:
    """Flatten and aggregate Cost Explorer response groups into report rows."""
    totals: defaultdict[tuple[str, str, str, str], Decimal] = defaultdict(Decimal)

    for period in response.get("ResultsByTime", []):
        time_period = period.get("TimePeriod", {})
        start = str(time_period.get("Start", "unknown"))
        end = str(time_period.get("End", "unknown"))

        for group in period.get("Groups", []):
            keys = group.get("Keys", [])
            service = str(keys[0]) if keys else "Unallocated"
            metric_data = group.get("Metrics", {}).get(metric, {})
            amount_raw = str(metric_data.get("Amount", "0"))
            unit = str(metric_data.get("Unit", "USD"))

            try:
                amount = Decimal(amount_raw)
            except InvalidOperation as exc:
                raise ValueError(
                    f"Invalid amount '{amount_raw}' for {service} in {start}"
                ) from exc

            totals[(start, end, service, unit)] += amount

    rows = [
        {
            "period_start": start,
            "period_end": end,
            "service": service,
            "amount": f"{amount.quantize(Decimal('0.01'))}",
            "currency": unit,
        }
        for (start, end, service, unit), amount in totals.items()
    ]

    return sorted(rows, key=lambda row: (row["period_start"], -Decimal(row["amount"]), row["service"]))


def write_csv(rows: Iterable[dict[str, str]], output: Path) -> None:
    """Write report rows to CSV."""
    output.parent.mkdir(parents=True, exist_ok=True)
    fieldnames = ["period_start", "period_end", "service", "amount", "currency"]
    with output.open("w", encoding="utf-8", newline="") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(rows)


def print_report(rows: list[dict[str, str]]) -> None:
    """Print a compact human-readable report and period totals."""
    if not rows:
        print("No cost rows returned.")
        return

    print(f"{'PERIOD':<12} {'AMOUNT':>14} {'CCY':<4} SERVICE")
    print("-" * 80)
    totals: defaultdict[tuple[str, str], Decimal] = defaultdict(Decimal)

    for row in rows:
        print(
            f"{row['period_start']:<12} {row['amount']:>14} "
            f"{row['currency']:<4} {row['service']}"
        )
        totals[(row["period_start"], row["currency"])] += Decimal(row["amount"])

    print("-" * 80)
    for (period, currency), total in sorted(totals.items()):
        print(f"{period:<12} {total.quantize(Decimal('0.01')):>14} {currency:<4} TOTAL")


def build_parser() -> argparse.ArgumentParser:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--start", type=parse_iso_date, help="Inclusive start date")
    parser.add_argument("--end", type=parse_iso_date, help="Exclusive end date")
    parser.add_argument(
        "--granularity",
        choices=["DAILY", "MONTHLY"],
        default="MONTHLY",
        help="Cost Explorer aggregation interval",
    )
    parser.add_argument("--profile", help="Optional AWS CLI profile")
    parser.add_argument(
        "--input",
        type=Path,
        help="Read a Cost Explorer-shaped JSON file instead of calling AWS",
    )
    parser.add_argument("--output", type=Path, help="Optional CSV output path")
    return parser


def main() -> int:
    args = build_parser().parse_args()

    if args.input:
        try:
            response = json.loads(args.input.read_text(encoding="utf-8"))
        except (OSError, json.JSONDecodeError) as exc:
            print(f"ERROR: Could not read input JSON: {exc}", file=sys.stderr)
            return 2
    else:
        if not args.start or not args.end:
            print("ERROR: --start and --end are required for live AWS queries", file=sys.stderr)
            return 2
        if args.end <= args.start:
            print("ERROR: --end must be later than --start", file=sys.stderr)
            return 2
        try:
            response = fetch_cost_explorer(
                start=args.start,
                end=args.end,
                granularity=args.granularity,
                profile=args.profile,
            )
        except RuntimeError as exc:
            print(f"ERROR: {exc}", file=sys.stderr)
            return 1

    try:
        rows = flatten_cost_response(response)
    except ValueError as exc:
        print(f"ERROR: {exc}", file=sys.stderr)
        return 2

    print_report(rows)
    if args.output:
        write_csv(rows, args.output)
        print(f"\nCSV written to {args.output}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
