package lematthe.calpoly.edu.enough;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    // Using two arrays because we need predictable indexing and unchanging sizes
    public String[] newContactNames = new String[3];
    public String[] newContactNumbers = new String[3];

    boolean setup_done = false;
    String TAG = "here";
    Button sendButton;
    Button myContact1;
    Button myContact2;
    Button myContact3;
    String clickedButton;
    Button selectM;
    Button saveButton;
    String checkButtonText;
    EditText firstName;
    EditText lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //implicit layout inflator
        setContentView(R.layout.setting_fields);

        // carry on the normal flow, as the case of  permissions  granted.
        //find contact buttons

        myContact1 = (Button) findViewById(R.id.contact1);
        myContact2 = (Button) findViewById(R.id.contact2);
        myContact3 = (Button) findViewById(R.id.contact3);
        selectM = (Button) findViewById(R.id.selectmessage);

        //send button here for now to show functionality
        sendButton = (Button) findViewById(R.id.save);

        dbHelper = new DatabaseHelper(getApplicationContext());
        saveButton = (Button) findViewById(R.id.save);

        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);


        // Set contacts if saved
        if (savedInstanceState != null) {
            Log.d("saved", "saved");
        }
        ArrayList<String> savedContacts = dbHelper.getContacts();
        if (savedContacts.size() > 0) {
            Log.d("bigger than", "big");
            Log.d("size", String.valueOf(savedContacts.size()));
            for (int i = 0; i < savedContacts.size(); i += 2) {
                String name = savedContacts.get(i);
                String number = savedContacts.get(i + 1);

                if (i == 0) {
                    Button b = (Button) findViewById(R.id.contact1);
                    b.setText(name + "         " + number);
                }
                if (i == 2) {
                    Button b = (Button) findViewById(R.id.contact2);
                    b.setText(name + "         " + number);
                }
                if (i == 4) {
                    Button b = (Button) findViewById(R.id.contact3);
                    b.setText(name + "         " + number);
                }
            }
        }
        else {
            Log.d("here", "small");
            myContact1.setText("Contact 1");
            myContact2.setText("Contact 2");
            myContact3.setText("Contact 3");
        }

        myContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButtonText = ((Button) v).getText().toString();
                contactClicked();
                clickedButton = "myContact1";
                try {
                    Log.d("what does this do", "que");
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

        selectM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("hi", "hi");
                context.startActivity(intent);
            }
        });

        if(checkAndRequestPermissions()) {
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
                    Log.d("all fields are filled", "in here");
                    ArrayList<String> savedContacts = dbHelper.getContacts();
                    for (int i = 0; i < savedContacts.size(); i += 2) {
                        String name = savedContacts.get(i);
                        String number = savedContacts.get(i + 1);
                        String initialMessage = "Hi " + name + " you have designated as an emergency contact by "
                                + dbHelper.getFirstName().toString() + " " + dbHelper.getLastName().toString() +
                                "on the application eNOugh. Be aware that in the event of an emergency you will be contacted via text message through this application.";
                        //change to real number just using emulator right now
                        sendSMS(getApplicationContext(), "5556", initialMessage);

                    }
                }
                setup_done = true;

                if (setup_done == true) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ScreenSaverActivity.class);
                    intent.putExtra("sup", "sup");
                    context.startActivity(intent);

                }
            }
        });

    }

    protected void sendSMS(Context context, String phoneNumber, String message) {
        Log.d("sms sent to", phoneNumber);
        Intent sentIntent = new Intent(context, getClass());
        sentIntent.setAction(ACTION_SMS_SENT);
        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0,
                sentIntent, 0);
        Intent deliveredIntent = new Intent(context, getClass());
        deliveredIntent.setAction(ACTION_SMS_DELIVERED);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0,
                deliveredIntent, 0);

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
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
                            if (checkButtonText != "Contact 1") {
                                dbHelper.deleteContact(1);
                            }
                            dbHelper.addNewContact(String.valueOf(1), name, number);
                        }
                        else if(clickedButton.equals("myContact2")) {
                            newContactNames[1] = name;
                            newContactNumbers[1] = number;
                            if (checkButtonText != "Contact 2") {
                                Log.d("deleting 2", "two");
                                dbHelper.deleteContact(2);
                            }
                            dbHelper.addNewContact(String.valueOf(2), name, number);
                        }
                        else if(clickedButton.equals("myContact3")) {
                            newContactNames[2] = name;
                            newContactNumbers[2] = number;
                            if (checkButtonText != "Contact 3") {
                                dbHelper.deleteContact(3);
                            }
                            dbHelper.addNewContact(String.valueOf(3),name, number);
                        }
                    }
                } while (people.moveToNext());
            }
            if (clickedButton.equalsIgnoreCase("myContact1")) {
                myB = (Button) findViewById(R.id.contact1);
            }
            else if(clickedButton.equalsIgnoreCase("myContact2")) {
                myB = (Button) findViewById(R.id.contact2);
            }
            else if(clickedButton.equalsIgnoreCase("myContact3")) {
                myB = (Button) findViewById(R.id.contact3);
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
    protected void onStart() {
        super.onStart();
        if(checkAndRequestPermissions()) {
            saveButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String firstCheck = dbHelper.getFirstName();
        String lastCheck = dbHelper.getLastName();

        if (!firstCheck.isEmpty() && !lastCheck.isEmpty()) {
            firstName.setText(firstCheck);
            lastName.setText(lastCheck);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        dbHelper.deleteUserInfo();
        dbHelper.insertUserInfo(firstName.getText().toString(), lastName.getText().toString());
    }

}

