package com.example.jaram.projectapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity implements View.OnClickListener
{
    private EditText nameField;
    private EditText pWordField;
    private EditText emailField;
    private EditText bDayField;
    private Button signUp;

    private String name;
    private String pWord;
    private String email;
    private String bDay;

    //connection
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //buttons
        signUp = findViewById(R.id.signUpBtn);

        //text fields
        nameField = findViewById(R.id.nameField);
        pWordField = findViewById(R.id.pWordField);
        emailField = findViewById(R.id.emailField);
        bDayField = findViewById(R.id.bDayField);

        //register listeners
        signUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signUpBtn:
                if(NetworkUtils.isNetworkConnected(this))
                {
                    signUp();
                }
                else
                {
                    Toast.makeText(this, "Check internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void signUp()
    {
        //TODO: add Google keystore
        name = nameField.getText().toString().trim();
        pWord = pWordField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        bDay = bDayField.getText().toString().trim();

        boolean accountCreated = false;
        //check input TODO: Add network check
        if (name.equals("") || pWord.equals("") || email.equals("") || bDay.equals(""))
        {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
        }
        else
        {
            //TODO Submit to server set accountCreated to true upon success
            if (accountCreated == true)
            {
                Toast.makeText(this, "Account Creation Sucessful", Toast.LENGTH_SHORT).show();
                //Return back to sign in to login
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else
            {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
