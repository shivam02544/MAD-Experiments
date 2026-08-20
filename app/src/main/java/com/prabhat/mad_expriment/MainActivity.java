package com.prabhat.mad_expriment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etPassword;

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private MaterialButton btnLogin;
    private MaterialButton btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Connect XML components
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);

        // Login button
        btnLogin.setOnClickListener(v -> {

            String email = getText(etEmail);
            String password = getText(etPassword);

            emailLayout.setError(null);
            passwordLayout.setError(null);

            if (email.isEmpty()) {

                emailLayout.setError(
                        "Please enter your email"
                );

                return;
            }

            if (password.isEmpty()) {

                passwordLayout.setError(
                        "Please enter your password"
                );

                return;
            }

            Intent intent = new Intent(
                    MainActivity.this,
                    DashboardActivity.class
            );

            startActivity(intent);
        });

        // Signup button
        btnSignup.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    SignupActivity.class
            );

            startActivity(intent);
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