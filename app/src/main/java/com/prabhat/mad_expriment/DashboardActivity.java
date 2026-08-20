package com.prabhat.mad_expriment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class DashboardActivity extends AppCompatActivity {

    private MaterialCardView cardWebsite;
    private MaterialCardView cardPhone;
    private MaterialCardView cardEmail;
    private MaterialCardView cardMaps;

    private MaterialButton btnLogout;

    private MaterialCardView cardNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dashboard);

        // Connect XML views
        cardNotification = findViewById(R.id.cardNotification);
        cardWebsite = findViewById(R.id.cardWebsite);
        cardPhone = findViewById(R.id.cardPhone);
        cardEmail = findViewById(R.id.cardEmail);
        cardMaps = findViewById(R.id.cardMaps);

        btnLogout = findViewById(R.id.btnLogout);

        cardNotification.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DashboardActivity.this,
                    NotificationActivity.class
            );

            startActivity(intent);
        });


        // Open Website
        cardWebsite.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com")
            );

            startActivity(intent);
        });

        // Open Phone Dialer
        cardPhone.setOnClickListener(v -> {

            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:6204441582")
            );

            startActivity(intent);
        });

        // Open Email
        cardEmail.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_SENDTO);

            intent.setData(
                    Uri.parse("mailto:prabhatkumar12356@gmail.com")
            );

            intent.putExtra(
                    Intent.EXTRA_SUBJECT,
                    "Android Experiment"
            );

            intent.putExtra(
                    Intent.EXTRA_TEXT,
                    "This email was opened using an implicit intent."
            );

            try {

                startActivity(intent);

            } catch (Exception e) {

                Toast.makeText(
                        DashboardActivity.this,
                        "No email application found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Open Maps
        cardMaps.setOnClickListener(v -> {

            Uri location = Uri.parse(
                    "geo:0,0?q=Bangalore"
            );

            Intent intent = new Intent(
                    Intent.ACTION_VIEW,
                    location
            );

            try {

                startActivity(intent);

            } catch (Exception e) {

                Toast.makeText(
                        DashboardActivity.this,
                        "No Maps application found",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        // Logout
        btnLogout.setOnClickListener(v -> {

            finish();
        });
    }
}