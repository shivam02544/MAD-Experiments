package com.prabhat.mad_expriment;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

public class BasicViewsActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private RadioGroup rgGender;
    private MaterialCheckBox cbTerms;
    private MaterialSwitch switchNotifications;
    private MaterialButton btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_views);

        etName = findViewById(R.id.et_name);
        rgGender = findViewById(R.id.rg_gender);
        cbTerms = findViewById(R.id.cb_terms);
        switchNotifications = findViewById(R.id.switch_notifications);
        btnSubmit = findViewById(R.id.btn_submit);

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            int selectedGenderId = rgGender.getCheckedRadioButtonId();
            String gender = "Not selected";
            if (selectedGenderId != -1) {
                RadioButton rb = findViewById(selectedGenderId);
                gender = rb.getText().toString();
            }
            boolean termsAccepted = cbTerms.isChecked();
            boolean notificationsEnabled = switchNotifications.isChecked();

            String message = "Name: " + name + "\n" +
                    "Gender: " + gender + "\n" +
                    "Terms Accepted: " + termsAccepted + "\n" +
                    "Notifications: " + notificationsEnabled;

            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}