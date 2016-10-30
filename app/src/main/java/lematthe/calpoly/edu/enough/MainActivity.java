package lematthe.calpoly.edu.enough;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;
    public DatabaseHelper dbHelper; // The database helper(manager)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button myContact1 = (Button) findViewById(R.id.contact1);
        Button sendButton = (Button) findViewById(R.id.send);

        dbHelper = new DatabaseHelper(getApplicationContext());

        myContact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });
    }

//    protected void testDb() {
//        db = dbHelper.getReadableDatabase();
//        List<String> list = new ArrayList<String>();
//
//        Cursor c = db.rawQuery("SELECT * FROM " + DatabaseContract.Messages.TABLE_NAME, null);
//        System.out.println("Query test for template messages");
//        try {
//            while(c.moveToNext()) {
//                String a = c.getString(c.getColumnIndex(DatabaseContract.Messages.COLUMN_NAME_MESSAGE));
//                list.add(a);
//            }
//        } finally {
//            System.out.println("Closed cursor to database output");
//            c.close();
//        }
//
//        c = db.rawQuery("SELECT * FROM " + DatabaseContract.EmergencyContacts.TABLE_NAME, null);
//        System.out.println("Query test for Emergency Contact finished");
//        try {
//            while(c.moveToNext()) {
//                String a = c.getString(c.getColumnIndex(DatabaseContract.EmergencyContacts.COLUMN_NAME_NUMBER));
//                list.add(a);
//            }
//        } finally {
//            System.out.println("Closed cursor to database output");
//            c.close();
//        }
//
//        for(String s : list) {
//            System.out.println(s);
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String contact1 = cursor.getString(column);
            Log.d("phone number", cursor.getString(column));
            dbHelper.addNewContact(contact1);
        }
    }

    protected void sendSMSMessage() {
        Log.i("Send SMS", "");
        String message = "hola testing";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("5556", null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
