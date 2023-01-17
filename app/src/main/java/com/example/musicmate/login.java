package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class login extends AppCompatActivity {
    EditText email, password;
    Boolean[] textInEditText = {false, false};
    Button loginBtn;
    ActionBar actionBar;

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


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) textInEditText[0] = false;
                if (s.length() > 0) textInEditText[0] = true;


                if (textInEditText[0] == true && textInEditText[1] == true) {
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
                if (s.length() == 0) textInEditText[1] = false;
                if (s.length() > 0) textInEditText[1] = true;


                if (textInEditText[0] == true && textInEditText[1] == true) {
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
}