package com.example.mememachine9000;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.EditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileManager extends Activity {

    private final int GALLERY_REQUEST_CODE = 1; //used when opening gallery and picking image
    private static FileManager instance = null;
    private Context context;
    public ArrayList<String> texts = new ArrayList<String>(); //texts that are saved are read here

    public FileManager(Context context1) {
        context = context1;
    }

    public void loadImage() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK); //pick image from gallery
            photoPickerIntent.setType("image/*");
            ((Activity)context).startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
        }
    }

    private void requestPermissions() {
        String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE}; //write permission includes reading
        ActivityCompat.requestPermissions((Activity)context, permissions, GALLERY_REQUEST_CODE);
    }

    public void saveImage(Bitmap image) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            requestPermissions();
        }
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            try {
                String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String fileName = "meme_" + time + ".jpg"; //filename is unique with date and time on it
                ContentResolver resolver = context.getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg");
                //next two lines make the saved image appear on top as new image in gallery
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                OutputStream os = resolver.openOutputStream(imageUri);
                image.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeText(EditText editText, String fileName) {
        String text = editText.getText().toString()+"\n";
        try {
            FileOutputStream fos = context.openFileOutput(fileName, MODE_APPEND | context.MODE_PRIVATE);
            byte[] bytes = text.getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readText(String fileName) {
        try {
            texts.clear(); //clears the ArrayList first, obligatory for RandomMemeActivity
            FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(isr);
            String line = reader.readLine();
            while (line != null) {
                texts.add(line); //add saved texts to arrayList
                line = reader.readLine();
            }
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return texts;
    }


}