package com.example.marksheetgeneration;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Idcard extends AppCompatActivity {

    private Button generateButton;

    private TextView tvStudentName, tvEnrollmentNumber, tvDepartment, tvContactEmail, tvAcademicYear, tvSemester, tv_timenow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.identity_card);

        initializeViews();
        String receivedData = getIntent().getStringExtra("ENROLLMENT_NUMBER");

        // Load and display student information
        loadStudentInformation(receivedData);

        generateButton = findViewById(R.id.buttondwn);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permissions before proceeding
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Idcard.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
        tvDepartment = findViewById(R.id.tvDepartment);
        tvContactEmail = findViewById(R.id.tvContactEmail);
        tvAcademicYear = findViewById(R.id.tvAcademicYear);
        tvSemester = findViewById(R.id.tvSemester);
        tv_timenow = findViewById(R.id.timenow);
    }

    private void loadStudentInformation(String enrollmentNumber) {
        // Get student info from Excel
        ExcelReader.StudentData studentData = ExcelReader.getStudentInfo(this, enrollmentNumber);
        if (studentData != null) {
            // Set student basic information
            tvStudentName.setText(studentData.name);
            tvEnrollmentNumber.setText(studentData.enrollmentNumber);
            tvDepartment.setText(studentData.department);
            tvContactEmail.setText(studentData.contactEmail);
            tvAcademicYear.setText(studentData.academicYear);
            tvSemester.setText(studentData.semester);
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
        File file = new File(downloadsFolder, "Student_ID_Card.pdf");

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