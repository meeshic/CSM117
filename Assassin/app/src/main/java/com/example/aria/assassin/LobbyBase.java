package com.example.aria.assassin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;

public abstract class LobbyBase extends AppCompatActivity {

    public static class LobbyInfoContainer {
        private String username;
        private boolean isGM;
        private ArrayList<String> playerList = new ArrayList<>();

        public LobbyInfoContainer(String currentUser) { this.username = currentUser; }

        public String getUsername() { return this.username; }
        public boolean isGM() { return this.isGM; }
        public ArrayList<String> getPlayerList() { return this.playerList; }

        public void setIsGM(boolean GM) { this.isGM = GM; }
        public void addPlayer(String player) { this.playerList.add(player); }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpView() {
        // Get the Intent that started this activity
        Intent intent = getIntent();

        // Get the current username
        String username = intent.getStringExtra(Launch.EXTRA_USERNAME);

        // Get list of players in game
        ArrayList<String> players = intent.getStringArrayListExtra(Launch.EXTRA_LOBBY_USERS);

        // Capture the username and set it at the top of the view
        TextView textView = (TextView) findViewById(R.id.usernameDisplay);
        textView.setText(username);

        // Display the users in the lobby
        final ListView playersListView = (ListView) findViewById(R.id.playerList);
        ArrayAdapter<String> playersAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        playersListView.setAdapter(playersAdapter);
    }

    public void exitLobby() {
        // Tell server player is leaving?
    }
}
