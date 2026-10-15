package com.prabhat.mad_expriment;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class DatabaseViewActivity extends AppCompatActivity {

    private ListView lvDatabaseLogs;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_view);

        lvDatabaseLogs = findViewById(R.id.lvDatabaseLogs);
        databaseHelper = new DatabaseHelper(this);

        loadLogs();
    }

    private void loadLogs() {
        List<String> logs = databaseHelper.getAllLogs();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_item_log, logs);
        lvDatabaseLogs.setAdapter(adapter);
    }
}