package com.example.aria.assassin;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.aria.assassin.RestClient.SyncRestClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import cz.msebera.android.httpclient.entity.StringEntity;

public class Dashboard extends AppCompatActivity{

    private String username;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        username = getIntent().getStringExtra(Launch.EXTRA_USERNAME);
        setUpView();
    }

    public void setUpView(){
        final ListView listview1 = (ListView) findViewById(R.id.alivePlayerList);
        final ArrayList<String> alivePlayers = new ArrayList<String>(Arrays.asList(
                "PlayerAlive1",
                "PlayerAlive2",
                "PlayerAlive3",
                "PlayerAlive4",
                "PlayerAlive5",
                "PlayerAlive6"
                ));
        ArrayAdapter<String> playersAdapter1 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, alivePlayers);
        listview1.setAdapter(playersAdapter1);

        final ListView listview2 = (ListView) findViewById(R.id.deadPlayerList);
        final ArrayList<String> deadPlayers = new ArrayList<String>(Arrays.asList(
                "PlayerDead1",
                "PlayerDead2",
                "PlayerDead3"
        ));
        ArrayAdapter<String> playersAdapter2 =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deadPlayers);
        listview2.setAdapter(playersAdapter2);
    }

    public void pressDash(View view) {
        Intent intent = new Intent(this, RegView.class);
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

