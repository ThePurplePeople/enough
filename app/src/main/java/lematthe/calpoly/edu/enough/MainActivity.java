package lematthe.calpoly.edu.enough;

import android.Manifest;
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
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //declare globally, this can be any int
    public final int MY_LOCATION_REQUEST = 456;
    public final int MY_SMS_REQUEST = 123;
    public final int PICK_CONTACT = 2015;
    String contact1;
    Button sendButton;
    Button locationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myContact1 = (Button) findViewById(R.id.contact1);
        sendButton = (Button) findViewById(R.id.send);

        locationButton = (Button) findViewById(R.id.location);

        myContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(
                        "5556",
                        null,
                        "Hola",
                        null,
                        null);
            }

        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("PLAYGROUND", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, MY_SMS_REQUEST);
            sendButton.setEnabled(false);
        } else {
            Log.d("PLAYGROUND", "Permission is granted");
        }

        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });


    }

    public void getLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d("PLAYGROUND", "Permission is not granted, requesting");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_REQUEST);
            locationButton.setEnabled(false);
        } else {
            Log.d("PLAYGROUND", "Permission is granted");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            contact1 = cursor.getString(column);
            Log.d("phone number", cursor.getString(column));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_LOCATION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PLAYGROUND", "Permission has been granted");
                    locationButton.setEnabled(true);
                } else {
                    Log.d("PLAYGROUND", "Permission has been denied or request cancelled");
                    locationButton.setEnabled(false);
                }
                return;
            }
            case MY_SMS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PLAYGROUND", "Permission has been granted");
                    sendButton.setEnabled(true);
                } else {
                    Log.d("PLAYGROUND", "Permission has been denied or request cancelled");
                    sendButton.setEnabled(false);
                }
            }

        }
    }

}
