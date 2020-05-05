package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.DragEvent;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CreatememeActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        View.OnDragListener {

    private ImageView imageView;
    private FileManager fileManager = null;
    private final int GALLERY_REQUEST_CODE = 1; //used when opening gallery and picking image
    private EditText editText;
    private Spinner textSpinner;
    private Switch switchText;
    private ArrayList<String> selections = new ArrayList<String>(); //spinner selections
    private TextPaint paint; //text is customized using this
    private Meme meme = null;
    private Bitmap scaledImage = null;

    // 5 lines added for live preview
    private GestureDetector gestureDetector;
    private ConstraintLayout createMemeLayout;
    private static final String TAG = "CreateMemeActivity";
    private int location[]=new int[2];
    private int locationImage[]=new int[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creatememe);
        imageView = (ImageView) findViewById(R.id.imageView);
        editText = (EditText) findViewById(R.id.editText);
        meme = Meme.getInstance();
        paint = new TextPaint();
        meme.paintSettings(this, paint);
        switchText = (Switch) findViewById(R.id.switchText);

        switchText.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editText.setTextColor(Color.WHITE);
                    paint.setColor(Color.WHITE);
                }else{
                    editText.setTextColor(Color.BLACK);
                    paint.setColor(Color.BLACK);
                }

            }
        });

        // Added for live preview
        //**********//
        createMemeLayout = (ConstraintLayout) findViewById(R.id.createMemeLayout);

        editText.setOnTouchListener(this);

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {   //Here is defined that on-screen keyboard is opened when editText has focus.
                if(hasFocus){
                    InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);
                }else{
                    InputMethodManager imm = (InputMethodManager)getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                }
            }
        });
        gestureDetector = new GestureDetector(this,this);
        //*******//

        fileManager = new FileManager(this);
        textFromFiles();
        textSpinner = (Spinner) findViewById(R.id.textSpinner);
        textSpinnerFunctions();
    }

    public void imageFromGallery(View v) {
        fileManager.loadImage(); //loadImage activates onActivityResult
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                try {
                    Uri imageUri = data.getData();
                    InputStream imagestream = getContentResolver().openInputStream(imageUri);
                    Bitmap image = BitmapFactory.decodeStream(imagestream);
                    scaledImage = meme.scaleImage(image, this);
                    imageView.setImageBitmap(scaledImage); //sets the image on screen
                    startPreview();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void imageToGallery(View v) {
        if (scaledImage != null && editText.getText().length()>0) { //checks if image is picked and text is written
            String text = editText.getText().toString();

            float textX = editText.getX()-imageView.getX();     //Current coordinates of editText relative to imageView
            float textY = editText.getY()-imageView.getY();

            Bitmap memeMap = meme.createMeme(scaledImage, text, paint, textX, textY);
            fileManager.saveImage(memeMap);
            Toast.makeText(getApplicationContext(), "Image saved to 'Pictures'", Toast.LENGTH_SHORT).show();
        }
        else if (scaledImage == null){
            Toast.makeText(getApplicationContext(), "Pick image first!", Toast.LENGTH_SHORT).show();
        }
        else if (editText.getText().length()==0){
            Toast.makeText(getApplicationContext(), "Write something first!", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
        }
    }

    public void startPreview(){
        imageView.getLocationOnScreen(locationImage);// provides x- and y-coordinates of imageView. However, imageView is 0x0 since it is not updated yet
        editText.setX(locationImage[0]-editText.getWidth()/2f);     //Set editText to center of the picture
        editText.setY(locationImage[1]-editText.getHeight()/2f);    //Set editText to center of the picture
        editText.bringToFront();
        //
    }

    public void textToFiles(View v) {
        //lets get user type first
        UserManager userManager = UserManager.getInstance();
        User currentUser = userManager.getCurrentUser();
        int type = currentUser.getType();
        if (type != 0) { //if user is guest, this is not permitted
            String fileName = "texts.csv";
            if (editText.getText().length() > 0) {
                fileManager.writeText(editText, fileName); //writes text to csv file
                selections = fileManager.readText(fileName); //updates spinner
                Toast.makeText(getApplicationContext(), "Text saved", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Write something first!", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Log in first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void textFromFiles() {
        String fileName = "texts.csv";
        File file = this.getFileStreamPath(fileName);
        if (file.exists()) {
            selections = fileManager.readText(fileName); //adds texts to spinner
        }
        else {
            try {
                FileOutputStream fos = this.openFileOutput(fileName, this.MODE_PRIVATE); //creates csv file
                String firstLine = "Choose text\n";
                byte[] bytes = firstLine.getBytes();
                fos.write(bytes);
                fos.close();
                selections = fileManager.readText(fileName); //adds texts to spinner
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void textSpinnerFunctions() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, selections);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        textSpinner.setAdapter(adapter);
        textSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String textOfSpinner = String.valueOf(textSpinner.getSelectedItem());
                if (textOfSpinner.equals("Choose text") == false) { //checks if text is selected from spinner
                    editText.setText(textOfSpinner);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        //Log.d(TAG, "onSingleTapUp: called.");
        // User taps editText once, which sets focus and max width of editText is set to 90 % of imageView's width. This gives visually good results and works with createMeme method
        int maxWidth = Math.round(imageView.getWidth()*0.9f);
        if(maxWidth>editText.getWidth()){
            editText.setMaxWidth(maxWidth);
        }
        editText.requestFocus();
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //Log.d(TAG, "onLongPress: called.");

        //Build drag shadow and start drag
        View.DragShadowBuilder builder = new View.DragShadowBuilder(editText);
        editText.startDrag(null, builder,null,0);
        builder.getView().setOnDragListener(this);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch(event.getAction()) {

            case DragEvent.ACTION_DRAG_STARTED:
                //Log.d(TAG, "onDrag: drag started.");
                //Set original editText to top left corner so it cannot be accessed and therefore result to true drop.
                editText.setX(0-editText.getWidth());
                editText.setY(0-editText.getHeight());

                return true;

            case DragEvent.ACTION_DRAG_ENTERED:
                //Log.d(TAG, "onDrag: drag entered.");
                return true;

            case DragEvent.ACTION_DRAG_LOCATION:
                //Log.d(TAG, "onDrag: current point: ( " + event.getX() + " , " + event.getY() + " )");
                return true;

            case DragEvent.ACTION_DRAG_EXITED:
                //Log.d(TAG, "onDrag: exited.");
                return true;

            case DragEvent.ACTION_DROP:
                //Log.d(TAG, "onDrag: dropped. current point: (" + event.getX() + " , " + event.getY() + " )");
                return true;

            case DragEvent.ACTION_DRAG_ENDED:
                //Log.d(TAG, "onDrag: ended. current point: (" + event.getX() + " , " + event.getY() + " )");

                createMemeLayout.getLocationInWindow(location);     //Provides location of main Layout in window (Titlebar, notificationbar etc.)

                float currentX = event.getX() - editText.getWidth() / 2.0f-location[0];     //Drag ended, set editText's position where item is dropped
                float currentY = event.getY() - editText.getHeight() / 2.0f - location[1];

                // Allow dropping only inside imageView (x-coordinates)
                if (currentX < imageView.getX()) {      // If editText is too far left
                    Toast.makeText(getApplicationContext(), "Text must be inside of the picture!", Toast.LENGTH_SHORT).show();
                    editText.setX(imageView.getX());

                } else if ((currentX + editText.getWidth()) > (imageView.getX() + imageView.getWidth())){       // If editText is too far right
                    Toast.makeText(getApplicationContext(), "Text must be inside of the picture!", Toast.LENGTH_SHORT).show();
                    editText.setX(imageView.getX()+imageView.getWidth()-editText.getWidth());

                }else{      //EditText's x-coordinate is inside the picture
                    editText.setX(event.getX()-editText.getWidth()/2.0f-location[0]);
                }

                // Allow dropping only inside imageView (y-coordinates)
                if(currentY < imageView.getY()){        // If editText is too far up
                    Toast.makeText(getApplicationContext(), "Text must be inside of the picture!", Toast.LENGTH_SHORT).show();
                    editText.setY(imageView.getY());

                }else if((currentY+editText.getHeight()) > (imageView.getY()+imageView.getHeight())){       // If editText is too far down
                    Toast.makeText(getApplicationContext(), "Text must be inside of the picture!", Toast.LENGTH_SHORT).show();
                    editText.setY(imageView.getY()+imageView.getHeight()-editText.getHeight());

                }else{      //EditText's y-coordinate is inside the picture
                    editText.setY(event.getY()-editText.getHeight()/2.0f-location[1]);
                }

                return true;

            // An unknown action type was received.
            default:
                //Log.e(TAG,"Unknown action type received by OnStartDragListener.");
                break;

        }
        return false;
    }
}