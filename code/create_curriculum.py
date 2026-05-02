import pandas as pd


def build_curriculum_df():
    program = "BS in Computer Science"
    semester = "Semester-1"

    rows = [
        {"ProgramName": program, "Semester": semester, "CourseCode": "CMPC-5201", "CourseTitle": "Programming Fundamentals", "CreditHours": "4(3-3)", "PreReq": ""},
        {"ProgramName": program, "Semester": semester, "CourseCode": "URCA-5123", "CourseTitle": "Application of Information & Communication Technologies", "CreditHours": "3(2-3)", "PreReq": ""},
        {"ProgramName": program, "Semester": semester, "CourseCode": "URCQ-5101", "CourseTitle": "Discrete Structures", "CreditHours": "3(3-0)", "PreReq": ""},
        {"ProgramName": program, "Semester": semester, "CourseCode": "URCQ-5102", "CourseTitle": "Calculus and Analytic Geometry", "CreditHours": "3(3-0)", "PreReq": ""},
        {"ProgramName": program, "Semester": semester, "CourseCode": "URCE-5118", "CourseTitle": "Functional English", "CreditHours": "3(3-0)", "PreReq": ""},
        {"ProgramName": program, "Semester": semester, "CourseCode": "BUSB-6101", "CourseTitle": "Introduction to Marketing", "CreditHours": "3(3-0)", "PreReq": ""},
    ]

    df = pd.DataFrame(rows, columns=["ProgramName", "Semester", "CourseCode", "CourseTitle", "CreditHours", "PreReq"])
    return df


def main():
    df = build_curriculum_df()

    # Save CSV and Excel in the current folder
    csv_path = "Curriculum.csv"
    xlsx_path = "Curriculum.xlsx"

    df.to_csv(csv_path, index=False)
    df.to_excel(xlsx_path, index=False)

    print(f"Saved: {csv_path} and {xlsx_path}")
    print(df.to_string(index=False))


if __name__ == "__main__":
    main()
