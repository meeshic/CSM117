package com.example.aria.assassin;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.aria.assassin.RestClient.AsyncRestClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.entity.StringEntity;

public class GameOver extends AppCompatActivity{

    public static final String EXTRA_STATUS = "com.example.aria.assassin.STATUS";
    public static final int DEAD = 0;
    public static final int WINNER = 1;

    private int playerStatus = 0;
    private String username;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        // Get the Intent that started this activity
        Intent intent = getIntent();

        username = intent.getStringExtra(Launch.EXTRA_USERNAME);

        // Get the player status. Winner or dead?
        playerStatus = intent.getIntExtra(GameOver.EXTRA_STATUS, GameOver.DEAD);
        TextView statusText = (TextView) findViewById(R.id.statusText);

        if (playerStatus == GameOver.DEAD) {
            statusText.setText("You died.");
        } else {
            statusText.setText("You won!");
        }
    }

    public void playAgain(View view) {
        if (playerStatus == GameOver.WINNER) {
            // Send game end request
            try {
                JSONObject jsonParams = new JSONObject();
                jsonParams.put("username", username);
                StringEntity data = new StringEntity(jsonParams.toString());
                AsyncRestClient.postJSON(this.getApplicationContext(), "game/end", data, new BlackholeHttpResponseHandler());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Return to launch screen
        Intent nextIntent = new Intent(GameOver.this, Launch.class);
        startActivity(nextIntent);
    }

}
