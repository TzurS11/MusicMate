package com.example.musicmate;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class afterlogin extends AppCompatActivity implements View.OnClickListener {
    ActionBar actionBar;
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_afterlogin);

        actionBar = getSupportActionBar();
        actionBar.hide();


        logout = findViewById(R.id.logoutBtn);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == logout){
            deleteSharedPreferences("userInfo");
            Intent intent = new Intent(afterlogin.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}