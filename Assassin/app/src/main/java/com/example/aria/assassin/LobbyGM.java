package com.example.aria.assassin;

import android.content.Context;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

import com.example.aria.assassin.RestClient.SyncRestClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;

public class LobbyGM extends LobbyBase {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_gm);

        setUpView();
    }


    public void startGame(View view) {
        playerListHandler.removeCallbacks(checkPlayerListRunnable);
        // Tell server to start game
        Intent intent = new Intent(this, RegView.class);
        intent.putExtra(Launch.EXTRA_USERNAME, username);

        Thread startGameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SyncRestClient.post("game/start", null, new BlackholeHttpResponseHandler());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        startGameThread.start();
        try { startGameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
        startActivity(intent);
    }
}
