package com.example.aria.assassin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class LobbyGM extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_gm);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(Launch.EXTRA_USERNAME);

        // Capture the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.usernameDisplay);
        textView.setText(message);
    }
}
