package com.example.mememachine9000;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.util.Random;


// Used for displaying two factor authentication dialog to user on login

public class AuthenticationDialog  extends DialogFragment {

    // Interface
    public interface InputListener {
        void sendInput(boolean input);
    }
    public InputListener inputListener;


    private EditText userInput = null;
    private TextView btnOk, btnCancel, codeView;
    private String authenticationCode = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_authentication, container, false);
        userInput = view.findViewById(R.id.userInput);
        btnCancel = view.findViewById(R.id.cancel);
        btnOk = view.findViewById(R.id.ok);
        codeView = view.findViewById(R.id.codeView);

        authenticationCode = generateAuthenticationCode();
        codeView.setText(authenticationCode);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if user entered right code
                if(userInput.getText().toString().equals(authenticationCode)){
                    inputListener.sendInput(true);
                }
                else{
                    inputListener.sendInput(false);
                }
                getDialog().dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        return view;
    }


    // Generate code of 6 random digits
    private String generateAuthenticationCode(){
        String newCode = "";
        Random rand = new Random();

        for(int i = 0; i < 6; i++){
            newCode = newCode.concat(Integer.toString(rand.nextInt(10)));
        }

        return  newCode;
    }


    // Attach interface
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try{
            inputListener = (InputListener) getActivity();
        }
        catch (ClassCastException e){
            System.out.println(e.getMessage());
        }
    }


}
