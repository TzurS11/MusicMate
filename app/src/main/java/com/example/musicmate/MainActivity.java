package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button login, signup;
    TextView welcome;
    ActionBar actionBar;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (sp.contains("userUid") && sp.contains("userEmail")) {
            Intent intent = new Intent(MainActivity.this, afterlogin.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();

        actionBar.hide();

        welcome = findViewById(R.id.WelcomeTv);
        String first = "Welcome to ";
        String next = "<font color='#ff5100'>MusicMate!</font>";
        welcome.setText(HtmlCompat.fromHtml(first + next, HtmlCompat.FROM_HTML_MODE_LEGACY));

        login = findViewById(R.id.loginBtn);
        login.setOnClickListener(this);

        signup = findViewById(R.id.signupBtn);
        signup.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v == login) {
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }
        if (v == signup) {
            Intent intent = new Intent(MainActivity.this, signup.class);
            startActivity(intent);
        }
    }
}