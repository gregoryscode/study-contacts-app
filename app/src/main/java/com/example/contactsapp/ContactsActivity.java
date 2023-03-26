package com.example.contactsapp;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {

    private Spinner _spinnerContacts;
    private Cursor _cursor;
    private boolean _contactsLoaded = false;
    private boolean _ignoreFirstSelection = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActivityCompat.requestPermissions(ContactsActivity.this, new String[] {Manifest.permission.READ_CONTACTS}, 1 );

        FloatingActionButton fab = findViewById(R.id.fabSearch);
        _spinnerContacts = findViewById(R.id.spinnerContacts);

        _spinnerContacts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Necessário, uma vez que ao carregar os contatos para o spinner pela primeira vez, o snackbar é ativado sem necessidade
                if(!_ignoreFirstSelection) {
                    Snackbar.make(view, "O contato '" + _spinnerContacts.getSelectedItem().toString() + "' foi selecionado.", Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.app_ok), null).show();
                }

                _ignoreFirstSelection = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    if(!_contactsLoaded) {
                        Uri path = ContactsContract.Contacts.CONTENT_URI;
                        ContentResolver contentResolver = ContactsActivity.this.getContentResolver();
                        _cursor = contentResolver.query(path, null,null, null, ContactsContract.Contacts.DISPLAY_NAME);
                        List<String> contactNames = new ArrayList<String>();

                        if(_cursor.getCount() > 0) {

                            while (_cursor.moveToNext()) {

                                String hasNumber = _cursor.getString(_cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));

                                if(hasNumber.equals("1")){
                                    String name = _cursor.getString(_cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                                    contactNames.add(name);
                                }
                            }
                        }

                        ArrayAdapter<String> adapterNames = new ArrayAdapter<String>( ContactsActivity.this, R.layout.support_simple_spinner_dropdown_item, contactNames);
                        _spinnerContacts.setAdapter(adapterNames);
                        _contactsLoaded = true;

                        Snackbar.make(view, R.string.app_contacts_loaded, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.app_ok), null).show();
                    }
                    else {
                        Snackbar.make(view, R.string.app_contacts_done, Snackbar.LENGTH_LONG)
                                .setAction(getString(R.string.app_ok), null).show();
                    }
                }
                catch (Exception ex) {
                    Snackbar.make(view, R.string.app_contacts_error, Snackbar.LENGTH_LONG)
                            .setAction(getString(R.string.app_ok), null).show();
                }
            }
        });
    }
}
