package mrd.tantablereader.activities;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.transform.Result;

import mrd.tantablereader.R;
import mrd.tantablereader.util.FileUtils;
import mrd.tantablereader.util.ObjectSerializer;

public class TANReaderMainActivity extends AppCompatActivity {

    private static String TAG = TANReaderMainActivity.class.getSimpleName();
    private static String PREFERENCES_ID = "TANReaderPrefs";
    public static String PREFERENCES_FILENAME = "fileName";
    public static String PREFERENCES_ENTRIES = "entries";
    private static final int SETTINGS_REQUEST_CODE = 43;

    private String SETTING_FILENAME;
    private HashMap<String, String> SETTTING_ENTRIES;

    private Button readButton;
    private ImageButton copyButton;
    private LinearLayout resultLayout;
    private TextView txtResult;
    private Spinner columnSpinner;
    private Spinner rowSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanreader_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        readButton = (Button) findViewById(R.id.btn_read);
        copyButton = (ImageButton) findViewById(R.id.btn_copy);
        resultLayout = (LinearLayout) findViewById(R.id.result_layout);
        txtResult = (TextView) findViewById(R.id.textView);
        columnSpinner = (Spinner) findViewById(R.id.column_spinner);
        rowSpinner = (Spinner) findViewById(R.id.row_spinner);
        setSupportActionBar(toolbar);
        loadSettings();
        if(SETTTING_ENTRIES == null) {
            Snackbar.make(findViewById(R.id.main_layout), "Please set up your TAN table in 'Settings'.", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        }
    }

    /**********************************************************************************************
     *                                                                          EVENT HANDLERS
     **********************************************************************************************/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tanreader_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, TANReaderSettingsActivity.class);
            intent.putExtra(PREFERENCES_FILENAME, SETTING_FILENAME);
            startActivityForResult(intent, SETTINGS_REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            String fileName = data.getStringExtra("fileName");
            HashMap<String, String> tans = (HashMap<String, String>) data.getSerializableExtra("entries");
            if(tans != null) {
               SETTTING_ENTRIES = tans;
               SETTING_FILENAME = fileName;
               saveSettings();
            }
        }
    }

    public void readButtonHandler(View view) {
        if(SETTTING_ENTRIES != null) {
            int row = rowSpinner.getSelectedItemPosition();
            int column = columnSpinner.getSelectedItemPosition();
            String key = FileUtils.columns[column] + ":" + String.valueOf(row+1);
            Log.d(TAG, "KEY: " + key);
            String entry = SETTTING_ENTRIES.get(key);
            txtResult.setText(entry);
            resultLayout.setVisibility(View.VISIBLE);
        }
    }

    public void copyButtonHandler(View view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("TAN VALUE", txtResult.getText());
        clipboard.setPrimaryClip(clip);
        Snackbar.make(view, "Copied to clipboard!", Snackbar.LENGTH_LONG)
                        .setAction("TAN VALUE", null).show();
    }

    /**********************************************************************************************
     *                                                                          UTIL FUNCTIONS
     **********************************************************************************************/
    private void loadSettings() {
        SharedPreferences prefs = getBaseContext().getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
        try {
            SETTTING_ENTRIES = (HashMap<String, String>) ObjectSerializer.deserialize(prefs.getString(PREFERENCES_ENTRIES, null));
            SETTING_FILENAME = prefs.getString(PREFERENCES_FILENAME, null);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveSettings() {
        SharedPreferences prefs = getBaseContext().getSharedPreferences(PREFERENCES_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        try {
            editor.putString(PREFERENCES_FILENAME, SETTING_FILENAME);
            editor.putString(PREFERENCES_ENTRIES, ObjectSerializer.serialize(SETTTING_ENTRIES));
            editor.commit();
            Snackbar.make(findViewById(R.id.main_layout), "Settings saved!", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(findViewById(R.id.main_layout), "Error saving preferences.", Snackbar.LENGTH_LONG)
                    .setAction("Error", null).show();
        }
    }
}
