package com.example.aria.assassin;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Lauren on 4/25/2017.
 */

public class Dashboard extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
    }

    public void setUpView(View view){
        final ListView listview1 = (ListView) findViewById(R.id.alivePlayerList);
        final ArrayList<String> alivePlayers = new ArrayList<String>(Arrays.asList(
                "PlayerAlive1",
                "PlayerAlive2",
                "PlayerAlive3"
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
}

