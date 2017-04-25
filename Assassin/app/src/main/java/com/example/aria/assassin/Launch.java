package com.example.aria.assassin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class Launch extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "com.example.aria.assassin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    /** Called when the user taps the Enter button for Username*/
    public void setUsername(View view) {
        Intent intent = new Intent(this, LobbyGM.class);
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        String username = usernameField.getText().toString();
        intent.putExtra(EXTRA_USERNAME, username);
        startActivity(intent);
    }
}
