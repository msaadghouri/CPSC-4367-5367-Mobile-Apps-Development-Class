package project.datastorage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mohammad-Ghouri on 11/8/17.
 */

public class DBUtils {

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private static String DB_NAME = "DataStorage";
    private int DB_VERSION = 1;

    private final String TABLE_NAME = "UserDetails";
    private final String USER_ID = "UserID";
    private final String USER_NAME = "UserName";
    private final String PASSWORD = "Password";
    private final String EMAIL = "Email";
    private final String NUMBER = "Number";

    public DBUtils(Context context) {
        dbHelper = new DBHelper(context, DB_NAME, null, DB_VERSION);
    }
//To write data to SQLite table
    public SQLiteDatabase open() {
        sqLiteDatabase = dbHelper.getWritableDatabase();
        return sqLiteDatabase;
    }
//Always close a connection after database interaction
    public void close() {
        sqLiteDatabase.close();
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//Create a table here. Make sure to pass 'If not exists'
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + USER_NAME + " TEXT,"
                            + PASSWORD + " TEXT,"
                            + EMAIL + " TEXT,"
                            + NUMBER + " TEXT);"
            );

        }
//Drops the table on upgrade and creates a new one
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + ";");
            onCreate(db);
        }
    }
//Cursors points to the rows of extracted data. Example using query.
    public boolean containUsers() {
//      To fetch data from table, make sure to make the database readable.
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    return true;
            }
        }
        return false;
    }
//Example using raw query. Ex: select * from table name;
    public String getNumber() {
        String number="";
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        String query = "Pass your sql query here";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                if (cursor.getCount() > 0)
                    number= cursor.getString(cursor.getColumnIndex(NUMBER));
                return number;
            }
        }
        return number;
    }

}
