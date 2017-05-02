package com.example.aria.assassin;

import android.os.Bundle;

public class LobbyGM extends LobbyBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_gm);

        setUpView();
    }

    @Override
    public void onBackPressed() {
        exitLobby();
    }

    public void startGame() {
        // Tell server to start game

    }
}
