package com.example.mememachine9000;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


// Activity used when user changes own password

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEditText, newPasswordEditText, newPasswordAgainEditText;
    private TextView oldPasswordStatus, passwordRequirements, newPasswordAgainStatus;
    private PasswordValidator passwordValidator = null;
    private UserManager userManager = null;
    private UserDatabaseHelper udpHelper = null;
    private PasswordHasher passwordHasher = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        oldPasswordEditText = findViewById(R.id.editTextOldPassword);
        newPasswordEditText = findViewById(R.id.editTextPassword);
        newPasswordAgainEditText = findViewById(R.id.editTextPasswordAgain);

        oldPasswordStatus = findViewById(R.id.oldPasswordStatus);
        passwordRequirements = findViewById(R.id.passwordRequirements);
        newPasswordAgainStatus = findViewById(R.id.passwordStatus);

        passwordValidator = PasswordValidator.getInstance();
        userManager = UserManager.getInstance();
        udpHelper = new UserDatabaseHelper(this);
        passwordHasher = PasswordHasher.getInstance();

        String initialPasswordValidationMessage = passwordValidator.validate("");
        passwordRequirements.setText(initialPasswordValidationMessage);
        passwordRequirements.setTextColor(Color.RED);


        // Displays status messages 'passwords match' or 'passwords don't match'
        // Displays status messages of new password's content
        newPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!newPasswordEditText.getText().toString().equals(newPasswordAgainEditText.getText().toString())){
                    newPasswordAgainStatus.setText("Passwords do not match!");
                    newPasswordAgainStatus.setTextColor(Color.RED);
                }
                else{
                    newPasswordAgainStatus.setText("Passwords match!");
                    newPasswordAgainStatus.setTextColor(Color.GREEN);
                }

                String passwordValidationMessage = passwordValidator.validate(newPasswordEditText.getText().toString());
                passwordRequirements.setText(passwordValidationMessage);
                if(passwordValidator.isValid(newPasswordEditText.getText().toString())){
                    passwordRequirements.setTextColor(Color.GREEN);
                }
                else{
                    passwordRequirements.setTextColor(Color.RED);
                }

            }
        });

        // Updates 'passwords match' or 'passwords don't match' when 'password again' is  edited
        newPasswordAgainEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!newPasswordEditText.getText().toString().equals(newPasswordAgainStatus.getText().toString())){
                    newPasswordAgainStatus.setText("Passwords do not match!");
                    newPasswordAgainStatus.setTextColor(Color.RED);
                }
                else{
                    newPasswordAgainStatus.setText("");
                }
            }
        });

    }


    // Called when user clicks 'change password' button
    // Checks if password meets all requirements and is not equal to old password
    public void changePassword(View v){
        int userId = userManager.getCurrentUser().getId();
        User currentUserFromDatabase = udpHelper.findUserById(userId);

        String passwordFromDatabase = currentUserFromDatabase.getPassword();
        byte[] saltFromDatabase = currentUserFromDatabase.getSalt();

        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        String newPasswordAgain = newPasswordAgainEditText.getText().toString();

        // Check that new password fills requirements
        if(passwordValidator.isValid(newPassword)){
            // Check that new password and new password again are equal
            if(newPassword.equals(newPasswordAgain)){
                // Check that hashed old and new password are not equal
                if(!passwordHasher.hash(newPassword, saltFromDatabase).equals(passwordFromDatabase)){
                    // Check old password
                    if(passwordHasher.hash(oldPassword, saltFromDatabase).equals(passwordFromDatabase)){

                        // Change password
                        boolean passwordChanged = userManager.changePassword(userId, passwordHasher.hash(newPassword, saltFromDatabase), udpHelper);
                        // Refresh current user
                        userManager.setCurrentUser(udpHelper.findUserById(userManager.getCurrentUser().getId()));

                        if(passwordChanged){
                            Toast.makeText(ChangePasswordActivity.this, "Password changed successfully", Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else {
                            Toast.makeText(ChangePasswordActivity.this, "Changing password failed!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        Toast.makeText(ChangePasswordActivity.this, "Check your old password!", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(ChangePasswordActivity.this, "New password can't be same than old password!", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(ChangePasswordActivity.this, "Passwords do not match!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            Toast.makeText(ChangePasswordActivity.this, "Please check all password requirements!", Toast.LENGTH_LONG).show();
        }
    }


}
