
from pathlib import Path
import sys
import xml.etree.ElementTree as ET


def main():
    root_dir = Path(sys.argv[1]).resolve()
    output_file = Path(sys.argv[2]).resolve()

    reports = sorted(
        root_dir.glob("**/target/checkstyle-result.xml")
    )

    reports = [
        report for report in reports
        if ".git" not in report.parts
        and report.resolve() != output_file
    ]

    merged_root = ET.Element("checkstyle")

    total_files = 0
    total_errors = 0

    for report in reports:
        module_dir = report.parent.parent

        module_name = module_dir.relative_to(root_dir)

        print(f"Processing module: {module_name}")

        source_root = ET.parse(report).getroot()

        for file_element in source_root.findall("file"):
            original_name = Path(file_element.get("name", ""))

            try:
                relative_name = original_name.resolve().relative_to(
                    module_dir.resolve()
                )
            except (ValueError, OSError):
                relative_name = original_name

            file_element.set(
                "name",
                (module_name / relative_name).as_posix()
            )

            merged_root.append(file_element)

            total_files += 1
            total_errors += len(file_element.findall("error"))

    output_file.parent.mkdir(parents=True, exist_ok=True)

    ET.indent(merged_root, space="  ")

    ET.ElementTree(merged_root).write(
        output_file,
        encoding="utf-8",
        xml_declaration=True
    )

    print("========================================")
    print(f"Modules processed: {len(reports)}")
    print(f"Files analyzed: {total_files}")
    print(f"Total violations: {total_errors}")
    print(f"Merged XML: {output_file}")
    print("========================================")


if __name__ == "__main__":
    main()
