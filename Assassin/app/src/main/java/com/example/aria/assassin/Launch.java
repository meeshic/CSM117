package com.example.aria.assassin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.loopj.android.http.BlackholeHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class Launch extends AppCompatActivity {
    public static final String EXTRA_USERNAME = "com.example.aria.assassin";

    private class ValContainer<T> {
        private T val;

        public ValContainer() {
        }

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
        Validity usernameValid = isValidUsername(username);

        if (!username.isEmpty() && (usernameValid == Validity.VALID)) {
            Intent intent;
            // Check server if a lobby already exists
            boolean isGM = true;

            if (isGM) {
                // If no lobby, then enter as GM
                intent = new Intent(this, LobbyGM.class);
            }
            else {
                // If lobby exists, then enter as reular
                intent = new Intent(this, LobbyRegular.class);
            }

            // Enter the lobby
            intent.putExtra(EXTRA_USERNAME, username);
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
    private Validity isValidUsername(String username) {
        final ValContainer<Validity> usernameValidity = new ValContainer<>(Validity.INVALID);

        // Check for invalid characters
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9\\.\\-\\_~]");
        Matcher matcher = pattern.matcher(username);
        if (matcher.find()) {
            // Username has invalid characters
            return usernameValidity.getVal();
        }

//        RequestParams params = new RequestParams();
//        params.put("username", username);
//        SyncRestClient.get("game/verifyusername", params, new BlackholeHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                // Username is unique
//                usernameValidity.setVal(Validity.VALID);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable e) {
//                // Username is not unique
//                usernameValidity.setVal(Validity.NOT_UNIQUE);
//            }
//        });

        usernameValidity.setVal(Validity.VALID);
        return usernameValidity.getVal();
    }

    public void getLobbyUsers() throws JSONException {
        SyncRestClient.get("game/players", null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject players) {
                // Handle JSON of players
            }
        });
    }
}
