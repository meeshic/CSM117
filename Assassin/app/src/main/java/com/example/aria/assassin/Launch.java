package com.example.aria.assassin;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.aria.assassin.RestClient.SyncRestClient;
import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class Launch extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "com.example.aria.assassin.USERNAME";
    public static final String EXTRA_LOBBY_USERS = "com.example.aria.assassin.LOBBY_USERS";

    private class ValContainer<T> {
        private T val;

        public ValContainer() {}

        public ValContainer(T v) {
            this.val = v;
        }

        public T getVal() {
            return val;
        }

        public void setVal(T val) {
            this.val = val;
        }
    }

    private enum Validity {
        VALID, NOT_UNIQUE, INVALID
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
    }

    /** Called when the user taps the Enter button for Username*/
    public void setUsername(View view) {
        // Get the username entered
        EditText usernameField = (EditText) findViewById(R.id.usernameField);
        String username = usernameField.getText().toString();

        // Check if username has valid characters and is unique
        Validity usernameValid = isValidUsername(username);

        // Make sure username is nonempty and valid
        if (!username.isEmpty() && (usernameValid == Validity.VALID)) {
            Intent intent;

            // Send post request with username
            joinGame(username);

            // Get users in lobby
            LobbyBase.LobbyInfoContainer lobbyInfo = getLobbyUsers(username);

            if (lobbyInfo.isGM()) {
                // If no lobby, then enter as GM
                intent = new Intent(this, LobbyGM.class);
            }
            else {
                // If lobby exists, then enter as reular
                intent = new Intent(this, LobbyRegular.class);
            }

            // Enter the lobby
            intent.putExtra(EXTRA_USERNAME, username);
            intent.putStringArrayListExtra(EXTRA_LOBBY_USERS, lobbyInfo.getPlayerList());
            startActivity(intent);
        }
        else {
            if (username.isEmpty()) {
                usernameField.setError("Username cannot be empty");
            } else if (usernameValid == Validity.INVALID) {
                usernameField.setError("Usernames can only contain the following characters: alphanumeric, ., -, _, ~");
            } else {
                usernameField.setError("This username is already taken");
            }
        }
    }

    // Usernames can only contain alphanumeric, ".", "-", "_", "~"
    // Usernames must be unique
    private Validity isValidUsername(final String username) {
        final ValContainer<Validity> usernameValidity = new ValContainer<>(Validity.INVALID);

        // Check for invalid characters
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\.\\-\\_~]");
        Matcher matcher = pattern.matcher(username);
        if (matcher.find()) {
            // Username has invalid characters
            return usernameValidity.getVal();
        }

        Thread validatenameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                params.put("username", username);
                SyncRestClient.get("game/validate", params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                        // Username is unique
                        usernameValidity.setVal(Validity.VALID);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseBody, Throwable e) {
                        // Username is not unique
                        usernameValidity.setVal(Validity.NOT_UNIQUE);
                    }
                });
            }
        });
        validatenameThread.start();
        try { validatenameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }

        return usernameValidity.getVal();
    }

    private void joinGame(final String username) {
        final Context context = this.getApplicationContext();

        Thread joinGameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put("username", username);
                    StringEntity data = new StringEntity(jsonParams.toString());
                    SyncRestClient.postJSON(context, "game/join", data, new BlackholeHttpResponseHandler());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        joinGameThread.start();
        try { joinGameThread.join(); } catch (InterruptedException e) { e.printStackTrace(); }
    }

    private LobbyBase.LobbyInfoContainer getLobbyUsers(final String currentPlayer) {
        final LobbyBase.LobbyInfoContainer lobbyInfo = new LobbyBase.LobbyInfoContainer(currentPlayer);

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

                                // Set GM status of current player
                                if (playername.equals(currentPlayer)) {
                                    lobbyInfo.setIsGM(isGM);
                                }

                                // Add star to end of username if isGM
                                if (isGM) {
                                    playername += " " + new String(Character.toChars(0x2B50));
                                }

                                // Add player to list
                                lobbyInfo.addPlayer(playername);
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

        return lobbyInfo;
    }
}
