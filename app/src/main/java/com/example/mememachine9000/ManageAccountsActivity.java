package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;


// Used for managing accounts in database by admin user

public class ManageAccountsActivity extends AppCompatActivity {

    private Spinner userSpinner = null;
    private EditText editUsername, editPassword;
    private Switch isAdmin;
    private TextView uid = null;

    private UserManager userManager = null;
    private UserDatabaseHelper udbHelper = null;
    private ArrayList <User> users = null;
    private ArrayList <String> userInfo = null;
    private PasswordHasher passwordHasher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);

        userSpinner = findViewById(R.id.spinnerAccounts);
        editUsername = findViewById(R.id.editText);
        editPassword = findViewById(R.id.newPassword);
        isAdmin = findViewById(R.id.isAdmin);
        uid = findViewById(R.id.textView6);

        userManager = UserManager.getInstance();
        udbHelper = new UserDatabaseHelper(this);
        passwordHasher = PasswordHasher.getInstance();

        // Get lists of users
        reloadUserLists();

        // Updates 'UID', 'username' and 'isAmin' fields when new user is selected
        userSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Skip default item
                if(userSpinner.getSelectedItemPosition() != 0){

                    editUsername.setText(users.get(userSpinner.getSelectedItemPosition() - 1).getUsername());
                    uid.setText("UID: " + users.get(userSpinner.getSelectedItemPosition() - 1).getId());

                    // isAdmin ?
                    if(users.get(userSpinner.getSelectedItemPosition() - 1).getType() == 2){
                        isAdmin.setChecked(true);
                    }
                    else {
                        isAdmin.setChecked(false);
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    // Save changes made to selected user. Called when save changes button is clicked
    public void saveChanges(View v){
        Boolean saveSucceeded = null;

        int userId = users.get(userSpinner.getSelectedItemPosition() - 1).getId();
        String newUsername = editUsername.getText().toString();
        String newPassword = editPassword.getText().toString();
        int newUserType = 1;        // Type = normal user by default

        // Checks for empty entries and sets them to null so they won't be updated
        if(newUsername.equals("")){
            newUsername = null;
        }
        if(newPassword.equals("")){
            newPassword = null;
        }
        // Hashes new password with selected users salt
        else{
            byte[] userSalt = users.get(userSpinner.getSelectedItemPosition() - 1).getSalt();
            newPassword = passwordHasher.hash(newPassword, userSalt);
        }

        if(isAdmin.isChecked()){
            newUserType = 2;        // Admin
        }

        // Edit user in database
        saveSucceeded = udbHelper.editUser(userId, newUsername, newPassword, newUserType);

        String message = "";
        if(saveSucceeded){
            message = "Successfully saved changes to user {" + newUsername + "} to database!";
        }
        else {
            message = "Failed to save changes!";
        }
        Toast.makeText(ManageAccountsActivity.this, message, Toast.LENGTH_LONG).show();

        reloadUserLists();
    }


    // Delete current user. Called when delete account button is clicked
    public void deleteUser(View v){
        Boolean deleteSucceeded = null;

        int userId = users.get(userSpinner.getSelectedItemPosition() - 1).getId();

        // Delete user from database
        deleteSucceeded = udbHelper.deleteUser(userId);

        String message = "";
        if(deleteSucceeded){
            message = "Successfully removed user with id {" + userId + "} from database!";
        }
        else {
            message = "Failed to save user!";
        }
        Toast.makeText(ManageAccountsActivity.this, message, Toast.LENGTH_LONG).show();

        reloadUserLists();
    }


    // Reloads ArrayLists, sets ArrayAdapter for spinner and clears TextViews and EditTexts
    // Called in onCreate and when changes to user are saved / user is deleted
    private void reloadUserLists(){
        users = udbHelper.getUsers();
        userInfo = userManager.generateUserInfo(users);

        ArrayAdapter <String> userAdapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, userInfo);
        userSpinner.setAdapter(userAdapter);

        uid.setText("UID:");
        editUsername.setText("");
        editPassword.setText("");
    }


}
