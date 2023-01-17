package com.example.musicmate;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public class signup extends AppCompatActivity {
    EditText email, password;
    Boolean[] textInEditText = {false, false};
    Button signupBtn;
    ActionBar actionBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        actionBar = getSupportActionBar();

        actionBar.hide();

        email = findViewById(R.id.emailAddress);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn2);
        setButtonEnabled(signupBtn,false);


        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) textInEditText[0] = false;
                if (s.length() > 0) textInEditText[0] = true;


                if (textInEditText[0] == true && textInEditText[1] == true) {
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
                if (s.length() == 0) textInEditText[1] = false;
                if (s.length() > 0) textInEditText[1] = true;


                if (textInEditText[0] == true && textInEditText[1] == true) {
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
        } else {
            btn.setClickable(true);
            btn.setAlpha(1f);
        }
    }
}