package com.example.aria.assassin;

import android.os.Bundle;
import android.content.Intent;
import android.view.View;

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

    public void startGame(View view) {
        // Tell server to start game
        Intent intent = new Intent(this, RegView.class);
//        String message = intent.getStringExtra(Launch.EXTRA_USERNAME);
//        intent.putExtra(Launch.EXTRA_USERNAME, message);
        startActivity(intent);
    }
}
