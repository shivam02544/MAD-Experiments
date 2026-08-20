package com.prabhat.mad_expriment;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etEmail;
    private TextInputEditText etPassword;
    private TextInputEditText etConfirmPassword;

    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;

    private MaterialButton btnCreateAccount;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        // Connect XML views
        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);

        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmPasswordLayout =
                findViewById(R.id.confirmPasswordLayout);

        btnCreateAccount =
                findViewById(R.id.btnCreateAccount);

        tvBackToLogin =
                findViewById(R.id.tvBackToLogin);

        // Create account
        btnCreateAccount.setOnClickListener(v -> {

            String name = getText(etName);
            String email = getText(etEmail);
            String password = getText(etPassword);
            String confirmPassword =
                    getText(etConfirmPassword);

            // Clear previous errors
            nameLayout.setError(null);
            emailLayout.setError(null);
            passwordLayout.setError(null);
            confirmPasswordLayout.setError(null);

            // Validate name
            if (name.isEmpty()) {

                nameLayout.setError(
                        "Please enter your name"
                );

                return;
            }

            // Validate email
            if (email.isEmpty()) {

                emailLayout.setError(
                        "Please enter your email"
                );

                return;
            }

            // Validate password
            if (password.isEmpty()) {

                passwordLayout.setError(
                        "Please enter a password"
                );

                return;
            }

            // Validate confirm password
            if (confirmPassword.isEmpty()) {

                confirmPasswordLayout.setError(
                        "Please confirm your password"
                );

                return;
            }

            // Check password match
            if (!password.equals(confirmPassword)) {

                confirmPasswordLayout.setError(
                        "Passwords do not match"
                );

                return;
            }

            Toast.makeText(
                    SignupActivity.this,
                    "Account creation will be implemented later",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Back to login
        tvBackToLogin.setOnClickListener(v -> {

            finish();
        });
    }

    private String getText(TextInputEditText editText) {

        if (editText.getText() == null) {
            return "";
        }

        return editText.getText()
                .toString()
                .trim();
    }
}