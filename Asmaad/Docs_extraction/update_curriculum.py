"""Interactive helper to append curriculum rows to Curriculum.csv / Curriculum.xlsx.

This small tool is safe to run from the project root. It will read existing
Curriculum.csv / Curriculum.xlsx (if present) from the repository root, allow
interactive additions, or append rows passed via JSON string / file.

Usage examples:
  python Asmaad/Docs_extraction/update_curriculum.py --interactive
  python Asmaad/Docs_extraction/update_curriculum.py --append-json '[{"ProgramName": "X", "Semester": "S1", "CourseCode": "ABC-0001", "CourseTitle": "Foo", "CreditHours": "3(3-0)", "PreReq": ""}]'
"""

import argparse
import json
from pathlib import Path
import pandas as pd


ROOT = Path(__file__).resolve().parents[2]
CSV = "W:\\7th Sem\\FYP\\Project\\AI-INBOUND-CALLING-AGENT\\Asmaad\\Docs_extraction\\Curriculum.csv"
XLSX = "W:\\7th Sem\\FYP\\Project\\AI-INBOUND-CALLING-AGENT\\Asmaad\\Docs_extraction\\Curriculum.xlsx"
COLS = ["ProgramName", "Semester", "CourseCode", "CourseTitle", "CreditHours", "PreReq"]


def load():
    if CSV.exists():
        return pd.read_csv(CSV, dtype=str).fillna("")
    if XLSX.exists():
        return pd.read_excel(XLSX, dtype=str).fillna("")
    return pd.DataFrame(columns=COLS)


def save(df):
    df.to_csv(CSV, index=False)
    df.to_excel(XLSX, index=False)
    print(f"Saved: {CSV} and {XLSX}")


def add_rows(rows):
    df = load()
    new = pd.DataFrame(rows)
    for c in COLS:
        if c not in new.columns:
            new[c] = ""
    new = new[COLS]
    combined = pd.concat([df, new], ignore_index=True)
    save(combined)
    return combined


def interactive():
    print("Enter new curriculum rows. Hit ENTER on ProgramName to finish.")
    rows = []
    while True:
        program = input("ProgramName: ").strip()
        if not program:
            break
        semester = input("Semester: ").strip()
        course = input("CourseCode: ").strip()
        title = input("CourseTitle: ").strip()
        credits = input("CreditHours: ").strip()
        prereq = input("PreReq: ").strip()
        rows.append({
            "ProgramName": program,
            "Semester": semester,
            "CourseCode": course,
            "CourseTitle": title,
            "CreditHours": credits,
            "PreReq": prereq,
        })
        print("Row queued. Add another or press ENTER at ProgramName to finish.")
    if rows:
        combined = add_rows(rows)
        print(combined.tail())
    else:
        print("No rows added.")


def main():
    p = argparse.ArgumentParser()
    p.add_argument('--interactive', action='store_true')
    p.add_argument('--append-json', type=str, help='JSON array string or path to JSON file')
    args = p.parse_args()

    if args.interactive:
        interactive()
        return

    if args.append_json:
        val = args.append_json
        if Path(val).exists():
            rows = json.loads(Path(val).read_text(encoding='utf-8'))
        else:
            rows = json.loads(val)
        if isinstance(rows, dict):
            rows = [rows]
        new = add_rows(rows)
        print(new.tail())
        return

    print('No action provided. Use --interactive or --append-json')


if __name__ == '__main__':
    main()
