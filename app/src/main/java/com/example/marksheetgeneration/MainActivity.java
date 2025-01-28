package com.example.marksheetgeneration;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button generateButton;

    private TextView tvStudentName, tvEnrollmentNumber, tvClassGrade,
            tvDepartment, tvContactEmail, tvAcademicYear, tvSemester;

    private TextView tvTotalMarksObtained, tvMaximumMarks,
            tvPercentage, tvOverallGrade,tv_timenow;

    // Table for Subject Results
    private TableLayout tableLayoutResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }




        initializeViews();
        String receivedData = getIntent().getStringExtra("ENROLLMENT_NUMBER");

        // Load and display student information
        loadStudentInformation(receivedData);







        generateButton = findViewById(R.id.button);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permissions before proceeding
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    // Hide the button before capturing the layout to avoid including it in the PDF
                    generateButton.setVisibility(View.GONE);

                    // Capture the entire layout and generate the PDF
                    convertXmlToPdf();

                    // Restore the button visibility after PDF generation
                    generateButton.setVisibility(View.VISIBLE);
                }
            }
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void initializeViews() {
        tvStudentName = findViewById(R.id.tvStudentName);
        tvEnrollmentNumber = findViewById(R.id.tvEnrollmentNumber);
        tvClassGrade = findViewById(R.id.tvClassGrade);
        tvDepartment = findViewById(R.id.tvDepartment);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        tvAcademicYear = findViewById(R.id.tvAcademicYear);
        tvSemester = findViewById(R.id.tvSemester);
        tv_timenow=findViewById(R.id.timenow);

        // Summary Views
        tvTotalMarksObtained = findViewById(R.id.tvmarksobtained);
        tvMaximumMarks = findViewById(R.id.tvMaximumMarks);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvOverallGrade = findViewById(R.id.tvoverallGrade);

        // Results Table
        tableLayoutResults = findViewById(R.id.tablelayoutresults);
    }

    private void loadStudentInformation(String enrollmentNumber) {
        // Get student info from Excel
        ExcelReader.StudentData studentData = ExcelReader.getStudentInfo(this, enrollmentNumber);
        if (studentData != null) {
            // Set student basic information
            tvStudentName.setText(studentData.name);
            tvEnrollmentNumber.setText(studentData.enrollmentNumber);
            tvClassGrade.setText(studentData.classGrade);
            tvDepartment.setText(studentData.department);
            tvContactEmail.setText(studentData.contactEmail);
            tvAcademicYear.setText(studentData.academicYear);
            tvSemester.setText(studentData.semester);
            tvTotalMarksObtained.setText(studentData.totalMarksObtained);
            tvMaximumMarks.setText(studentData.maximumMarks);
            tvPercentage.setText(studentData.percentage);
            tvOverallGrade.setText(studentData.overallGrade);

        }
        String currentTimestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        tv_timenow.setText(currentTimestamp);

        // Get and display student results
        List<ResultItem> results = ExcelReader.getStudentResults(this, enrollmentNumber);
        displayStudentResults(results);
    }

    private void displayStudentResults(List<ResultItem> results) {
        // Clear existing rows (except header)
        while (tableLayoutResults.getChildCount() > 1) {
            tableLayoutResults.removeViewAt(1);
        }

        // Populate results table
        for (ResultItem result : results) {
            TableRow row = new TableRow(this);

            // Create TextViews for each column
            TextView tvSubjectCode = new TextView(this);
            tvSubjectCode.setText(result.getSubjectCode());
            TextView tvSubjectName = new TextView(this);
            tvSubjectName.setText(result.getSubjectName());
            TextView tvMaxMarks = new TextView(this);
            tvMaxMarks.setText(result.getMaxMarks());
            TextView tvObtainedMarks = new TextView(this);
            tvObtainedMarks.setText(result.getObtainedMarks());
            TextView tvPassMarks = new TextView(this);
            tvPassMarks.setText(result.getPassMarks());
            TextView tvRemarks = new TextView(this);
            tvRemarks.setText(result.getRemarks());
            TextView tvGrade = new TextView(this);
            tvGrade.setText(result.getGrade());

            // Add TextViews to the row
            row.addView(tvSubjectCode);
            row.addView(tvSubjectName);
            row.addView(tvMaxMarks);
            row.addView(tvObtainedMarks);
            row.addView(tvPassMarks);
            row.addView(tvRemarks);
            row.addView(tvGrade);

            // Add row to the table
            tableLayoutResults.addView(row);
        }
    }


    private void convertXmlToPdf() {
        // Get the root layout of the activity (replace with the actual root layout of your XML)
        View rootView = findViewById(android.R.id.content).getRootView();

        // Create a new PdfDocument
        PdfDocument pdfDocument = new PdfDocument();

        // Create a page description for the PDF
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(rootView.getWidth(), rootView.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Create a Bitmap from the entire layout
        Bitmap bitmap = createBitmapFromView(rootView);

        // Get the Canvas object for the page and draw the bitmap onto it
        Canvas canvas = page.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);

        // Save the PDF to file
        pdfDocument.finishPage(page);

        File downloadsFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(downloadsFolder, "Student_Marksheet_from_XML.pdf");

        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "PDF generated successfully!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error generating PDF!", Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

    private Bitmap createBitmapFromView(View view) {
        // Create a Bitmap with the same size as the view
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas); // Draw the layout onto the canvas
        return bitmap;
    }




}
