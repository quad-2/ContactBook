package com.example.quad2.contactbookdashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Contact> contacts = new ArrayList<>();
    private RecyclerView recyclerView;
    private ContentResolver contentResolver;
    private Cursor phones;
    private ProgressDialog progressDialog;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showContacts();
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait! Loading Contacts.");
        progressDialog.show();
    }

    /**
     * checking permissions on Android Marshmallow and above
     */
    private void showContacts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        } else {
            readContacts();
        }
    }

    public void readContacts() {
        showProgressDialog();
        contacts.clear();
        contentResolver = this.getContentResolver();
        phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (phones != null) {
            if (phones.getCount() == 0) {
                Toast.makeText(MainActivity.this, "No contacts in your contact list.", Toast.LENGTH_LONG).show();
            }
            while (phones.moveToNext()) {
                String emailAddr = "";
                String id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String lastContacted = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED));
                String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI));

                Cursor emails = contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + id, null, null);
                while (emails.moveToNext()) {
                    emailAddr = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    break;
                }
                emails.close();

                Contact con = new Contact();
                con.setContactId(id);
                con.setName(name);
                con.setPhoneNumber(phoneNumber);
                con.setEmail(emailAddr);
                con.setImageURI(image_thumb);
                con.setLastContacted(lastContacted);

                if (!con.getEmail().isEmpty())
                    contacts.add(con);
            }
        }
        setView();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setView() {
        recyclerView = (RecyclerView) findViewById(R.id.contactList_rv);
        recyclerView.setHasFixedSize(true);
        ContactListAdapter contactListAdapter = new ContactListAdapter(MainActivity.this, contacts);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(contactListAdapter);
        contactListAdapter.notifyDataSetChanged();
        progressDialog.dismiss();
    }
}
