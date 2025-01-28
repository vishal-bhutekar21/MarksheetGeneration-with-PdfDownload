package com.example.marksheetgeneration;
public class ResultItem {
    private String subjectCode;
    private String subjectName;
    private String maxMarks;
    private String obtainedMarks;
    private String passMarks;
    private String remarks;
    private String grade;

    public ResultItem(String subjectCode, String subjectName, String maxMarks,
                      String obtainedMarks, String passMarks, String remarks, String grade) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.maxMarks = maxMarks;
        this.obtainedMarks = obtainedMarks;
        this.passMarks = passMarks;
        this.remarks = remarks;
        this.grade = grade;
    }

    // Getters
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public String getMaxMarks() { return maxMarks; }
    public String getObtainedMarks() { return obtainedMarks; }
    public String getPassMarks() { return passMarks; }
    public String getRemarks() { return remarks; }
    public String getGrade() { return grade; }
}