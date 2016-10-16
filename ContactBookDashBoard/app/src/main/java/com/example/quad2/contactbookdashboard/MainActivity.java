package com.example.quad2.contactbookdashboard;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.github.tamir7.contacts.Contacts;
import com.github.tamir7.contacts.Email;
import com.github.tamir7.contacts.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Contact> contacts = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Contacts.initialize(MainActivity.this);

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
            showProgressDialog();
            LoadAsynchronusly loadAsynchronusly = new LoadAsynchronusly();
            loadAsynchronusly.execute();
        }
    }

    public void xyz() {
        contacts.clear();
        Query query = Contacts.getQuery();
        query.hasPhoneNumber();
        query.include(com.github.tamir7.contacts.Contact.Field.EventType, com.github.tamir7.contacts.Contact.Field.Email,
                com.github.tamir7.contacts.Contact.Field.EventStartDate, com.github.tamir7.contacts.Contact.Field.PhotoUri,
                com.github.tamir7.contacts.Contact.Field.PhoneLabel, com.github.tamir7.contacts.Contact.Field.PhoneNormalizedNumber,
                com.github.tamir7.contacts.Contact.Field.PhoneType, com.github.tamir7.contacts.Contact.Field.DisplayName,
                com.github.tamir7.contacts.Contact.Field.PhoneNumber);
        List<com.github.tamir7.contacts.Contact> contactsData = query.find();
        for (com.github.tamir7.contacts.Contact contactData : contactsData) {
            if (contactData != null) {
                Contact contact = new Contact();
                if (contactData.getEmails().size() > 0 && contactData.getPhoneNumbers().size() > 0) {
                    //Log.d("Test1", contactData.getDisplayName() + "***" + contactData.getEmails().get(0).getAddress());
                    for (Email email : contactData.getEmails()) {
                        Log.d("email", email.getAddress());
                        contact.setEmail(email.getAddress());
                    }
                    contact.setName(contactData.getDisplayName());

                    if (contactData.getBirthday() != null) {
                        String birthDate = convertDateFormat(contactData.getBirthday().getStartDate());
                        //Log.d("bday", contactData.getBirthday().getStartDate());
                        contact.setDateOfBirth(birthDate);
                    } else
                        contact.setDateOfBirth("Not Available");

                    contact.setPhoneNumber(contactData.getPhoneNumbers().get(0).getNumber());


                    if (contactData.getPhotoUri() != null) {
                        contact.setImageURI(contactData.getPhotoUri());
                    }

                    contacts.add(contact);
                }
            }
        }

    }

    private String convertDateFormat(String date) {
        SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat myFormat = new SimpleDateFormat("dd--MM--yyyy");
        String newFormat = "";

        try {

            newFormat = myFormat.format(oldFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newFormat;
    }

    class LoadAsynchronusly extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... params) {
            xyz();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setView();
        }
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
        progressDialog.dismiss();
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
