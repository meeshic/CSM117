package com.example.aria.assassin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class LobbyBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpView() {
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(Launch.EXTRA_USERNAME);

        // Capture the username and set it at the top of the view
        TextView textView = (TextView) findViewById(R.id.usernameDisplay);
        textView.setText(message);

        // Display the users in the lobby
        final ListView listview = (ListView) findViewById(R.id.playerList);
        final ArrayList<String> players = new ArrayList<String>(Arrays.asList(
                "Player1",
                "Player2",
                "Player3",
                "Player2",
                "Player3",
                "Player2",
                "Player3",
                "Player2",
                "Player3",
                "Player2",
                "Player3"));
        ArrayAdapter<String> playersAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, players);
        listview.setAdapter(playersAdapter);
    }

    public abstract void exitLobby();
}
