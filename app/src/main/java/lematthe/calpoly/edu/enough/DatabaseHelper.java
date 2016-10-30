package lematthe.calpoly.edu.enough;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
     * @param context The context of the activity or application.
     */
    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        System.out.println("DB Constructor Hit");
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println(DatabaseContract.EmergencyContacts.CREATE_TABLE);
        System.out.println(DatabaseContract.Messages.CREATE_TABLE);
        db.execSQL(DatabaseContract.EmergencyContacts.CREATE_TABLE);
        db.execSQL(DatabaseContract.Messages.CREATE_TABLE);

    }

    // Method is called during an upgrade of the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.EmergencyContacts.DELETE_TABLE);
        db.execSQL(DatabaseContract.Messages.DELETE_TABLE);
        onCreate(db);
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
        template1.put(DatabaseContract.Messages.COLUMN_NAME_SELECTED, false);

        ContentValues template2 = new ContentValues();
        template2.put(DatabaseContract.Messages.COLUMN_NAME_MESSAGE, Resources.getSystem().getString(R.string.template_message_2));
        template2.put(DatabaseContract.Messages.COLUMN_NAME_SELECTED, false);

        db.insert(DatabaseContract.Messages.TABLE_NAME, null, template1);
        db.insert(DatabaseContract.Messages.TABLE_NAME, null, template2);
    }

    /**
     * Adds a new contact to the EmergencyContact table.
     * @param number The phone number of an SMS supported device.
     */
    public void addNewContact(String number) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contact = new ContentValues();

        contact.put(DatabaseContract.EmergencyContacts.COLUMN_NAME_NUMBER, number);
        db.insert(DatabaseContract.EmergencyContacts.TABLE_NAME, null, contact);
    }

}