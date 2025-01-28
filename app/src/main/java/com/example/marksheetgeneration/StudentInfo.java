package com.example.marksheetgeneration;

public class StudentInfo {
    private String studentName;
    private String enrollmentNumber;
    private String classGrade;
    private String department;
    private String contactEmail;
    private String academicYear;
    private String semester;
    private String totalMarksObtained;
    private String maximumMarks;
    private String percentage;
    private String overallGrade;

    // Constructor
    public StudentInfo(String studentName, String enrollmentNumber,
                       String classGrade, String department,
                       String contactEmail, String academicYear,
                       String semester, String totalMarksObtained,
                       String maximumMarks, String percentage,
                       String overallGrade) {
        this.studentName = studentName;
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

    // Getters
    public String getStudentName() { return studentName; }
    public String getEnrollmentNumber() { return enrollmentNumber; }
    public String getClassGrade() { return classGrade; }
    public String getDepartment() { return department; }
    public String getContactEmail() { return contactEmail; }
    public String getAcademicYear() { return academicYear; }
    public String getSemester() { return semester; }
    public String getTotalMarksObtained() { return totalMarksObtained; }
    public String getMaximumMarks() { return maximumMarks; }
    public String getPercentage() { return percentage; }
    public String getOverallGrade() { return overallGrade; }
}