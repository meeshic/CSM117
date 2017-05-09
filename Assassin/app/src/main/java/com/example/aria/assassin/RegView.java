package com.example.aria.assassin;
import android.content.Intent;
import android.os.health.PackageHealthStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
/**
 * Created by Lauren on 4/25/2017.
 */

public class RegView extends AppCompatActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regview);
    }


//    public void testButton(View view){

//        Button btnExample = (Button) findViewById(R.id/button_Dashboard);
//        btnExample.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Do something here
//            }
//        });
//    }

    public void pressDash(View view)
    {
        Intent intent = new Intent(this, Dashboard.class);
        startActivity(intent);
    }

    public void showHint(View view)
    {
        //new view to display number?
    }

    public void onBackPressed()
    {
    }
}
