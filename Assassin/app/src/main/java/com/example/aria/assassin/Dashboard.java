package com.example.aria.assassin;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.aria.assassin.RestClient.SyncRestClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Dashboard extends AppCompatActivity{

    private String username;
    private ArrayList<String> alivePlayers = new ArrayList<>();
    private ArrayList<String> deadPlayers = new ArrayList<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        username = getIntent().getStringExtra(Launch.EXTRA_USERNAME);
        getPlayers();
        setUpView();
    }

    public void setUpView(){
        final ListView alivePlayersView = (ListView) findViewById(R.id.alivePlayerList);
        ArrayAdapter<String> playersAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, alivePlayers);
        alivePlayersView.setAdapter(playersAdapter1);

        final ListView deadPlayersView = (ListView) findViewById(R.id.deadPlayerList);
        ArrayAdapter<String> playersAdapter2 = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deadPlayers);
        deadPlayersView.setAdapter(playersAdapter2);
    }

    private void getPlayers() {
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
                                boolean isAlive = player.getBoolean("alive");

                                // Add star to end of username if isGM
                                if (isGM) {
                                    playername += " " + new String(Character.toChars(0x2B50));
                                }

                                // Add player to list
                                if (isAlive) {
                                    alivePlayers.add(playername);
                                } else {
                                    deadPlayers.add(playername);
                                }
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            pressDash();
            return true;
        }
        return false;
    }

    public void pressDash() {
        Intent intent = new Intent(this, RegView.class);
        intent.putExtra(Launch.EXTRA_USERNAME, username);
        startActivity(intent);
    }

    public void onBackPressed() {
        // Don't let them press back
    }

    public void onExitPressed(View view) {
        quitGame();
        Intent intent = new Intent(this, Launch.class);
        intent.putExtra(Launch.EXTRA_USERNAME, username);
        startActivity(intent);
        finish();
    }

    private void quitGame() {
        final Context context = this.getApplicationContext();
        // Tell server player is leaving
        Thread quitGameThread = new Thread(new Runnable() {
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
        quitGameThread.start();
        try { quitGameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}
