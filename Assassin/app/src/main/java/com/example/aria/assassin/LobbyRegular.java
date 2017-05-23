package com.example.aria.assassin;

import android.content.Intent;
import android.os.Bundle;

import com.example.aria.assassin.RestClient.SyncRestClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

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


    //make continuous GET requests to Lobby to see if game in progresss


    public boolean attemptJoinGame(){
        RequestParams params = new RequestParams();
        SyncRestClient.get("/game",params, new JsonHttpResponseHandler()
        {
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                // Username is unique
                for(int i = 0; i < response.length(); i++)
                {
                    try {
                        JSONObject gameInfo = response.getJSONObject(i);
                        String gameStatus = gameInfo.getString("Status");
                        if (gameStatus.equals("InProgress"))
                        {
//                            Intent intent = new Intent(this, RegView.class);
//                            startActivity(intent);
                        }
                        else
                        {

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable e) {
                Log.d("FAIL", responseString);
            }
            });

        return false;

    }

}