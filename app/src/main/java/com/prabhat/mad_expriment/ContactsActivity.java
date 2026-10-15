package com.prabhat.mad_expriment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private TextView tvContactStatus;

    private List<ContactModel> contactList;
    private ContactsAdapter contactsAdapter;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    fetchContactsFromProvider();
                } else {
                    Toast.makeText(this, getString(R.string.permission_required), Toast.LENGTH_SHORT).show();
                    tvContactStatus.setText(R.string.permission_required);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        MaterialButton btnLoadContacts = findViewById(R.id.btnLoadContacts);
        MaterialButton btnAddContact = findViewById(R.id.btnAddContact);
        tvContactStatus = findViewById(R.id.tvContactStatus);
        ListView lvContacts = findViewById(R.id.lvContacts);

        contactList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(this, R.layout.list_item_contact, contactList);
        lvContacts.setAdapter(contactsAdapter);

        btnLoadContacts.setOnClickListener(v -> checkAndLoadContacts());

        btnAddContact.setOnClickListener(v -> addContactViaProvider());

        // Check and load on launch if permission already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            fetchContactsFromProvider();
        }
    }

    private void checkAndLoadContacts() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            fetchContactsFromProvider();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private void fetchContactsFromProvider() {
        contactList.clear();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        try (Cursor cursor = contentResolver.query(
                uri,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                do {
                    String id = idIndex != -1 ? cursor.getString(idIndex) : "";
                    String name = nameIndex != -1 ? cursor.getString(nameIndex) : "Unknown";
                    String number = numberIndex != -1 ? cursor.getString(numberIndex) : "";

                    if (!number.isEmpty()) {
                        contactList.add(new ContactModel(id, name, number));
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error fetching contacts: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (contactList.isEmpty()) {
            tvContactStatus.setText(R.string.no_contacts_found);
        } else {
            tvContactStatus.setText(getString(R.string.total_contacts_format, contactList.size()));
        }

        contactsAdapter.notifyDataSetChanged();
    }

    private void addContactViaProvider() {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, "Demo Contact");
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, "+1 555-123-4567");

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to launch Contacts application.", Toast.LENGTH_SHORT).show();
        }
    }
}