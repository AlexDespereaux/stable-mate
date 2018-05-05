package com.example.jaram.projectapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //variables
    private EditText nameField;
    private EditText pWordField;
    private Button loginBtn;

    private String name;
    private String pWord;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttons
        loginBtn = findViewById(R.id.loginBtn);

        //text fields
        nameField = findViewById(R.id.nameField);
        pWordField = findViewById(R.id.pWordField);

        //register listeners
        loginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginBtn:
                if(NetworkUtils.isNetworkConnected(this))
                {
                    login();
                }
                else
                {
                    Toast.makeText(this, "No internet saving files locally", Toast.LENGTH_SHORT).show();
                    localLogin();
                }
                break;
        }

    }

    private void login()
    {
        //TODO: login to server
    }

    private void localLogin()
    {
        //TODO: Setup google keystore
    }
}
