package com.example.musicmate;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class signup extends AppCompatActivity implements View.OnClickListener {
    EditText email, password;
    Boolean[] textInEditText = {false, false};
    Button signupBtn, backBtn;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn2);
        setButtonEnabled(signupBtn, false);
        signupBtn.setOnClickListener(this);

        backBtn = findViewById(R.id.signupBackBtn);
        backBtn.setOnClickListener(this);


        firebaseAuth = FirebaseAuth.getInstance();


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) textInEditText[0] = false;
                if (s.length() > 0) textInEditText[0] = true;


                if (textInEditText[0] && textInEditText[1] && patternMatches(s.toString(), "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
                    setButtonEnabled(signupBtn, true);
                } else {
                    setButtonEnabled(signupBtn, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() < 8) textInEditText[1] = false;
                if (s.length() >= 8) textInEditText[1] = true;


                if (textInEditText[0] && textInEditText[1] && patternMatches(email.getText().toString(), "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")) {
                    setButtonEnabled(signupBtn, true);
                } else {
                    setButtonEnabled(signupBtn, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void setButtonEnabled(Button btn, Boolean enable) {
        if (enable == false) {
            btn.setClickable(false);
            btn.setAlpha(1f);
            btn.setEnabled(false);
        } else {
            btn.setClickable(true);
            btn.setAlpha(1f);
            btn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == backBtn) {
            Intent intent = new Intent(signup.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        if (v == signupBtn) {
            showDialog();
            return;
        }
    }

    public boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    private void showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Create account");
        builder.setMessage("Are you sure?\nYou can't change email and password later");
        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        firebaseAuth.createUserWithEmailAndPassword(
                                email.getText().toString(),
                                password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    Toast.makeText(signup.this, "Successfully registered", Toast.LENGTH_SHORT).show();
                                    finish();
                                    return;
                                } else {
                                    Toast.makeText(signup.this, "Registration failed, please try again.", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    return;
                                }
                            }
                        });
                    }

                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                return;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}