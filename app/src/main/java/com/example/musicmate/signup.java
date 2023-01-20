package com.example.musicmate;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity implements View.OnClickListener {
    EditText email, password;
    Boolean[] textInEditText = {false, false};
    Button signupBtn;
    ActionBar actionBar;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        actionBar = getSupportActionBar();

        actionBar.hide();

        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn2);
        setButtonEnabled(signupBtn, false);
        signupBtn.setOnClickListener(this);

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


                if (textInEditText[0] && textInEditText[1] ) {
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
            btn.setAlpha(0.5f);
            btn.setEnabled(false);
        } else {
            btn.setClickable(true);
            btn.setAlpha(1f);
            btn.setEnabled(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == signupBtn) {
            firebaseAuth.createUserWithEmailAndPassword(
                    email.getText().toString(),
                    password.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d("TAG", "createUserWithEmail:success");
                        Toast.makeText(signup.this, "Successfully registered uid: "+task.getResult().getUser().getUid(), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(signup.this,MainActivity.class);
//                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(signup.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}