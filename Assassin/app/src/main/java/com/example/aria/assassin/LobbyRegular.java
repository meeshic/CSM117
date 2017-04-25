package com.example.aria.assassin;

import android.os.Bundle;

public class LobbyRegular extends LobbyBase {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby_regular);

        setUpView();
    }

    @Override
    public void onBackPressed() {
        exitLobby();
    }

    public void exitLobby() {
        // Tell server this player is leaving
    }
}
