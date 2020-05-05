package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

public class RandomMemeActivity extends AppCompatActivity {

    private ImageView imageView;
    private EditText editText;
    private FileManager fileManager = null;
    private ArrayList<String> drawables = new ArrayList<String>(); //names of images in assets
    private ArrayList<String> texts = new ArrayList<String>(); //ArrayList with updated texts
    private int imagesLength = 5; //Keeps track of how many images are in the pool. Initial value is the same than number of images in drawables.
    private Bitmap memeMap = null; //actual meme
    private TextPaint paint; //text is customized using this
    private Meme meme;
    private File root; //holds the path of default directory for saved files
    private final int GALLERY_REQUEST_CODE = 1; //used when opening gallery and picking image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_random_meme);
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
        meme = Meme.getInstance();
        paint = new TextPaint();
        meme.paintSettings(this, paint);
        fileManager = new FileManager(this);
        textsFromAssets();
        textFromFiles();
        drawablesToFiles();
        setMeme();
    }

    private void setMeme() {
        int randomNumber1 = getRandomNumberForImages();
        String textsName = "random texts.csv"; //lets get root folder for saved files with already created csv file
        File textsFile = this.getFileStreamPath(textsName);
        root = new File(textsFile.getParent());
        String path = root + "/random images/image"+randomNumber1+".jpg"; //path of random image
        Bitmap image = BitmapFactory.decodeFile(path);
        int randomNumber2 = getRandomNumber(texts);
        String text = texts.get(randomNumber2); //get random text
        memeMap = meme.createRandomMeme(image, text, paint);
        imageView.setImageBitmap(memeMap);
    }

    public void newRandomMeme(View v) {
        setMeme();
    }

    public void imageToGallery(View v) {
        if (memeMap != null) {
            fileManager.saveImage(memeMap);
            Toast.makeText(getApplicationContext(), "Image saved to 'Pictures'", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    private void drawablesToFiles() {
        String imageName;
        String fileName = "image names.csv";
        for (int i=0; i<imagesLength; i++) {
            imageName = "image"+i;
            drawables.add(imageName); //drawable names to ArrayList
        }
        File file = this.getFileStreamPath(fileName);
        if (!file.exists()) {
            for (int i=0; i<drawables.size(); i++) {
                try {
                    FileOutputStream fos = this.openFileOutput(fileName, MODE_APPEND | this.MODE_PRIVATE);
                    String text = drawables.get(i);
                    byte[] bytes = (text+"\n").getBytes();
                    fos.write(bytes); //writes Strings from ArrayList to csv file
                    fos.close();
                    int id = this.getResources().getIdentifier(text, "drawable", this.getPackageName());
                    Bitmap image = BitmapFactory.decodeResource(getResources(), id); //image from drawable
                    Bitmap scaledDrawable = meme.scaleImage(image, this);
                    imageToFiles(scaledDrawable, text); //adds drawable to random images folder
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        ArrayList<String> imageNames = fileManager.readText(fileName);
        imagesLength = imageNames.size();
    }

    public void addImageToPool(View v) {
        //lets get user type first
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();
        int type = currentUser.getType();
        if (type != 0) { //if user is guest, this is not permitted
            fileManager.loadImage(); //activates onActivityResult
            Toast.makeText(getApplicationContext(), "Image added to pool", Toast.LENGTH_SHORT).show();
            imagesLength++;
        }
        else {
            Toast.makeText(getApplicationContext(), "Log in first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void imageToFiles(Bitmap image, String imageName) {
        try {
            String textsName = "random texts.csv"; //lets get root folder for saved files with already created csv file
            File textsFile = this.getFileStreamPath(textsName);
            root = new File(textsFile.getParent());
            String path = root + "/random images";
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String fileName = imageName+".jpg";
            File file = new File(path, fileName);
            OutputStream os = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String updateImageNames() { //adds a line to "image names.csv" and returns the name of newest image
        String fileName = "image names.csv";
        ArrayList<String> imageNames = fileManager.readText(fileName);
        for (int i=0; i<imageNames.size(); i++) {
            System.out.println(imageNames.get(i));
        }
        String imageName = "image"+imageNames.size();
        try {
            FileOutputStream fos = this.openFileOutput(fileName, MODE_APPEND | this.MODE_PRIVATE);
            byte[] bytes = (imageName+"\n").getBytes();
            fos.write(bytes);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                try {
                    Uri imageUri = data.getData();
                    InputStream imagestream = getContentResolver().openInputStream(imageUri);
                    Bitmap selectedImage = BitmapFactory.decodeStream(imagestream);
                    Bitmap scaledSelectedImage = meme.scaleImage(selectedImage, this);
                    String imageName = updateImageNames();
                    imageToFiles(scaledSelectedImage, imageName);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private int getRandomNumber(ArrayList<String> texts) {
        int randomNumber;
        randomNumber = (int) (Math.random()*(texts.size())); //returns a number between 0 and size of ArrayList
        return randomNumber;
    }

    private int getRandomNumberForImages() {
        int randomNumber;
        randomNumber = (int) (Math.random()*(imagesLength));
        return randomNumber;
    }

    private void textsFromAssets() {
        String fileName = "textsforrandom.csv";
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line = reader.readLine();
            while (line != null) {
                texts.add(line); //add saved texts to arrayList
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void textFromFiles() {
        String fileName = "random texts.csv";
        File file = this.getFileStreamPath(fileName);
        if (file.exists()) { //checks if the file exists
            ArrayList<String> csvTexts = fileManager.readText(fileName);
            for (int i=0; i<csvTexts.size(); i++) {
                texts.add(csvTexts.get(i)); //updates texts
            }
        }
        else {
            try {
                FileOutputStream fos = this.openFileOutput(fileName, this.MODE_PRIVATE); //creates csv file
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addTextsToPool(View v) {
        //lets get user type first
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();
        int type = currentUser.getType();
        if (type != 0) { //if user is guest, this is not permitted
            String fileName = "random texts.csv";
            if (editText.getText().length() > 0) {
                fileManager.writeText(editText, fileName);
                texts.add(editText.getText().toString());
                Toast.makeText(getApplicationContext(), "Text added to pool", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Write something first!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Log in first!", Toast.LENGTH_SHORT).show();
        }
    }

}
