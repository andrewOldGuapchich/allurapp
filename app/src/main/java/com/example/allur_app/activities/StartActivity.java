package com.example.allur_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.allur_app.activities.box.BoxInform;
import com.example.allur_app.R;
import com.example.allur_app.activities.box.BoxActivity;
import com.example.allur_app.activities.move.SelectMoveActivity;
import com.example.allur_app.activities.product.ProductActivity;

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

    public void onClickBox(View view){
        Intent intent = new Intent(this, BoxInform.class);
        startActivities(new Intent[]{intent});
    }

    public void onClickProduct(View view){
        Intent intent = new Intent(this, ProductActivity.class);
        startActivities(new Intent[]{intent});
    }

    public void onClickMove(View view){
        Intent intent = new Intent(this, SelectMoveActivity.class);
        startActivities(new Intent[]{intent});
    }
}