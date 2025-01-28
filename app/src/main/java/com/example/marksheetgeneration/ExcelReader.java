package com.example.marksheetgeneration;

import android.content.Context;
import android.util.Log;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public static class StudentData {
        public String name;
        public String enrollmentNumber;
        public String classGrade;
        public String department;
        public String contactEmail;
        public String academicYear;
        public String semester;
        public String totalMarksObtained;
        public String maximumMarks;
        public String percentage;
        public String overallGrade;

        public StudentData(String name, String enrollmentNumber, String classGrade,
                           String department, String contactEmail, String academicYear,
                           String semester, String totalMarksObtained, String maximumMarks,
                           String percentage, String overallGrade) {
            this.name = name;
            this.enrollmentNumber = enrollmentNumber;
            this.classGrade = classGrade;
            this.department = department;
            this.contactEmail = contactEmail;
            this.academicYear = academicYear;
            this.semester = semester;
            this.totalMarksObtained = totalMarksObtained;
            this.maximumMarks = maximumMarks;
            this.percentage = percentage;
            this.overallGrade = overallGrade;
        }
    }

    public static StudentData getStudentInfo(Context context, String enrollmentNumber) {
        try {
            // Open the Excel file from assets
            InputStream inputStream = context.getAssets().open("student_marksheet_data.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Get the first sheet (Student Info)
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate through rows to find matching enrollment number
            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0) continue;

                // Get enrollment number from the second column
                String cellEnrollment = getCellValueAsString(row.getCell(1));

                if (cellEnrollment.equals(enrollmentNumber)) {
                    // Create and return StudentData object
                    return new StudentData(
                            getCellValueAsString(row.getCell(0)),   // Name
                            cellEnrollment,                         // Enrollment Number
                            getCellValueAsString(row.getCell(2)),   // Class/Grade
                            getCellValueAsString(row.getCell(3)),   // Department
                            getCellValueAsString(row.getCell(4)),   // Contact Email
                            getCellValueAsString(row.getCell(5)),   // Academic Year
                            getCellValueAsString(row.getCell(6)),   // Semester
                            getCellValueAsString(row.getCell(7)),   // Total Marks Obtained
                            getCellValueAsString(row.getCell(8)),   // Maximum Marks
                            getCellValueAsString(row.getCell(9)),   // Percentage
                            getCellValueAsString(row.getCell(10))   // Overall Grade
                    );
                }
            }

            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("ExcelReader", "Error reading student info", e);
        }

        return null;
    }

    public static List<ResultItem> getStudentResults(Context context, String enrollmentNumber) {
        List<ResultItem> results = new ArrayList<>();

        try {
            // Open the Excel file from assets
            InputStream inputStream = context.getAssets().open("student_marksheet_data.xlsx");
            Workbook workbook = new XSSFWorkbook(inputStream);

            // Get the second sheet (Subject Results)
            Sheet sheet = workbook.getSheetAt(1);

            // Iterate through rows
            for (Row row : sheet) {
                // Skip header row
                if (row.getRowNum() == 0) continue;

                // Get enrollment number from the first column
                String cellEnrollment = getCellValueAsString(row.getCell(0));

                if (cellEnrollment.equals(enrollmentNumber)) {
                    // Create ResultItem and add to list
                    ResultItem result = new ResultItem(
                            getCellValueAsString(row.getCell(2)),  // Subject Code
                            getCellValueAsString(row.getCell(3)),  // Subject Name
                            getCellValueAsString(row.getCell(4)),  // Max Marks
                            getCellValueAsString(row.getCell(5)),  // Obtained Marks
                            getCellValueAsString(row.getCell(6)),  // Pass Marks
                            getCellValueAsString(row.getCell(7)),  // Remarks
                            getCellValueAsString(row.getCell(8))   // Grade
                    );
                    results.add(result);
                }
            }

            workbook.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e("ExcelReader", "Error reading student results", e);
        }

        return results;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Check if it's a whole number
                if (cell.getNumericCellValue() % 1 == 0) {
                    return String.valueOf((int) cell.getNumericCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                // Evaluate formula
                FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper()
                        .createFormulaEvaluator();
                return getCellValueAsString(evaluator.evaluateInCell(cell));
            default:
                return "";
        }
    }
}