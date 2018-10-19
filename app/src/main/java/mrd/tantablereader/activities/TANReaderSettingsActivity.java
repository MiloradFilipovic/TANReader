package mrd.tantablereader.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import mrd.tantablereader.R;
import mrd.tantablereader.util.FileUtils;

public class TANReaderSettingsActivity extends AppCompatActivity {

    private static String TAG = TANReaderSettingsActivity.class.getSimpleName();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final int READ_REQUEST_CODE = 42;

    private TextView txtFile;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanreader_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        txtFile = (TextView) findViewById(R.id.txt_file);
        setSupportActionBar(toolbar);

        String fileName = getIntent().getStringExtra(TANReaderMainActivity.PREFERENCES_FILENAME);
        if(fileName != null) {
            txtFile.setText(fileName);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void browseFiles(View view) {
        verifyStoragePermissions(TANReaderSettingsActivity.this);
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (resultData != null) {
                String selectedFile = getFileName(resultData.getData());
                if(!selectedFile.endsWith(".txt")) {
                    Snackbar.make(findViewById(R.id.settings_layout), "Please choose a text file.", Snackbar.LENGTH_LONG)
                            .setAction("Error", null).show();
                }else {
                    txtFile.setText(selectedFile);

                    try {
                        HashMap<String, String> tans = FileUtils.readTANFile(getContentResolver(), resultData.getData());
                        Intent returnIntent = getIntent();
                        returnIntent.putExtra("fileName", selectedFile);
                        returnIntent.putExtra("entries", tans);
                        setResult(Activity.RESULT_OK, returnIntent);
                    } catch (IOException e) {
                        Snackbar.make(findViewById(R.id.settings_layout), "Error reading TAN file.", Snackbar.LENGTH_LONG)
                                .setAction("Error", null).show();
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String uriString = uri.toString();
        File file = new File(uriString);
        String fileName = null;

        if(uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = TANReaderSettingsActivity.this.getContentResolver().query(uri, null, null,null, null);
                if(cursor != null && cursor.moveToFirst()) {
                    fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                }
            }finally {
                cursor.close();
            }

        } else if (uriString.startsWith("file://")) {
            fileName = file.getName();
        }
        return fileName;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
