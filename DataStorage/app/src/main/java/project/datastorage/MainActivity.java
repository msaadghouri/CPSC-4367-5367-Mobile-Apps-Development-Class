package project.datastorage;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {
    public static final String PREF_NAME = "DataStorage";
    private static final String IS_LOGGED_IN = "IsLoggedIn";
    private EditText edName, edPass, edMail, edNum;
    private Button save, fetch;
    private DBUtils dbUtils;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGUI();

        dbUtils = new DBUtils(getApplicationContext());
        preferences = getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE); //0

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreference(edName.getText().toString(), edPass.getText().toString(), edMail.getText().toString(),
                        Integer.parseInt(edNum.getText().toString()));

                saveToDB(edName.getText().toString(), edPass.getText().toString(), edMail.getText().toString(),
                        Integer.parseInt(edNum.getText().toString()));
//
//                //Get Runtime Permission for writing external storage
                writeToExternalStorage(edName.getText().toString());
                writeToInternalStorage(edName.getText().toString());
            }
        });

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    Log.d("Read Internal Storage", "" + readFromInternalStorage());
                    Log.d("Read External Storage", "" + readFromExternalStorage());
                    Log.d("Read from Preference", "" + preferences.getString("Email", null));
                    Log.d("Read from Database", "" + dbUtils.getNumber());

            }
        });
    }


    private void initGUI() {

        edName = (EditText) findViewById(R.id.editText6);
        edPass = (EditText) findViewById(R.id.editText7);
        edMail = (EditText) findViewById(R.id.editText8);
        edNum = (EditText) findViewById(R.id.editText9);
        save = (Button) findViewById(R.id.button2);
        fetch = (Button) findViewById(R.id.button3);
    }

    /*
    * Shared preferences can be fetched using getSharedPreferences() method
    * Editor- edit and save the changes in shared preferences
    * */
    public void savePreference(String name, String password, String eMail, int number) {
        SharedPreferences.Editor editor = preferences.edit();
        //Primitive data types like booleans, floats, ints, longs, and strings are supported
        //Is logged in used to manage a login session in this example
        editor.putBoolean(IS_LOGGED_IN, true);
        editor.putString("UserName", name);
        editor.putString("Password", password);
        editor.putString("E-mail", eMail);
        editor.putInt("Number", number);

        editor.commit();
    }
    /*
    * Content values are used to save data to sqlite database.
    * Example of INSERT query.
    * */
    public void saveToDB(String name, String password, String eMail, int number) {
        DBUtils dbUtils = new DBUtils(getApplicationContext());
        SQLiteDatabase sqLiteDatabase = dbUtils.open();
        ContentValues cv = new ContentValues();
        cv.put("UserName", name);
        cv.put("Password", password);
        cv.put("Email", eMail);
        cv.put("Number", number);
        sqLiteDatabase.insert("UserDetails", null, cv);
        dbUtils.close();
    }
    /*
    * Writing to internal storage.
    * Commented code is another option to save to internal storage.
    * */
    public void writeToInternalStorage(String userName) {
        String fileName = "UserNameFile";
//        File file = new File(getApplicationContext().getFilesDir(), "MyDirectory");
//        if (!file.exists()) {
//            file.mkdir();
//        }
        try {
            FileOutputStream outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(userName.getBytes());
            outputStream.close();

//            File fileToWrite = new File(file, fileName);
//            FileWriter writer = new FileWriter(fileToWrite);
//            writer.append(userName);
//            writer.flush();
//            writer.close();

        } catch (Exception e) {

        }
    }

    public String readFromInternalStorage() {
        String temp = "";
        try {
            FileInputStream fin = openFileInput("UserNameFile");
            int c;
            while ((c = fin.read()) != -1) {
                temp = temp + Character.toString((char) c);
            }
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public void writeToExternalStorage(String userName) {
        if (isExternalStorageWritable()) {

            File file = new File(Environment.getExternalStorageDirectory(), "MyDirectory");

            if (!file.exists()) {
                file.mkdir();
            }
            try {
                File fileToWrite = new File(file, "FileName");
                FileOutputStream outputStream = new FileOutputStream(fileToWrite);
                outputStream.write(userName.getBytes());
                outputStream.close();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String readFromExternalStorage() {
        String temp = "";
        if (isExternalStorageReadable()) {
            try {
                FileInputStream fin = new FileInputStream(new File(Environment.getExternalStorageDirectory()
                        .getAbsolutePath(), "MyDirectory/FileName"));
                int c;
                while ((c = fin.read()) != -1) {
                    temp = temp + Character.toString((char) c);
                }
                fin.close();
            } catch (Exception e) {

            }
        }
        return temp;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean getPreference() {
        return preferences.getBoolean(IS_LOGGED_IN, false);
    }
}
