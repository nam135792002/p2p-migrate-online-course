
from pathlib import Path
import sys
import xml.etree.ElementTree as ET


def main():
    if len(sys.argv) != 3:
        print(
            "Usage: python merge-spotbugs-reports.py "
            "<root-dir> <output.xml>"
        )
        return 2

    root_dir = Path(sys.argv[1]).resolve()
    output_file = Path(sys.argv[2]).resolve()

    reports = sorted(
        root_dir.glob("**/target/spotbugsXml.xml")
    )

    reports = [
        report for report in reports
        if ".git" not in report.parts
        and report.resolve() != output_file
    ]

    merged_root = ET.Element("BugCollection")

    total_bugs = 0

    for report in reports:
        module_dir = report.parent.parent
        module_name = module_dir.relative_to(root_dir).as_posix()

        print(f"Processing module: {module_name}")

        source_root = ET.parse(report).getroot()

        for bug in source_root.findall(".//BugInstance"):
            # Preserve the module that produced this finding
            bug.set("module", module_name)

            merged_root.append(bug)
            total_bugs += 1

    output_file.parent.mkdir(parents=True, exist_ok=True)

    ET.indent(merged_root, space="  ")

    ET.ElementTree(merged_root).write(
        output_file,
        encoding="utf-8",
        xml_declaration=True
    )

    print("========================================")
    print(f"Reports processed: {len(reports)}")
    print(f"Total bugs: {total_bugs}")
    print(f"Merged XML: {output_file}")
    print("========================================")

    return 0


if __name__ == "__main__":
    sys.exit(main())
