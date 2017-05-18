package com.example.aria.assassin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aria.assassin.RestClient.SyncRestClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.entity.StringEntity;

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

    protected String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpView() {
        // Get the Intent that started this activity
        Intent intent = getIntent();

        // Get the current username
        username = intent.getStringExtra(Launch.EXTRA_USERNAME);

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

    @Override
    public void onBackPressed() {
        exitLobby();
        super.onBackPressed();
        Intent intent = new Intent(LobbyBase.this, Launch.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    public void exitLobby() {
        final Context context = this.getApplicationContext();
        // Tell server player is leaving
        Thread exitLobbyThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("username", username);
                    StringEntity data = new StringEntity(jsonParams.toString());
                    SyncRestClient.postJSON(context, "game/leave", data, new BlackholeHttpResponseHandler());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        exitLobbyThread.start();
        try { exitLobbyThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
