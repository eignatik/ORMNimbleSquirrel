package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.Date;

/**
 * Created by eignatik on 28/05/15.
 */
public class Registr extends Activity implements View.OnClickListener{
    private TextView butCanc; //button for cancel

    private EditText loginField;
    private EditText passwordField;
    private EditText descField;

    private EditText dateBornField;
    private Date dateBorn;

    private RadioButton maleCheck, femaleCheck;

    private Button saveProfile;

    private App app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registr);

        //find views by ID
        butCanc = (TextView) findViewById(R.id.cancBut);
        loginField = (EditText) findViewById(R.id.loginField);
        passwordField = (EditText) findViewById(R.id.passField);
        descField = (EditText) findViewById(R.id.descField);
        dateBornField = (EditText) findViewById(R.id.dateBorn);
        maleCheck = (RadioButton) findViewById(R.id.male);
        femaleCheck = (RadioButton) findViewById(R.id.female);
        saveProfile = (Button) findViewById(R.id.saveBut);

        app = (App)getApplicationContext();

        //set listeners
        butCanc.setOnClickListener(this);
        saveProfile.setOnClickListener(this);
        dateBornField.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancBut: {
                super.onBackPressed();
                break;
                }
            case R.id.dateBorn: {
                break; //DATA PICKER
            }
            case R.id.saveBut: {
                //user data write
                //femaleCheck.isChecked();
                app.setIsLoggened(true);
                Intent intent = new Intent(this, BlogsActivity.class);
                startActivity(intent);
                break;
            }
        }
    }
}
