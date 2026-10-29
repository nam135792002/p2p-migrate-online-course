
import html
import sys
import xml.etree.ElementTree as ET
from collections import Counter
from pathlib import Path

PRIORITIES = {
    "1": "High",
    "2": "Medium",
    "3": "Low",
    "4": "Experimental",
}

CATEGORY_LABELS = {
    "SECURITY": "Security",
    "BAD_PRACTICE": "Bad Practice",
    "CORRECTNESS": "Correctness",
    "PERFORMANCE": "Performance",
    "MALICIOUS_CODE": "Malicious Code",
    "MT_CORRECTNESS": "Multithreading",
    "STYLE": "Style",
    "I18N": "Internationalization",
    "EXPERIMENTAL": "Experimental",
    "NOISE": "Noise",
}


def text(element, path, default=""):
    found = element.find(path)
    return found.text.strip() if found is not None and found.text else default


def classify_category(category, bug_type):
    if category:
        return CATEGORY_LABELS.get(category, category.replace("_", " ").title())

    # Fallback classification if XML has no category attribute.
    if bug_type.startswith(("HRS_", "PT_", "SQL_", "XSS_", "SC_", "WEAK_")):
        return "Security"

    return "Unclassified"


def main():
    if len(sys.argv) != 3:
        print(
            "Usage: python generate-spotbugs-report.py "
            "<spotbugs.xml> <report.html>"
        )
        return 2

    xml_path = Path(sys.argv[1])
    output_path = Path(sys.argv[2])

    if not xml_path.is_file():
        print(f"SpotBugs XML not found: {xml_path}")
        return 1

    root = ET.parse(xml_path).getroot()
    bugs = []

    for bug in root.findall(".//BugInstance"):
        bug_type = bug.get("type", "UNKNOWN")
        priority_code = bug.get("priority", "3")
        category_code = bug.get("category", "")

        source = bug.find(".//SourceLine")
        source_file = source.get("sourcefile", "") if source is not None else ""
        line = source.get("start", "") if source is not None else ""

        class_name = text(bug, "./Class", "Unknown")
        method_name = text(bug, "./Method", "")

        bugs.append({
            "type": bug_type,
            "category": classify_category(category_code, bug_type),
            "priority": PRIORITIES.get(priority_code, "Unknown"),
            "class": class_name,
            "method": method_name,
            "file": source_file,
            "line": line,
            "message": bug.get("message", ""),
        })

    counts = Counter(bug["priority"] for bug in bugs)
    rows = []

    for bug in bugs:
        values = [
            bug["type"], bug["category"], bug["priority"],
            bug["class"], bug["method"],
            bug["file"], bug["line"], bug["message"],
        ]
        cells = "".join(
            f"<td>{html.escape(str(value))}</td>" for value in values
        )
        rows.append(f"<tr>{cells}</tr>")

    summary = " | ".join(
        f"{name}: {counts.get(name, 0)}"
        for name in ("High", "Medium", "Low", "Experimental")
    )

    document = f"""<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>SpotBugs Report</title>
<style>
body {{ font-family: Arial, sans-serif; margin: 24px; color: #222; }}
h1 {{ margin-bottom: 8px; }}
.summary {{ padding: 12px; background: #f2f4f7; margin-bottom: 16px; }}
input {{ padding: 9px; width: min(500px, 90%); margin-bottom: 12px; }}
table {{ border-collapse: collapse; width: 100%; font-size: 13px; }}
th, td {{ border: 1px solid #ddd; padding: 8px; text-align: left; }}
th {{ background: #e9edf2; position: sticky; top: 0; }}
tr:nth-child(even) {{ background: #f8f9fb; }}
td {{ overflow-wrap: anywhere; }}
</style>
</head>
<body>
<h1>SpotBugs Report</h1>
<div class="summary">
Total bugs: {len(bugs)}<br>{html.escape(summary)}
</div>
<input id="filter" placeholder="Filter bug type, category, priority, class..."
       oninput="filterRows()">
<table id="bugs">
<thead><tr>
<th>Bug Type</th><th>Category</th><th>Priority</th>
<th>Class</th><th>Method</th><th>Source File</th>
<th>Line</th><th>Details</th>
</tr></thead>
<tbody>{''.join(rows)}</tbody>
</table>
<script>
function filterRows() {{
  const query = document.getElementById('filter').value.toLowerCase();
  document.querySelectorAll('#bugs tbody tr').forEach(row => {{
    row.hidden = !row.innerText.toLowerCase().includes(query);
  }});
}}
</script>
</body>
</html>
"""

    output_path.parent.mkdir(parents=True, exist_ok=True)
    output_path.write_text(document, encoding="utf-8")
    print(f"Report generated: {output_path}")
    print(f"Total bugs: {len(bugs)}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
