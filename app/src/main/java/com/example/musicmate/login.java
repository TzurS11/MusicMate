package com.example.musicmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class login extends AppCompatActivity implements View.OnClickListener {
    EditText email, password;
    Boolean[] textInEditText = {false, false};
    Button loginBtn;
    ActionBar actionBar;
    SharedPreferences sp;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        actionBar = getSupportActionBar();

        actionBar.hide();

        email = findViewById(R.id.emailAddress2);
        password = findViewById(R.id.password2);
        loginBtn = findViewById(R.id.loginBtn2);
        setButtonEnabled(loginBtn, false);
        loginBtn.setOnClickListener(this);

        firebaseAuth = firebaseAuth.getInstance();




        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) textInEditText[0] = false;
                if (s.length() > 0) textInEditText[0] = true;


                if (textInEditText[0] && textInEditText[1]) {
                    setButtonEnabled(loginBtn, true);
                } else {
                    setButtonEnabled(loginBtn, false);
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


                if (textInEditText[0] && textInEditText[1] ) {
                    setButtonEnabled(loginBtn, true);
                } else {
                    setButtonEnabled(loginBtn, false);
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
            btn.setAlpha(0.5f);
        } else {
            btn.setClickable(true);
            btn.setAlpha(1f);
        }
    }


    @Override
    public void onClick(View view) {
        if(view == loginBtn){
            firebaseAuth.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(login.this, "Login was successful", Toast.LENGTH_SHORT).show();

                        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("userUid",task.getResult().getUser().getUid());
                        editor.putString("userEmail", task.getResult().getUser().getEmail());
                        editor.commit();

                        Intent intent = new Intent(login.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        Toast.makeText(login.this, "Failed to login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}