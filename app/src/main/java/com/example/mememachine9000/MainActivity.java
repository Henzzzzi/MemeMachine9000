package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private ImageButton buttonSettings;
    private Button buttonCreateMeme, buttonRandomMeme;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonSettings=(ImageButton) findViewById(R.id.buttonSettings);
        buttonCreateMeme = (Button) findViewById(R.id.buttonCreateMeme);
        buttonRandomMeme = (Button) findViewById(R.id.buttonRandomMeme);

    }

    public void createMeme(View v){
        Intent intent = new Intent(this, CreatememeActivity.class);
        startActivity(intent);
    }

    public void randomMeme(View V){
        Intent intent = new Intent(this, RandomMemeActivity.class);
        startActivity(intent);
    }

    public void openSettings(View v){
        Integer userType = -1; //0-Guest, 1-User, 2-Admin
        UserManager userManager = UserManager.getInstance();
        userType =userManager.getCurrentUser().getType();

        if(userType==0){
            Intent intent = new Intent(this, SettingsGuestActivity.class);
            startActivity(intent);
        } else if(userType==1){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else if(userType==2){
            Intent intent = new Intent(this, SettingsAdminActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Error identifying user type, please login again.", Toast.LENGTH_LONG).show();
        }

    }

}