
from pathlib import Path
import html
import sys
import xml.etree.ElementTree as ET

source = Path(sys.argv[1])
output = Path(sys.argv[2])

root = ET.parse(source).getroot()
rows = []

for file in root.findall("file"):
    filename = Path(file.get("name", "")).name

    for error in file.findall("error"):
        rule = error.get("source", "").split(".")[-1]
        if rule.endswith("Check"):
            rule = rule[:-5]

        values = [
            filename,
            error.get("line", ""),
            error.get("column", ""),
            rule,
            error.get("message", ""),
            error.get("severity", ""),
        ]

        cells = "".join(
            f"<td>{html.escape(str(value))}</td>"
            for value in values
        )
        rows.append(f"<tr>{cells}</tr>")

document = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Checkstyle Violations</title>
<style>
body { font-family: Arial, sans-serif; margin: 32px; }
h1 { margin-bottom: 8px; }
table { width: 100%; border-collapse: collapse; }
th, td { padding: 12px; border-bottom: 1px solid #ddd;
         text-align: left; }
th { background: #f2f2f2; }
tr:hover { background: #fafafa; }
</style>
</head>
<body>
<h1>Checkstyle Violations</h1>
<p>Total violations: COUNT</p>
<table>
<thead>
<tr>
<th>File</th><th>Line</th><th>Column</th>
<th>Rule</th><th>Reason</th><th>Severity</th>
</tr>
</thead>
<tbody>
ROWS
</tbody>
</table>
</body>
</html>
"""

document = document.replace("COUNT", str(len(rows)))
document = document.replace("ROWS", "\n".join(rows))

output.parent.mkdir(parents=True, exist_ok=True)
output.write_text(document, encoding="utf-8")

print(f"Report generated: {output}")
