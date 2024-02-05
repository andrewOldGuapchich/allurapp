package com.example.allur_app.activities.move;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.allur_app.R;
import com.example.allur_app.activities.StartActivity;
import com.example.allur_app.activities.product.ProductActivity;

public class SelectMoveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_move);
    }

    public void boxOnBoxClick(View view){
        Intent intent = new Intent(this, MoveActivityBoxOnBox.class);
        startActivities(new Intent[]{intent});
    }
}