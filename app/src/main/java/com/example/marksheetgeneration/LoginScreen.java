package com.example.marksheetgeneration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.marksheetgeneration.ExcelReader;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Random;

public class LoginScreen extends AppCompatActivity {

    private TextInputEditText etRollNumber, etCaptchaInput;
    private TextInputLayout rollNumberInputLayout, captchaInputLayout;
    private TextView tvCaptcha;
    private Button btnViewResult, btnidcard;
    private ImageButton btnRefreshCaptcha;

    private String currentCaptcha;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        // Initialize views
        etRollNumber = findViewById(R.id.etRollNumber);
        etCaptchaInput = findViewById(R.id.etCaptchaInput);
        rollNumberInputLayout = findViewById(R.id.rollNumberInputLayout);
        captchaInputLayout = findViewById(R.id.captchaInputLayout);
        tvCaptcha = findViewById(R.id.tvCaptcha);
        btnViewResult = findViewById(R.id.btnViewResult);
        btnRefreshCaptcha = findViewById(R.id.btnRefreshCaptcha);
        btnidcard = findViewById(R.id.idcard);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);

        // Generate initial captcha
        generateCaptcha();

        // Refresh captcha on button click
        btnRefreshCaptcha.setOnClickListener(v -> generateCaptcha());

        // Handle View Result button click
        btnViewResult.setOnClickListener(v -> validateInputs());
        btnidcard.setOnClickListener(view -> validateInputs2());
    }

    private void validateInputs2() {
        String rollNumber = etRollNumber.getText().toString().trim();
        String captchaInput = etCaptchaInput.getText().toString().trim();

        // Reset errors
        rollNumberInputLayout.setError(null);
        captchaInputLayout.setError(null);

        // Validate roll number
        if (TextUtils.isEmpty(rollNumber)) {
            rollNumberInputLayout.setError("Roll number is required.");
            return;
        }
        if (TextUtils.isEmpty(captchaInput)) {
            captchaInputLayout.setError("Captcha is required.");
            return;
        }

        if (!captchaInput.equals(currentCaptcha)) {
            captchaInputLayout.setError("Captcha is incorrect.");
            return;
        }

        // Show loading dialog
        progressDialog.show();

        // Simulate loading delay
        new Handler().postDelayed(() -> {
            // Check if enrollment number exists in Excel
            if (isEnrollmentNumberValid(rollNumber)) {
                // If validation passes
                Toast.makeText(this, "Validation successful! Viewing ID card...", Toast.LENGTH_SHORT).show();
                String data = etRollNumber.getText().toString();
                Intent intent = new Intent(this, Idcard.class);
                intent.putExtra("ENROLLMENT_NUMBER", data);

                startActivity(intent);
            } else {
                // Show error dialog
                showErrorDialog("Enter valid details or data not found.");
            }
            progressDialog.dismiss();
        }, 2000); // 2 seconds delay
    }

    private void generateCaptcha() {
        // Generate a simple math captcha
        Random random = new Random();
        int num1 = random.nextInt(10) + 1;
        int num2 = random.nextInt(10) + 1;
        currentCaptcha = String.valueOf(num1 + num2);
        tvCaptcha.setText(num1 + " + " + num2 + " = ?");
    }

    private void validateInputs() {
        String rollNumber = etRollNumber.getText().toString().trim();
        String captchaInput = etCaptchaInput.getText().toString().trim();

        // Reset errors
        rollNumberInputLayout.setError(null);
        captchaInputLayout.setError(null);

        // Validate roll number
        if (TextUtils.isEmpty(rollNumber)) {
            rollNumberInputLayout.setError("Roll number is required.");
            return;
        }

        // Validate captcha
        if (TextUtils.isEmpty(captchaInput)) {
            captchaInputLayout.setError("Captcha is required.");
            return;
        }

        if (!captchaInput.equals(currentCaptcha)) {
            captchaInputLayout.setError("Captcha is incorrect.");
            return;
        }

        // Show loading dialog
        progressDialog.show();

        // Simulate loading delay
        new Handler().postDelayed(() -> {
            // Check if enrollment number exists in Excel
            if (isEnrollmentNumberValid(rollNumber)) {
                // If validation passes
                Toast.makeText(this, "Validation successful! Viewing result...", Toast.LENGTH_SHORT).show();
                String data = etRollNumber.getText().toString();
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("ENROLLMENT_NUMBER", data);

                startActivity(intent);
            } else {
                // Show error dialog
                showErrorDialog("Enter valid details or data not found.");
            }
            progressDialog.dismiss();
        }, 2000); // 2 seconds delay
    }

    private boolean isEnrollmentNumberValid(String enrollmentNumber) {
        // Replace this with actual logic to check if the enrollment number exists in the Excel file
        // For example, you can use the ExcelReader class to check if the enrollment number is valid
        ExcelReader.StudentData studentData = ExcelReader.getStudentInfo(this, enrollmentNumber);
        return studentData != null;
    }

    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}
