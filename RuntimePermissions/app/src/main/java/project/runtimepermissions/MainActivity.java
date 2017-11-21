package project.runtimepermissions;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_CALLBACK_REQUESTCODE = 100;
    public static final int REQUEST_PERMISSION_REQUESTCODE = 101;
    public static final String[] PERMISSIONS_REQUIRED = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG};
    private SharedPreferences permissionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionStatus = getSharedPreferences("permissionStatus", MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_REQUIRED[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_REQUIRED[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_REQUIRED[2]) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_REQUIRED[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_REQUIRED[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_REQUIRED[2])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert").setMessage("Accept Permissions onCreate activity");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_REQUIRED, PERMISSION_CALLBACK_REQUESTCODE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(PERMISSIONS_REQUIRED[0], false)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Permission Alert");
                builder.setMessage("Manually accepting permission");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_REQUESTCODE);
                        Toast.makeText(getApplicationContext(), "Nagivating to Permissions", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_REQUIRED, PERMISSION_CALLBACK_REQUESTCODE);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(PERMISSIONS_REQUIRED[0], true);
            editor.commit();
        } else {
            afterPermissionsAccepted();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_REQUESTCODE) {
            boolean haveAllPermissions = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    haveAllPermissions = true;
                } else {
                    haveAllPermissions = false;
                    break;
                }
            }
            if (haveAllPermissions) {
                afterPermissionsAccepted();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_REQUIRED[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_REQUIRED[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, PERMISSIONS_REQUIRED[2])) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Alert- On Request Permission Result");
                builder.setMessage("Accept all permissions to proceed.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS_REQUIRED, PERMISSION_CALLBACK_REQUESTCODE);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getApplicationContext(), "Unable to get Permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_REQUESTCODE) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_REQUIRED[0]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_REQUIRED[1]) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, PERMISSIONS_REQUIRED[2]) == PackageManager.PERMISSION_GRANTED) {
                afterPermissionsAccepted();
            } else {
                Toast.makeText(getApplicationContext(), "Accept all permissions", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void afterPermissionsAccepted() {
        Toast.makeText(getApplicationContext(), "Do your work here after accepting all permissions", Toast.LENGTH_LONG).show();
    }
}
