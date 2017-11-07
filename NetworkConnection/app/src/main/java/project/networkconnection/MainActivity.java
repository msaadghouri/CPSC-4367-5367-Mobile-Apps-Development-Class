package project.networkconnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private Button checkConnection, toServer, fromServer, fromMySQL;
    private DataModel dataModel;
    private ProgressDialog pDialog;
    static String url=null;
    static String driver=null;
    static{
        /* Prerequisites of MySQL connectivity
        Add mysql-connector-java library to libs folder
        https://dev.mysql.com/downloads/connector/j/5.1.html
        In app's build.gradle, compile the jar in dependencies
        compile files('libs/mysql-connector-java-5.1.39-bin.jar') */

        try {
            String driverclass = "com.mysql.jdbc.Driver";
            String dbuser = "root";
            String dbpassword = "root";
            String dburl = "jdbc:mysql://144.167.241.193:3306/"; //jdbc:mysql://Address:To:MySQL:Server:portNumber/
            String dataBaseName = "arkansas";

            url=dburl+dataBaseName+"?user="+dbuser+"&password="+dbpassword;
            driver=driverclass;
            Class.forName(driver);
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGUI();
        checkConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetworkConnection();

            }
        });

        toServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataModel = new DataModel("Saad", "Ghouri");
                new SendToServer().execute(dataModel.toString());
            }
        });

        fromServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new FetchFromServer().execute();
            }
        });

        fromMySQL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new MySQLConnectivity().execute();
            }
        });
    }

    private void initGUI() {
        checkConnection = (Button) findViewById(R.id.checkNetwork);
        toServer = (Button) findViewById(R.id.toServer);
        fromServer = (Button) findViewById(R.id.fromServer);
        fromMySQL = (Button) findViewById(R.id.fromMySQL);
    }

    private void checkNetworkConnection() {
        //ConnectivityManager- Provides the state of network connectivity
        //We refer to WiFi and mobile data in this case
        ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //NetworkInfo- provides status of available network interfaces
        NetworkInfo nwInfo = cManager.getActiveNetworkInfo();

        if (nwInfo != null && nwInfo.isConnected()) {
            //If there is internet connectivity
            if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Connected to Mobile's data
                showSnackBar("Connected to " + nwInfo.getTypeName());

            } else if (nwInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                //Connected to WiFi
                Log.d("Connectivity","Connected to " + nwInfo.getTypeName());
                showSnackBar("Connected to " + nwInfo.getTypeName());
            }
        } else {
            showSnackBar("No Internet Connection");
        }
    }


    //Notify user about network connection using Snack Bar
    private void showSnackBar(String connectionStatus) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.snackBarTextView), connectionStatus, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    //AysncTask paramters: Params, Progress and Result
    private class SendToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            //pDialog: prevents the user's interaction with the app
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Sending Data to Server");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /* Params are the paramters of doInBackground and result is the return type of it
        In this case, String is the params as well as return type */
        @Override
        protected String doInBackground(String... strings) {
            String jsonData = strings[0];
            try {
                //Link to REST Webservice
                URL url = new URL("http://144.167.241.193:8080/UALR/Class323/sendToServer");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                // When sending data to server set the request method to POST
                conn.setRequestMethod("POST");
                // Content type can be changed based on type of data being sent; XML, JSON or Text
                conn.setRequestProperty("Content-Type",
                        "application/json");
                conn.setDoInput(true);
                //Set to true as the data is being sent
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonData);

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    //Used to read the text from a character-based input stream
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();

                } else {
                    return "" + responseCode;
                }
            } catch (Exception e) {
                return "Exception Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Once the job is done, dismiss the Progress dialog
            pDialog.dismiss();
            showSnackBar("Send to Server result: " + result);
        }
    }

    private class FetchFromServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Fetching Data from Server");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                //Link to REST Webservice
                URL url = new URL("http://144.167.241.193:8080/UALR/Class323/getFromServer");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                //Set the request method to GET while fetching data from server
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type",
                        "application/json");
                conn.setDoInput(true);
                //Set to false while fetching the data.
                conn.setDoOutput(false);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();

                } else {
                    return "" + responseCode;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "Exception Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            //Perform any operation after having the result
            showSnackBar("Fetched Data: " + result);
        }
    }

    private class MySQLConnectivity extends AsyncTask<Void,Void,Void>{
        String userRefId=null;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                //Establish the connection with MySQL server
                Connection conn= DriverManager.getConnection(url);
                String queryStr="Provide your sql query here";
                //Interface used to execute parameterized query
                Statement stmt = conn.createStatement();
                //Maintains a cursor pointing to a row of a table
                ResultSet rset = stmt.executeQuery(queryStr);
                while (rset.next())
                {
                     userRefId = rset.getString("UserRefId");
                }
                rset.close();
                stmt.close();
                conn.close();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showSnackBar(userRefId);
        }
    }
}
