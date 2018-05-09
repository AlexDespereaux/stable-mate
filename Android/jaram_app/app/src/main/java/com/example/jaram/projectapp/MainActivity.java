package com.example.jaram.projectapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jaram.projectapp.Utils.NetworkUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //(I leave variables as global until ready for release, and then convert to local variable)
    //Easier to debug

    //variables
    private EditText nameField;
    private EditText pWordField;
    private Button loginBtn;
    private Button goToSignUpBtn;

    private String name;
    private String pWord;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttons
        loginBtn = findViewById(R.id.loginBtn);
        goToSignUpBtn = findViewById(R.id.goToSignUpBtn);

        //text fields
        nameField = findViewById(R.id.nameField);
        pWordField = findViewById(R.id.pWordField);

        //register listeners
        loginBtn.setOnClickListener(this);
        goToSignUpBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginBtn:
                //get data from login fields
                name = nameField.getText().toString().trim();
                pWord = pWordField.getText().toString().trim();

                //check internet status
                if (NetworkUtils.isNetworkConnected(this))
                {
                    login();
                } else
                {
                    Toast.makeText(this, "No internet, saving files locally", Toast.LENGTH_SHORT).show();
                    //allow use of app and saving files to database until user logs in again
                    localLogin(v);
                }
                break;
            case R.id.goToSignUpBtn:
                //Go to signup activity
                Intent intent = new Intent(this, SignUp.class);
                startActivity(intent);
                break;
        }
    }

    private void login()
    {
        //TODO: login to server
        if (true)
        {
            //go to home menu
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        } else
        {
            Toast.makeText(this, "Login Failed please try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void localLogin(View view)
    {
        //get stored variables
        String prefName = getPrefName(view);
        String prefPWord = getPrefPword(view);

        if ((prefName != null) && (prefPWord != null))
        {
            if ((name.equals(prefName)) && (pWord.equals(prefPWord)))
            {
                //go to home menu
                Intent intent = new Intent(this, Home.class);
                startActivity(intent);
            } else
            {
                Toast.makeText(this, "Login Failed please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO implement login security
    private String getPrefName(View v)
    {
        SharedPreferences jaramSharedP = getPreferences(MODE_PRIVATE);
        return jaramSharedP.getString("userNameLogin", name);
    }

    private String getPrefPword(View v)
    {
        SharedPreferences jaramSharedP = getPreferences(MODE_PRIVATE);
        return jaramSharedP.getString("userNameLogin", name);
    }
}
