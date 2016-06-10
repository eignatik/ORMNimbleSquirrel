package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends Activity implements View.OnClickListener {

    private TextView tvText1;
    private Button submitButton;
    private EditText firstSubmit;
    private App app;
    private Button regBut;
    private Button guestBut;
    //DBHelper dbh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        app = (App) getApplicationContext();

        //find view elements
        tvText1 = (TextView) findViewById(R.id.tvText1);
        submitButton = (Button) findViewById(R.id.submitButton);
        firstSubmit = (EditText) findViewById(R.id.firstSubmit);
        regBut = (Button) findViewById(R.id.regBut);
        guestBut = (Button) findViewById(R.id.guestBut);

        submitButton.setOnClickListener(this);
        firstSubmit.setOnClickListener(this);
        guestBut.setOnClickListener(this);
        regBut.setOnClickListener(this);

        //dbh = new DBHelper(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstSubmit: {
                firstSubmit.setText("");
                break;
            }
            case R.id.submitButton: {
                if(firstSubmit.getText().toString().isEmpty())
                    Toast.makeText(this, "Not entered, please again", Toast.LENGTH_LONG).show();
                else{
                    Toast.makeText(this, "You have sign in like a" + firstSubmit.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(this, BlogsActivity. class);
                    startActivity(intent);
                    app.setIsLoggened(true);
                }
                break;
            }
            case R.id.regBut: {
                Intent intent = new Intent(this, Registr. class);
                startActivity(intent);
                break;
            }
            case R.id.guestBut: {
                Intent intent = new Intent(this, BlogsActivity. class);
                startActivity(intent);
                app.setIsLoggened(false);
                break;
            }
        }
    }


    @Override
    public void onResume(){
        super.onResume();
    }
}
