package com.example.appchat.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.appchat.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordDialog extends Dialog {
    TextInputEditText edtCurrentPass,edtNewPass,edtConfirmPass;
    Button btnChangePass,btnCancel;
    FirebaseUser firebaseUser;
    public ChangePasswordDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.diaglog_change_pass);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        mapping();
        btnChangePass.setOnClickListener(v -> {
            changePass();
        });
        btnCancel.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void changePass(){
        String currentPass = edtCurrentPass.getText().toString().trim();
        String newPass = edtNewPass.getText().toString().trim();
        String confirmPass = edtConfirmPass.getText().toString().trim();

        if (currentPass.isEmpty()){
            edtCurrentPass.setError("Password must not empty");
            edtCurrentPass.requestFocus();
            return;
        }
        if (newPass.length()<6){
            edtCurrentPass.setError("Password length >6");
            edtCurrentPass.requestFocus();
            return;
        }
        if (!confirmPass.equals(newPass)){
            edtCurrentPass.setError("confirm password is not same as new password");
            edtCurrentPass.requestFocus();
            return;
        }

        AuthCredential authCredential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),currentPass);
        firebaseUser.reauthenticate(authCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                firebaseUser.updatePassword(newPass).addOnCompleteListener(task1->{
                    if (task1.isSuccessful()){
                        Toast.makeText(getContext(), "Change password success", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getContext(), "Change password unsuccess", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(getContext(), "Current password is uncorrect", Toast.LENGTH_SHORT).show();
            }
        });
//        firebaseUser.updatePassword("Hihi");
    }
    private void mapping() {
        edtCurrentPass = findViewById(R.id.edtCurrentPass);
        edtNewPass = findViewById(R.id.edtNewPass);
        edtConfirmPass = findViewById(R.id.edtConfirmPass);
        btnChangePass = findViewById(R.id.btnChangePass);
        btnCancel = findViewById(R.id.btnCancel);

    }

}
