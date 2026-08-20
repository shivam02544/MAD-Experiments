package com.prabhat.mad_expriment;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.button.MaterialButton;

public class NotificationActivity extends AppCompatActivity {

    private static final String CHANNEL_ID =
            "android_experiment_notifications";

    private static final int NOTIFICATION_ID = 1001;

    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    private MaterialButton btnShowNotification;
    private MaterialButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        btnShowNotification =
                findViewById(R.id.btnShowNotification);

        btnBack =
                findViewById(R.id.btnBack);

        // Create notification channel
        createNotificationChannel();

        // Ask notification permission
        requestNotificationPermission();

        // Show notification
        btnShowNotification.setOnClickListener(v -> {

            showNotification();
        });

        // Back to dashboard
        btnBack.setOnClickListener(v -> {

            finish();
        });
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channelName = "Experiment Notifications";

            String channelDescription =
                    "Notifications from Android Labs";

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            channelName,
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(channelDescription);

            channel.enableVibration(true);

            channel.setVibrationPattern(
                    new long[]{0, 300, 200, 300}
            );

            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private void requestNotificationPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.POST_NOTIFICATIONS
                        },
                        NOTIFICATION_PERMISSION_CODE
                );
            }
        }
    }

    private void showNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                requestNotificationPermission();
                return;
            }
        }

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        this,
                        CHANNEL_ID
                )
                        .setSmallIcon(
                                android.R.drawable.ic_dialog_info
                        )
                        .setContentTitle("Android Labs")
                        .setContentText(
                                "Experiment 5 notification is working!"
                        )
                        .setPriority(
                                NotificationCompat.PRIORITY_HIGH
                        )
                        .setDefaults(
                                NotificationCompat.DEFAULT_ALL
                        )
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

        notificationManager.notify(
                NOTIFICATION_ID,
                builder.build()
        );
    }
}