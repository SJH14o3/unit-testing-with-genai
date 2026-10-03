#!/usr/bin/env python3

import argparse
import xml.etree.ElementTree as ET
from pathlib import Path


def get_file_name(file_element):
    """
    Try to get the source file path from a Clover <file> element.
    """
    return (
        file_element.get("name")
        or file_element.get("path")
        or file_element.get("filename")
        or "Unknown file"
    )


def parse_file(file_element):
    methods = []

    current_method = None

    total_branches = 0
    missed_branches = 0

    for line in file_element.findall(".//line"):
        line_type = line.get("type")
        line_number = int(line.get("num"))

        if line_type == "method":
            current_method = {
                "line": line_number,
                "signature": line.get("signature", "unknown"),
                "count": int(line.get("count", "0")),
                "branches": [],
            }

            methods.append(current_method)

        elif line_type == "cond":
            true_count = int(line.get("truecount", "0"))
            false_count = int(line.get("falsecount", "0"))

            # Every condition has two branches:
            # true and false.
            total_branches += 2

            missing = []

            if true_count == 0:
                missing.append("true")
                missed_branches += 1

            if false_count == 0:
                missing.append("false")
                missed_branches += 1

            # Only associate the condition with a method if we
            # currently have one.
            if current_method is not None and missing:
                current_method["branches"].append({
                    "line": line_number,
                    "missing": missing,
                })

    return {
        "methods": methods,
        "total_branches": total_branches,
        "missed_branches": missed_branches,
    }


def generate_markdown(file_name, report):
    methods = report["methods"]
    total_branches = report["total_branches"]
    missed_branches = report["missed_branches"]

    covered_branches = total_branches - missed_branches

    not_entered = [
        method
        for method in methods
        if method["count"] == 0
    ]

    partially_covered = [
        method
        for method in methods
        if method["count"] > 0 and method["branches"]
    ]

    lines = []

    lines.append("# Coverage Gaps")
    lines.append("")
    lines.append(f"**File:** `{file_name}`")
    lines.append("")
    lines.append(
        f"**Branches:** {covered_branches} / {total_branches} covered "
        f"— {missed_branches} missed"
    )
    lines.append("")

    # ------------------------------------------------------------------
    # Methods Not Entered
    # ------------------------------------------------------------------

    if not_entered:
        lines.append("## Methods Not Entered")
        lines.append("")
        lines.append("| Line | Method |")
        lines.append("|---:|---|")

        for method in not_entered:
            lines.append(
                f"| {method['line']} | `{method['signature']}` |"
            )

        lines.append("")

    # ------------------------------------------------------------------
    # Partially Covered Methods
    # ------------------------------------------------------------------

    if partially_covered:
        lines.append("## Partially Covered Methods")
        lines.append("")

        for method in partially_covered:
            lines.append(
                f"### Line {method['line']} — "
                f"`{method['signature']}`"
            )
            lines.append("")
            lines.append("| Line | Missing coverage |")
            lines.append("|---:|---|")

            for branch in method["branches"]:
                for missing_branch in branch["missing"]:
                    lines.append(
                        f"| {branch['line']} | "
                        f"`{missing_branch}` branch |"
                    )

            lines.append("")

    # ------------------------------------------------------------------
    # No gaps
    # ------------------------------------------------------------------

    if not not_entered and not partially_covered:
        lines.append("No coverage gaps found.")
        lines.append("")

    return "\n".join(lines)


def find_files(root):
    """
    Find Clover <file> elements.
    """
    return root.findall(".//file")


def main():
    parser = argparse.ArgumentParser(
        description=(
            "Generate a Markdown coverage-gap report "
            "from OpenClover XML."
        )
    )

    parser.add_argument(
        "xml",
        help="Path to Clover XML file"
    )

    parser.add_argument(
        "-o",
        "--output",
        default="coverage-gaps.md",
        help="Output Markdown file (default: coverage-gaps.md)"
    )

    parser.add_argument(
        "-f",
        "--file",
        help="Only report coverage for this source file"
    )

    args = parser.parse_args()

    xml_path = Path(args.xml)

    if not xml_path.exists():
        raise SystemExit(
            f"XML file does not exist: {xml_path}"
        )

    tree = ET.parse(xml_path)
    root = tree.getroot()

    files = find_files(root)

    if not files:
        raise SystemExit(
            "No <file> elements were found in the Clover XML."
        )

    selected_files = []

    for file_element in files:
        file_name = get_file_name(file_element)

        if args.file:
            requested = Path(args.file)

            if (
                file_name != args.file
                and Path(file_name) != requested
                and Path(file_name).name != requested.name
            ):
                continue

        selected_files.append(
            (file_name, file_element)
        )

    if not selected_files:
        raise SystemExit(
            f"Could not find requested file: {args.file}"
        )

    reports = []

    for file_name, file_element in selected_files:
        report = parse_file(file_element)

        markdown = generate_markdown(
            file_name,
            report
        )

        reports.append(markdown)

    output = "\n".join(reports)

    output_path = Path(args.output)
    output_path.write_text(
        output,
        encoding="utf-8"
    )

    print(f"Generated: {output_path}")


if __name__ == "__main__":
    main()