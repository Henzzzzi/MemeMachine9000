package com.example.mememachine9000;

import android.content.Context;
import android.widget.Toast;

import java.io.File;

public class Reset {

    private static Reset instance = null;
    private Context context;

    public Reset(Context maincontext) { //this class gets context from MainActivity
        context = maincontext;
    }

    public void resetTexts() {
        String fileName = "texts.csv";
        deleteCSV(fileName);
        Toast.makeText(context.getApplicationContext(), "Texts reseted", Toast.LENGTH_SHORT).show();
    }

    public void resetRandomTexts() {
        String fileName = "random texts.csv";
        deleteCSV(fileName);
        Toast.makeText(context.getApplicationContext(), "Random texts reseted", Toast.LENGTH_SHORT).show();
    }

    public void resetRandomImages() {
        String dir = "random images";
        File file = context.getFileStreamPath(dir);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (int i=0; i<files.length; i++) {
                files[i].delete(); //delete files one by one
            }
        }
        file.delete(); //delete empty folder
        String fileName = "image names.csv";
        deleteCSV(fileName);
        Toast.makeText(context.getApplicationContext(), "Random images reseted", Toast.LENGTH_SHORT).show();
    }

    private void deleteCSV(String fileName) {
        File file = context.getFileStreamPath(fileName);
        if (file.exists()) {
            file.delete();
        }
    }

    public static Reset getInstance(Context context) { //singleton
        if (instance == null) {
            instance = new Reset(context);
        }
        return instance;
    }

}
