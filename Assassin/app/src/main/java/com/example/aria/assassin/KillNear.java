package com.example.aria.assassin;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Lauren on 4/25/2017.
 */

public class KillNear extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_near);
    }

    public void pressDash(View view)
    {
        Intent intent = new Intent(this, RegView.class);
        startActivity(intent);
    }

    public void onBackPressed()
    {
    }
}
