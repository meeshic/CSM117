package com.csm117.astar.assassin;

import android.content.Intent;
import android.os.Bundle;

import com.csm117.astar.assassin.RestClient.AsyncRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.os.Handler;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LobbyRegular extends LobbyBase {

    private final int CHECK_GAME_STATUS_INTERVAL = 2000;

    private final Handler checkGameHandler = new Handler();
    private final Runnable checkGameStatusRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                AsyncRestClient.get("game", null, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            String status = response.getString("status");

                            if (status.equals("InProgress")) {
                                // Go to next screen.
                                checkGameHandler.removeCallbacks(checkGameStatusRunnable);
                                Intent intent = new Intent(LobbyRegular.this, RegView.class);
                                intent.putExtra(Launch.EXTRA_USERNAME, username);
                                startActivity(intent);
                            }
                            // Still waiting for game to start
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        Toast.makeText(getApplicationContext(), "Lobby was closed. Please enter again.",
                                Toast.LENGTH_LONG).show();
                        checkGameHandler.removeCallbacks(checkGameStatusRunnable);
                        Intent intent = new Intent(LobbyRegular.this, Launch.class);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            checkGameHandler.postDelayed(checkGameStatusRunnable, CHECK_GAME_STATUS_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_regular);

        setUpView();
        scheduleCheckGameStatus();
    }

    public void scheduleCheckGameStatus() {
        checkGameHandler.post(checkGameStatusRunnable);
    }

    @Override
    public void exitLobby() {
        checkGameHandler.removeCallbacks(checkGameStatusRunnable);
        super.exitLobby();
    }
}