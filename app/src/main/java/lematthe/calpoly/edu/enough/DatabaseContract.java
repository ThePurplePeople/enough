package lematthe.calpoly.edu.enough;

import android.provider.BaseColumns;

/**
 * Created by alexye on 10/29/16.
 */

public final class DatabaseContract {
    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "contactmessage.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BOOL_TYPE = " BOOLEAN";
    private static final String COMMA_SEP = ",";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private DatabaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class UserInfo implements BaseColumns {
        public static final String TABLE_NAME = "UserInfo";
        public static final String USER_FIRST_NAME = "firstName";
        public static final String USER_LAST_NAME = "lastName";
        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                USER_FIRST_NAME + TEXT_TYPE + "," +
                USER_LAST_NAME + TEXT_TYPE + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }

    public static abstract class EmergencyContacts implements BaseColumns {

        public static final String TABLE_NAME = "EmergencyContacts";
        public static final String CONTACT_NUMBER = "number";
        public static final String CONTACT_NAME = "name";
        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                CONTACT_NAME + TEXT_TYPE + "," +
                CONTACT_NUMBER + TEXT_TYPE + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Messages implements BaseColumns {
        public static final String TABLE_NAME = "Messages";
        public static final String COLUMN_NAME_MESSAGE = "message";
        //public static final String COLUMN_NAME_SELECTED = "selected";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_MESSAGE + TEXT_TYPE + " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
