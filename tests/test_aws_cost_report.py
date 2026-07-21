from decimal import Decimal

from scripts.aws_cost_report import flatten_cost_response


def test_flatten_cost_response_aggregates_duplicate_groups() -> None:
    response = {
        "ResultsByTime": [
            {
                "TimePeriod": {"Start": "2026-06-01", "End": "2026-07-01"},
                "Groups": [
                    {
                        "Keys": ["AmazonCloudWatch"],
                        "Metrics": {
                            "UnblendedCost": {"Amount": "10.125", "Unit": "USD"}
                        },
                    },
                    {
                        "Keys": ["AmazonCloudWatch"],
                        "Metrics": {
                            "UnblendedCost": {"Amount": "2.125", "Unit": "USD"}
                        },
                    },
                ],
            }
        ]
    }

    rows = flatten_cost_response(response)

    assert len(rows) == 1
    assert rows[0]["service"] == "AmazonCloudWatch"
    assert Decimal(rows[0]["amount"]) == Decimal("12.25")
    assert rows[0]["currency"] == "USD"


def test_flatten_cost_response_sorts_highest_cost_first_within_period() -> None:
    response = {
        "ResultsByTime": [
            {
                "TimePeriod": {"Start": "2026-06-01", "End": "2026-07-01"},
                "Groups": [
                    {
                        "Keys": ["Service A"],
                        "Metrics": {
                            "UnblendedCost": {"Amount": "5", "Unit": "USD"}
                        },
                    },
                    {
                        "Keys": ["Service B"],
                        "Metrics": {
                            "UnblendedCost": {"Amount": "15", "Unit": "USD"}
                        },
                    },
                ],
            }
        ]
    }

    rows = flatten_cost_response(response)

    assert [row["service"] for row in rows] == ["Service B", "Service A"]


def test_flatten_cost_response_handles_empty_response() -> None:
    assert flatten_cost_response({"ResultsByTime": []}) == []
