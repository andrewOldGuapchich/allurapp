package com.example.allur_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.allur_app.R;

// TODO: 19.12.2023 disable scanner
public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void onClick(View view){
        Intent intent = new Intent(this, BoxActivity.class);
        startActivities(new Intent[]{intent});
    }
}