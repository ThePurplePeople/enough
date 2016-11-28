package lematthe.calpoly.edu.enough;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
/**
 * Created by alexye on 10/29/16.
 */

/**
 * Database helper class for simple SQLite persistent storage
 *
 * Table schema can be references from the DatabaseContract class
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    /**
     * Constructor for creating a DatabaseHelper that provides database "endpoints" local
     * to the application.
     *
     * @param context The context of the activity or application.
     */
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(DatabaseContract.EmergencyContacts.CREATE_TABLE);
        System.out.println(DatabaseContract.Messages.CREATE_TABLE);
        db.execSQL(DatabaseContract.EmergencyContacts.CREATE_TABLE);
        db.execSQL(DatabaseContract.Messages.CREATE_TABLE);
        db.execSQL(DatabaseContract.UserInfo.CREATE_TABLE);
    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion != oldVersion) {
            db.execSQL(DatabaseContract.EmergencyContacts.DELETE_TABLE);
            db.execSQL(DatabaseContract.Messages.DELETE_TABLE);
            db.execSQL(DatabaseContract.UserInfo.DELETE_TABLE);
            onCreate(db);
            System.out.println("upgrade");
        }

    }

    /**
     * Inserts the stock template messages specified in resources strings.xml.
     */
    private void insertStockMessages() {
        // Adds all the template messages to the database on creations.
        // All template messages are referenced from strings.xml resources.
        SQLiteDatabase db = getWritableDatabase();
        ContentValues template1 = new ContentValues();
        template1.put(DatabaseContract.Messages.COLUMN_NAME_MESSAGE, Resources.getSystem().getString(R.string.template_message_1));
        //template1.put(DatabaseContract.Messages.COLUMN_NAME_SELECTED, false);

        ContentValues template2 = new ContentValues();
        template2.put(DatabaseContract.Messages.COLUMN_NAME_MESSAGE, Resources.getSystem().getString(R.string.template_message_2));
        //template2.put(DatabaseContract.Messages.COLUMN_NAME_SELECTED, false);

        db.insert(DatabaseContract.Messages.TABLE_NAME, null, template1);
        db.insert(DatabaseContract.Messages.TABLE_NAME, null, template2);
    }

    /*
     * A Method to Add the users first name and last name
     * @param last Users last name
     * @param first Users first name
     */
    public void insertUserInfo(String first, String last) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues userInfo = new ContentValues();
        userInfo.put(DatabaseContract.UserInfo._ID, 1);
        userInfo.put(DatabaseContract.UserInfo.USER_FIRST_NAME, first);
        userInfo.put(DatabaseContract.UserInfo.USER_LAST_NAME, last);
        db.insert(DatabaseContract.UserInfo.TABLE_NAME, null, userInfo);
    }

    /*
    * A Method to Remove the users first name and last name
    */
    public boolean deleteUserInfo() {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(DatabaseContract.UserInfo.TABLE_NAME, "_ID = 1", null) > 0;
    }

    /*
    * A Method to get the users first name
    * @return Users first name
    */
    public String getFirstName() {
        String first = "";
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT firstName FROM UserInfo WHERE _ID = 1";
        try {
            Cursor cursor = db.rawQuery(select, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst())
                    first = cursor.getString(0);
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }
        return first;
    }

    /*
  * A Method to get the users last name
  * @return Users last name
  */
    public String getLastName() {
        String last = "";
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT lastName FROM UserInfo WHERE _ID = 1";
        try {
            Cursor cursor = db.rawQuery(select, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst())
                    last = cursor.getString(0);
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }
        return last;
    }

    /*
    * A Method to insert the Message into the database
    * @param the Users selected message
    */
    public void insertMessage(String message) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues messageValue = new ContentValues();
        messageValue.put(DatabaseContract.Messages._ID, 1);
        messageValue.put(DatabaseContract.Messages.COLUMN_NAME_MESSAGE, message);
        db.insert(DatabaseContract.Messages.TABLE_NAME, null, messageValue);
    }

    /*
    * A Method to delete the Users message
    * @return true if deleted
    */
    public boolean deleteMessage() {
        SQLiteDatabase db = getReadableDatabase();
        return db.delete(DatabaseContract.Messages.TABLE_NAME, "_ID = 1", null) > 0;
    }

    /*
    * A Method to get the users message
    * @return the Users message
    */
    public String getMessage() {
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT message FROM Messages WHERE _ID = 1";
        String message = "";
        try {
            Cursor cursor = db.rawQuery(select, null);
            try {
                // looping through all rows and adding to list
                if (cursor.moveToFirst())
                    message = cursor.getString(0);
            } finally {
                try {
                    cursor.close();
                } catch (Exception ignore) {
                }
            }
        } finally {
            try {
                db.close();
            } catch (Exception ignore) {
            }
        }
        return message;
    }

    /*
     * Deletes a contact from the EmergencyContact table
     * @param id The id of the contact to delete from
     * @return boolean true if contact successfully deleted, otherwise false
     */

    public boolean deleteContact(int id) {
        SQLiteDatabase db = getReadableDatabase();
        //String delete = "DELETE FROM EmergencyContacts WHERE id = " + String.valueOf(id) + ";";
        boolean check = db.delete("EmergencyContacts", "_ID = " + String.valueOf(id), null) > 0;
        Log.d("check", String.valueOf(check));
        return check;
    }


    /**
     * Adds a new contact to the EmergencyContact table.
     * @param name The name of an EmergencyContact
     * @param number The phone number of an SMS supported device.
     *
     * @return boolean true if contact successfully added, otherwise false
     */
    public boolean addNewContact(String id, String name, String number) {
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT number FROM EmergencyContacts";
        ArrayList<String> numbers = new ArrayList<>();
        // Check if there are already 3 entries inside
        try {
            Cursor cursor = db.rawQuery(select, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        numbers.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }
        if(numbers.size() < 3) {
            db = getWritableDatabase();
            ContentValues contact = new ContentValues();
            contact.put(DatabaseContract.EmergencyContacts._ID, id);
            contact.put(DatabaseContract.EmergencyContacts.CONTACT_NAME, name);
            contact.put(DatabaseContract.EmergencyContacts.CONTACT_NUMBER, number);
            db.insert(DatabaseContract.EmergencyContacts.TABLE_NAME, null, contact);

            return true;
        }
        System.out.println("Database for contacts is already full");
        return false;
    }

    /**
     * Returns all emergency contacts stored in list.
     * @return ArrayList containing phone numbers sans country code
     */
    public ArrayList<String> getNumbers() {
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT number FROM EmergencyContacts";
        ArrayList<String> numbers = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery(select, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        numbers.add(cursor.getString(0));
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }
        return numbers;
    }

    /**
     * Data for all members added to emergency contacts are returned
     * @return ArrayList containing Name and Number of all Contacts
     */
    public ArrayList<String> getContacts() {
        SQLiteDatabase db = getReadableDatabase();
        String select = "SELECT name, number FROM EmergencyContacts";

        ArrayList<String> contacts = new ArrayList<String>();

        try {
            Cursor cursor = db.rawQuery(select, null);
            try {

                // looping through all rows and adding to list
                if (cursor.moveToFirst()) {
                    do {
                        contacts.add(cursor.getString(0));
                        contacts.add(cursor.getString(1));
                    } while (cursor.moveToNext());
                }

            } finally {
                try { cursor.close(); } catch (Exception ignore) {}
            }
        } finally {
            try { db.close(); } catch (Exception ignore) {}
        }

        return contacts;
    }



}
