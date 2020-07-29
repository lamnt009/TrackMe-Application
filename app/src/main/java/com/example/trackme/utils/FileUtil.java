package com.example.trackme.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
    private static final String TAG = "FileUtil";

    public static boolean writeFile(Context context, String fileName, String content) {
        if (context == null || TextUtils.isEmpty(fileName) || TextUtils.isEmpty(content)){
            return false;
        }
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            if (e.getMessage() != null)
                Log.e(TAG, e.getMessage());
            else
                e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeImage(Context context, Bitmap bitmap, String fileName) {
        if (context == null || TextUtils.isEmpty(fileName) || bitmap == null){
            return false;
        }
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outputStream);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            writer.close();
        } catch (IOException e) {
            if (e.getMessage() != null)
                Log.e(TAG, e.getMessage());
            else
                e.printStackTrace();
            return false;
        }
        return true;
    }

    public static String readFile(Context context, String fileName) {
        String ret = "";
        if (context == null || TextUtils.isEmpty(fileName)){
            return ret;
        }
        try {
            FileInputStream inputStream = context.openFileInput(fileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int size = inputStream.available();
                char[] buffer = new char[size];
                inputStreamReader.read(buffer);
                inputStream.close();
                ret = new String(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public static Bitmap readImage(Context context, String filename) {
        Bitmap bitmap = null;
        try {
            FileInputStream fileInputStream = context.openFileInput(filename);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static Uri getFileUri(Context context, String filename) {
        if (TextUtils.isEmpty(filename)){
            return null;
        }
        Uri uri = Uri.fromFile(context.getFilesDir());
        return Uri.withAppendedPath(uri, filename);
    }
}
