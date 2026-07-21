# Contributing

This is a public engineering portfolio. Contributions should improve reusable patterns without introducing employer, customer or production information.

## Before opening a pull request

1. Use generated data, documentation-only domains and placeholder cloud identifiers.
2. Never commit credentials, billing exports, negotiated rates, internal resource names or account/subscription IDs.
3. Keep pricing inputs explicit; do not present a public list price as a user's contracted price.
4. Add or update tests when report transformation logic changes.
5. Run:

```bash
python -m pip install -r requirements.txt
python -m compileall -q scripts tests
pytest -q
python scripts/aws_cost_report.py --input examples/sample_aws_costs.json
```

## Pull-request notes

Explain the measured usage, pricing assumption, allocation dimension or engineering decision affected by the change. Cost reduction must not be presented without its reliability and security trade-offs.
