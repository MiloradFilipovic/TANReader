package mrd.tantablereader.util;

import android.content.ContentResolver;
import android.net.Uri;
import android.nfc.Tag;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class FileUtils {

    private static String TAG = FileUtils.class.getSimpleName();
    public static String[] columns = new String[] {"A", "B", "C", "D"};

    public static HashMap<String, String> readTANFile(ContentResolver contentResolver, Uri uri) throws IOException {
        HashMap<String, String> tanEntries = new HashMap<String, String>();
        InputStream inputStream = contentResolver.openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        int row = 1;
        while((line = reader.readLine()) != null) {
            if(line.trim() != "") {
                String[] line_parts = line.split(" ");
                for(int i=0; i<line_parts.length; i++) {
                    String entry = line_parts[i];
                    String key = columns[i] + ":" + String.valueOf(row);
                    tanEntries.put(key, entry);
                }
            }
            row += 1;
        }
        inputStream.close();
        return tanEntries;
    }
}
