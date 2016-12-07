package lematthe.calpoly.edu.enough;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.widget.GridLayout.LayoutParams;
import android.app.PendingIntent;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;
    public DatabaseHelper dbHelper; // The database helper(manager)
    public final int PERMISSION_ALL = 1;
    public static String ACTION_SMS_SENT = "ActionSMSSent";
    public static String ACTION_SMS_DELIVERED = "ActionSMSDelivered";
    public final int LOCATION_PERM = 2;
    private MessageDetailFragment fragment;

    // Using two arrays because we need predictable indexing and unchanging sizes
    public String[] newContactNames = new String[3];
    public String[] newContactNumbers = new String[3];

    boolean first_time_saved = false;
    String TAG = "here";
    Button sendButton;
    Button myContact1;
    Button myContact2;
    Button myContact3;
    Button setupbuttoncheck;
    String clickedButton;
    Button selectM;
    Button deleteContact3;
    Button deleteContact2;
    Button deleteContact1;
    Button saveButton;
    Button locationButton;
    TextView selectMessage;
    String checkButtonText;
    TextView emergencyContacts;
    EditText firstName;
    EditText lastName;
    TextView messageEntered;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //implicit layout inflator
        dbHelper = new DatabaseHelper(getApplicationContext());
        setContentView(R.layout.setting_fields);
        // carry on the normal flow, as the case of  permissions  granted.
        //find contact buttons
        if (findViewById(R.id.message_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.d("true", "true fragment");
            mTwoPane = true;
            Bundle arguments = new Bundle();
            arguments.putBoolean(MessageDetailFragment.ARG_ITEM_ID, mTwoPane);
            fragment = new MessageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.message_detail_container, fragment, TAG)
                    .commit();
        }

        else {
            Log.d("false", "in here");
            mTwoPane = false;
        }
        deleteContact1 = (Button) findViewById(R.id.delete_contact_1);
        deleteContact2 = (Button) findViewById(R.id.delete_contact_2);
        deleteContact3 = (Button) findViewById(R.id.delete_contact_3);
        deleteContact1.setEnabled(false);
        deleteContact2.setEnabled(false);
        deleteContact3.setEnabled(false);
        myContact1 = (Button) findViewById(R.id.contact1);
        myContact2 = (Button) findViewById(R.id.contact2);
        myContact3 = (Button) findViewById(R.id.contact3);
        emergencyContacts = (TextView) findViewById(R.id.emergencyContactHeader);
        messageEntered = (TextView) findViewById(R.id.message_entered);
        selectMessage = (TextView) findViewById(R.id.select_message_text);


        //send button here  for now to show functionality
        sendButton = (Button) findViewById(R.id.save);

        saveButton = (Button) findViewById(R.id.save);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);


        ArrayList<String> savedContacts = dbHelper.getContacts();
        if (savedContacts.size() > 0) {
            Log.d("size", String.valueOf(savedContacts.size()));
            for (int i = 0; i < savedContacts.size(); i += 2) {
                String name = savedContacts.get(i);
                String number = savedContacts.get(i + 1);

                if (i == 0) {
                    Button b = (Button) findViewById(R.id.contact1);
                    b.setText(name + "         " + number);
                    deleteContact1.setVisibility(View.VISIBLE);
                    deleteContact1.setEnabled(true);
                }
                if (i == 2) {
                    Button b = (Button) findViewById(R.id.contact2);
                    b.setText(name + "         " + number);
                    deleteContact2.setVisibility(View.VISIBLE);
                    deleteContact2.setEnabled(true);
                }
                if (i == 4) {
                    Button b = (Button) findViewById(R.id.contact3);
                    b.setText(name + "         " + number);
                    deleteContact3.setVisibility(View.VISIBLE);
                    deleteContact3.setEnabled(true);
                }
            }
        }
        else {
            Log.d("here", "small");
            myContact1.setText(R.string.contact1);
            myContact2.setText(R.string.contact2);
            myContact3.setText(R.string.contact3);
        }

        deleteContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact(1);
            }
        });

        deleteContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact(2);
            }
        });

        deleteContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact(3);
            }
        });


        myContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButtonText = ((Button) v).getText().toString();
                contactClicked();
                clickedButton = "myContact1";

                try {
                    newContactNames[0] = "";
                    newContactNumbers[0] = "";
                } catch(Exception e) { }
            }
        });

        myContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButtonText = ((Button) v).getText().toString();
                contactClicked();
                clickedButton = "myContact2";
                try {
                    newContactNames[1] = "";
                    newContactNumbers[1] = "";
                } catch(Exception e) { }

            }
        });

        myContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButtonText = ((Button) v).getText().toString();
                contactClicked();
                clickedButton = "myContact3";
                try {
                    newContactNames[2] = "";
                    newContactNumbers[2] = "";
                } catch(Exception e) { }

            }
        });

        if (!mTwoPane) {
            selectM = (Button) findViewById(R.id.selectmessage);
            selectM.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, MessageDetailActivity.class);
                    intent.putExtra("hi", "hi");
                    context.startActivity(intent);
                }
            });
        }

        if(checkAndRequestPermissions()) {
            saveButton = (Button) findViewById(R.id.save);
            saveButton.setEnabled(true);
        }

        //check if all of the necessary fields are filled
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkRequiredFields()) {
                    Log.d("NOT all fields", "in here");

                }
                else {
                    //send the original text message
                    //go to end activity
                    Log.d("all fields are filled", "in here");
                    if (first_time_saved) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("Widget Is Available");
                        alertDialog.setMessage("A widget has become available for you to use. In the event of an emergency tap the widget 3 times.");
                        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("dialog pressed", "pressed");
                                sendInitialText();
                                finish();
                            }
                        });
                        alertDialog.show();
                    }
                    else {
                        sendInitialText();
                    }

                }

            }
        });

    }


    public void sendInitialText() {
        ArrayList<String> savedContacts = dbHelper.getContacts();
        for (int i = 0; i < savedContacts.size(); i += 2) {
            String name = savedContacts.get(i);
            String number = savedContacts.get(i + 1);
            String initialMessage = "Hi " + name + " you have designated as an emergency contact by "
                    + dbHelper.getFirstName().toString() + " " + dbHelper.getLastName().toString() +
                    "on the application eNOugh. Be aware that in the event of an emergency you will be contacted via text message through this application.";
            //change to real number just using emulator right now
            sendSMS(number, initialMessage);
            Log.d("send text", "text");
        }
    }

    protected void sendSMS(String phoneNumber, String message) {
        Log.d("sms sent to", phoneNumber);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    public void deleteContact(final int id) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setMessage("Are you sure that you want to delete this contact?");
        alertDialog.setTitle("Delete Contact")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d("delete", "delete this contact");
                                dbHelper.deleteContact(id);
                                if (id == 1) {
                                    myContact1.setText(R.string.contact1);
                                    deleteContact1.setEnabled(false);
                                    deleteContact1.setVisibility(View.INVISIBLE);

                                }
                                else if(id == 2) {
                                    myContact2.setText(R.string.contact2);
                                    deleteContact2.setEnabled(false);
                                    deleteContact2.setVisibility(View.INVISIBLE);
                                }
                                else {
                                    myContact3.setText(R.string.contact3);
                                    deleteContact3.setEnabled(false);
                                    deleteContact3.setVisibility(View.INVISIBLE);
                                }

                            }
                        }
                )
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Log.d("dont delete", "here");
                                dialog.dismiss();
                            }
                        }
                );
        alertDialog.show();
    }


    /*
     * This is a method that is called when the save button is pressed
     * checks database to make sure required fields are filled
     *  @return true if all fields are filled
     */
    public boolean checkRequiredFields() {
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        ArrayList <String> checkContacts = dbHelper.getContacts();
        String message = dbHelper.getMessage();
        if (checkContacts.size() > 0 && !message.isEmpty() && !firstName.getText().toString().isEmpty() && !lastName.getText().toString().isEmpty()) {
            dbHelper.deleteUserInfo();
            dbHelper.insertUserInfo(firstName.getText().toString(), lastName.getText().toString());
            return true;
        }
        if (checkContacts.size() == 0) {
            myContact1.setError("Please enter at least 1 contact");
        }
        if (firstName.getText().toString().isEmpty()) {
            firstName.setError("Please enter your first name");
        }
        if (lastName.getText().toString().isEmpty()) {
            lastName.setError("Please enter your last name");
        }
        if (message.isEmpty()) {
            selectMessage.setError("Please enter or select a message");
        }

        return false;
    }

    public void contactClicked(){
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }


    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            String name = "";
            Button myB = null;
            String number = "";
            Uri contactUri = data.getData();
            String[] projection    = new String[] {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                    ContactsContract.CommonDataKinds.Phone.NUMBER};

            Cursor people = getContentResolver().query(contactUri, projection, null, null, null);

            int indexName = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            int indexNumber = people.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);



            if(people.moveToFirst()) {
                do {
                    name   = people.getString(indexName);
                    number = people.getString(indexNumber);

                    if(name.length() > 0 && number.length() > 0){
                        if(clickedButton.equals("myContact1")) {
                            newContactNames[0] = name;
                            newContactNumbers[0] = number;
                            Log.d("contact chosen", "contact chosen");
                            if (checkButtonText != getString(R.string.contact1)) {
                                dbHelper.deleteContact(1);
                            }
                            dbHelper.addNewContact(String.valueOf(1), name, number);
                        }
                        else if(clickedButton.equals("myContact2")) {
                            newContactNames[1] = name;
                            newContactNumbers[1] = number;
                            if (checkButtonText != getString(R.string.contact2)) {
                                Log.d("deleting 2", "two");
                                dbHelper.deleteContact(2);
                            }
                            dbHelper.addNewContact(String.valueOf(2), name, number);
                        }
                        else if(clickedButton.equals("myContact3")) {
                            newContactNames[2] = name;
                            newContactNumbers[2] = number;
                            if (checkButtonText != getString(R.string.contact3)) {
                                dbHelper.deleteContact(3);
                            }
                            dbHelper.addNewContact(String.valueOf(3),name, number);
                        }
                    }
                } while (people.moveToNext());
            }
            if (clickedButton.equalsIgnoreCase("myContact1")) {
                myB = (Button) findViewById(R.id.contact1);
                deleteContact1.setEnabled(true);
                deleteContact1.setVisibility(View.VISIBLE);
            }
            else if(clickedButton.equalsIgnoreCase("myContact2")) {
                myB = (Button) findViewById(R.id.contact2);
                deleteContact2.setEnabled(true);
                deleteContact2.setVisibility(View.VISIBLE);
            }
            else if(clickedButton.equalsIgnoreCase("myContact3")) {
                myB = (Button) findViewById(R.id.contact3);
                deleteContact3.setEnabled(true);
                deleteContact3.setVisibility(View.VISIBLE);
            }
            //set the button to contact name
            myB.setText(name + "         " + number);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case LOCATION_PERM: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perm granted", "yaya");
                }
                else {
                    Log.d("boo", "poop");
                }
            }
            case PERMISSION_ALL: {
                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    //locationButton.setEnabled(false);
                                                    //sendButton.setEnabled(false);
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            saveButton.setEnabled(false);
                            //locationButton.setEnabled(false);
                            //sendButton.setEnabled(false);
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

    @Override
    public void onSaveInstanceState(Bundle state)
    {
        if (mTwoPane)
        {
            getSupportFragmentManager().beginTransaction()
                    .remove(fragment)
                    .commit();
        }

        super.onSaveInstanceState(state);
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(checkAndRequestPermissions()) {
            saveButton.setEnabled(true);
        }
        //check if this is the first time this has been opened
        if (dbHelper.getContacts().size() == 0) {
            first_time_saved = true;
        }

    }

    @Override
    protected void onResume() {
        if (findViewById(R.id.message_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.d("true", "true fragment");
            mTwoPane = true;
            Bundle arguments = new Bundle();
            arguments.putBoolean(MessageDetailFragment.ARG_ITEM_ID, mTwoPane);
            fragment = new MessageDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.message_detail_container, fragment, TAG)
                    .commit();
        }

        else {
            Log.d("false", "in here");
            mTwoPane = false;
        }

        super.onResume();

//        try {
        String firstCheck = dbHelper.getFirstName();
        String lastCheck = dbHelper.getLastName();

        if (!firstCheck.isEmpty() && !lastCheck.isEmpty()) {
            firstName.setText(firstCheck);
            lastName.setText(lastCheck);
        }
        if (!dbHelper.getMessage().isEmpty()) {
            messageEntered.setText(dbHelper.getMessage());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.deleteUserInfo();
        dbHelper.insertUserInfo(firstName.getText().toString(), lastName.getText().toString());
    }

    public void messageChanged(String message) {
        if (!dbHelper.getMessage().isEmpty()) {
            messageEntered.setText(message);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}

