package com.star.contacts;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    public static final String DB_NAME = "MyContacts";
    public static final String DB_FILE_NAME = DB_NAME + ".db";
    public static final String TABLE_NAME = "contacts";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_EMAIL = "email";

    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_NAME + " VARCHAR, "
            + COLUMN_EMAIL + " VARCHAR"
            + ");";

    private SQLiteDatabase mContactsSQLiteDatabase;

    private Button mCreateDatabaseButton;
    private Button mAddContactButton;
    private Button mDeleteContactButton;
    private Button mGetContactsButton;
    private Button mDeleteDatabaseButton;

    private EditText mNameEditText;
    private EditText mEmailEditText;
    private EditText mIdToDeleteEditText;
    private EditText mContactsListEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCreateDatabaseButton = (Button) findViewById(R.id.create_database_button);
        mCreateDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContactsSQLiteDatabase = MainActivity.this
                        .openOrCreateDatabase(DB_NAME, MODE_PRIVATE, null);

                mContactsSQLiteDatabase.execSQL(CREATE_TABLE);

                File database = getApplicationContext().getDatabasePath(DB_FILE_NAME);

                if (!database.exists()) {
                    Toast.makeText(MainActivity.this, "Database Created",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, "Database Missing",
                            Toast.LENGTH_LONG).show();
                }

                mAddContactButton.setClickable(true);
                mDeleteContactButton.setClickable(true);
                mGetContactsButton.setClickable(true);
                mDeleteDatabaseButton.setClickable(true);
            }
        });

        mAddContactButton = (Button) findViewById(R.id.add_contact_button);
        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contactName = mNameEditText.getText().toString();
                String contactEmail = mEmailEditText.getText().toString();

                ContentValues contentValues = new ContentValues();
                contentValues.put(COLUMN_NAME, contactName);
                contentValues.put(COLUMN_EMAIL, contactEmail);

                mContactsSQLiteDatabase.insert(TABLE_NAME, null, contentValues);
            }
        });
        mAddContactButton.setClickable(false);

        mDeleteContactButton = (Button) findViewById(R.id.delete_contact_button);
        mDeleteContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mIdToDeleteEditText.getText().toString();

                mContactsSQLiteDatabase.delete(TABLE_NAME, COLUMN_ID + " = ? ", new String[]{id});
            }
        });
        mDeleteContactButton.setClickable(false);

        mGetContactsButton = (Button) findViewById(R.id.get_contacts_button);
        mGetContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String contactsList = "";

                Cursor cursor = mContactsSQLiteDatabase
                        .query(TABLE_NAME, null, null, null, null, null, null);

                if (cursor != null && cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                        long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                        String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                        String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));

                        contactsList += id + " : " + name + " : " + email + "\n";
                    }

                } else {

                    Toast.makeText(MainActivity.this, "No Results to Show",
                            Toast.LENGTH_LONG).show();
                }

                if (cursor != null) {
                    cursor.close();
                }

                mContactsListEditText.setText(contactsList);

            }
        });
        mGetContactsButton.setClickable(false);

        mDeleteDatabaseButton = (Button) findViewById(R.id.delete_database_button);
        mDeleteDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.deleteDatabase(DB_NAME);

                mAddContactButton.setClickable(false);
                mDeleteContactButton.setClickable(false);
                mGetContactsButton.setClickable(false);
                mDeleteDatabaseButton.setClickable(false);
            }
        });
        mDeleteDatabaseButton.setClickable(false);

        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
        mIdToDeleteEditText = (EditText) findViewById(R.id.id_to_delete_edit_text);
        mContactsListEditText = (EditText) findViewById(R.id.contacts_list_edit_text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mContactsSQLiteDatabase != null) {
            mContactsSQLiteDatabase.close();
        }
    }
}
