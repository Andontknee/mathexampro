package com.example.mathexampro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button normalModeButton = findViewById(R.id.normal_mode_button);
        Button timedModeButton = findViewById(R.id.timed_mode_button);

        normalModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            startActivity(intent);
        });

        timedModeButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainMenuActivity.this, TimedModeActivity.class);
            startActivity(intent);
        });
    }
}