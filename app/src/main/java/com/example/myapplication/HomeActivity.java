package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    Button getStarted;
    ImageView historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        historyButton = findViewById(R.id.historyButton);

        getStarted = findViewById(R.id.getStarted);



        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HistoryFragment homeFragment = new HistoryFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.home_fragment_container, homeFragment)
                        .commit();
            }
        });


        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        }
    }
