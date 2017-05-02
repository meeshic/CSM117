package com.example.aria.assassin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Launch extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "com.example.aria.assassin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    /** Called when the user taps the Enter button for Username*/
    public void setUsername(View view) {
        // Get the username entered
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        String username = usernameField.getText().toString();

        if (!username.isEmpty() && isValidUsername(username)) {
            Intent intent;
            // Check server if a lobby already exists
            boolean isGM = true;

            if (isGM) {
                // If no lobby, then enter as GM
                intent = new Intent(this, LobbyGM.class);
            }
            else {
                // If lobby exists, then enter as reular
                intent = new Intent(this, LobbyRegular.class);
            }

            // Enter the lobby
            intent.putExtra(EXTRA_USERNAME, username);
            startActivity(intent);
        }
        else {
            usernameField.setError("Usernames can only contain the following characters: alphanumeric, ., -, _, ~");
        }
    }

    // Usernames can only contain alphanumeric, ".", "-", "_", "~"
    private boolean isValidUsername(String username) {
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\.\\-\\_~]");
        Matcher matcher = pattern.matcher(username);
        return !matcher.find();
    }
}
