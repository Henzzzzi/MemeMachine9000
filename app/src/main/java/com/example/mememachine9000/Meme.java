package com.example.mememachine9000;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;

import androidx.appcompat.app.AppCompatActivity;

public class Meme extends AppCompatActivity {

    private TextPaint paint = new TextPaint(); //text is customized using this
    private static Meme instance = null;
    private int scaledWidth = 0;
    private int scaledHeight = 0;
    private int spSize=30;      //Hardcoded text size, same as used in createMemeActivity ediText-widget

    public Bitmap createMeme(Bitmap scaledImage, String text, TextPaint paint, float textX, float textY) {
        int width = scaledImage.getWidth();
        int height = scaledImage.getHeight();
        Bitmap memeMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //the meme is constructed here
        Canvas combo = new Canvas(memeMap); //lets use canvas to create the meme
        combo.drawBitmap(scaledImage, 0, 0, null);
        int textWidth = (int) (width*0.9); //max width of text on meme is 90% of image's width
        //lets create StaticLayout for drawing text on meme
        StaticLayout textLayout = new StaticLayout(text, paint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        combo.save();
        combo.translate(textX, textY);
        textLayout.draw(combo);
        combo.restore();
        return memeMap;
    }

    public Bitmap createRandomMeme(Bitmap scaledImage, String text, TextPaint paint) {
        int width = scaledImage.getWidth();
        int height = scaledImage.getHeight();
        Bitmap memeMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888); //the meme is constructed here
        Canvas combo = new Canvas(memeMap); //lets use canvas to create the meme
        combo.drawBitmap(scaledImage, 0, 0, null);
        int textWidth = (int) (width*0.9); //max width of text on meme is 90% of image's width
        //lets create StaticLayout for drawing text on meme
        StaticLayout textLayout = new StaticLayout(text, paint, textWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
        //Every random meme sets text to same point. These values are found to be good by trial-error.
        float textX = scaledImage.getWidth()*0.05f;
        float textY = scaledImage.getHeight()/30;
        combo.save();
        combo.translate(textX, textY);
        textLayout.draw(combo);
        combo.restore();
        return memeMap;
    }

    public void paintSettings(Context context, TextPaint paint) { //settings for TextPaint
        paint.setColor(Color.BLACK);
        float scaledSizeInPixels = spSize*context.getResources().getDisplayMetrics().scaledDensity; //This line adjusts text size in canvas to match live preview
        paint.setTextSize(scaledSizeInPixels);
        AssetManager manager = context.getAssets();
        paint.setTypeface(Typeface.createFromAsset(manager, "leaguegothicregular.otf"));
    }

    public Bitmap scaleImage(Bitmap image, Context context) { //every image needs to be scaled for safe saving and opening in ImageView
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;
        int unscaledWidth = image.getWidth();
        int unscaledHeight = image.getHeight();
        if (unscaledWidth > unscaledHeight) {
            scaledWidth = displayWidth;
            scaledHeight = (unscaledHeight * scaledWidth) / unscaledWidth;
        } else if (unscaledWidth < unscaledHeight) {
            scaledHeight = (int) (displayWidth*0.8);
            scaledWidth = (unscaledWidth * scaledHeight) / unscaledHeight;
        } else if (unscaledWidth == unscaledHeight) {
            scaledWidth = displayWidth;
            scaledHeight = scaledWidth;
        }


        Bitmap scaledImage = Bitmap.createScaledBitmap(image, scaledWidth, scaledHeight, false);
        return scaledImage;
    }

    public static Meme getInstance() { //singleton
        if (instance == null) {
            instance = new Meme();
        }
        return instance;
    }

}
