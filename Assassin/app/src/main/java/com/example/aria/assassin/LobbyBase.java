package com.example.aria.assassin;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.aria.assassin.RestClient.AsyncRestClient;
import com.example.aria.assassin.RestClient.SyncRestClient;
import com.google.android.gms.common.api.Result;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public abstract class LobbyBase extends AppCompatActivity {

    private final int UPDATE_PLAYER_LIST_INTERVAL = 2000;

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

    private class QueryPlayerList extends AsyncTask<String, Void, LobbyInfoContainer> {
        @Override
        protected LobbyInfoContainer doInBackground(String... username) {
            final LobbyBase.LobbyInfoContainer lobbyInfo = new LobbyBase.LobbyInfoContainer(username[0]);
            Thread playernameThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    RequestParams params = new RequestParams();
                    SyncRestClient.get("game/players", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject player = response.getJSONObject(i);
                                    String playername = player.getString("username");
                                    boolean isGM = player.getString("role").equals("GameMaster");

                                    // Add star to end of username if isGM
                                    if (isGM) {
                                        playername += " " + new String(Character.toChars(0x2B50));
                                    }

                                    // Add player to list
                                    lobbyInfo.addPlayer(playername);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            Log.d("FAIL", responseString);
                        }
                    });
                }
            });
            playernameThread.start();
            try { playernameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }

            return lobbyInfo;
        }

        @Override
        protected void onPostExecute(LobbyInfoContainer result) {
            displayPlayers(result.getPlayerList());
        }
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
        displayPlayers(players);

        // Schedule update
        scheduleUpdatePlayerList();
    }

    public void displayPlayers(ArrayList<String> players) {
        final ListView playersListView = (ListView) findViewById(R.id.playerList);
        ArrayAdapter<String> playersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, players);
        playersListView.setAdapter(playersAdapter);
    }

    public void scheduleUpdatePlayerList() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask updatePlayerListTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            QueryPlayerList query = new QueryPlayerList();
                            query.execute(username);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(updatePlayerListTask, 0, UPDATE_PLAYER_LIST_INTERVAL); //execute in every 2000 ms
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
