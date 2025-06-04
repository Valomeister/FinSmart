package com.example.finsmart.main_activity;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {

    public static void copyPortfolioDataIfNeeded(Context context) {
        String fileName = "portfolio_data.csv";
        File file = new File(context.getFilesDir(), fileName);

        if (!file.exists()) {
            Log.d("tmp", "copying started");
            try (InputStream is = context.getAssets().open(fileName);
                 OutputStream os = new FileOutputStream(file)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
            } catch (Exception e) {
                Log.d("tmp", "copying failed");
                e.printStackTrace();
            }
        }
    }
}
