package lematthe.calpoly.edu.enough;

import android.Manifest;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    public final int LOCATION_PERM = 2;

    String TAG = "here";
    Button sendButton;
    Button locationButton;
    Button myContact1;
    Button myContact2;
    Button myContact3;
    String clickedButton;
    Button selectM;
    Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //implicit layout inflator
        setContentView(R.layout.setting_fields);

        // carry on the normal flow, as the case of  permissions  granted.
        //find contact buttons

        if (savedInstanceState!=null) {
            Log.d("ALEX databse", "set the contact buttons with name and number");
        }
        myContact1 = (Button) findViewById(R.id.contact1);
        myContact2 = (Button) findViewById(R.id.contact2);
        myContact3 = (Button) findViewById(R.id.contact3);
        selectM = (Button) findViewById(R.id.selectmessage);

        //send button here for now to show functionality
        sendButton = (Button) findViewById(R.id.save);

        dbHelper = new DatabaseHelper(getApplicationContext());
        saveButton = (Button) findViewById(R.id.save);
        //locationButton = (Button) findViewById(R.id.location);

        myContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactClicked();
                clickedButton = "myContact1";
            }
        });

        myContact2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactClicked();
                clickedButton = "myContact2";

            }
        });

        myContact3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactClicked();
                clickedButton = "myContact3";

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
                Log.d("ALEX database: ", "check if the fields are all filled if YES then save");
                Log.d("if not filled", "we need to not let the activitiy finish");
            }
        });

        //Sending a Text functionality implement in widget
        /*sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                System.out.println("Send clicked");
                ArrayList<String> numbers = dbHelper.sendEmergency();
                for(String n : numbers) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(
                            "+1" + n,
                            null,
                            "This is a test message!",
                            null,
                            null);
                    }
            }
        });*/

        /*locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("clicked", "get location");
            }
        });*/

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
                    Log.d("name", name);
                    Log.d("number", number);

                   Log.d("ALEX databse", "add the name and phone number to database");
                    dbHelper.addNewContact(number);
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
            //sendButton.setEnabled(true);
            //locationButton.setEnabled(true);

        }
    }

}

