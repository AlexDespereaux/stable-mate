package com.jaram.jarambuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaram.jarambuild.Utils.NetworkUtils;

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
    private boolean validInput = false;
    private String prefName;
    private String prefPWord;

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
                //set variables & validate input
                validInput = getUserInput();
                //check internet status
                if (NetworkUtils.isNetworkConnected(this))
                {
                    login(validInput);
                } else
                {
                    //allow use of app and saving files to database until user logs in again
                    localLogin(v, validInput);
                }
                break;
            case R.id.goToSignUpBtn:
                //Go to signup activity
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login(boolean validInput)
    {
        if(validInput)
        {
            //TODO: login to server
            if (true)
            {
                //go to home menu
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
            } else
            {
                Toast.makeText(this, "Login Failed please try again", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }

    }

    private void localLogin(View v, boolean validInput)
    {
        //get stored variables
        getInputPrefs(v);
        if(validInput)
        {
            if ((prefName != null) && (prefPWord != null))
            {
                if ((name.equals(prefName)) && (pWord.equals(prefPWord)))
                {
                    Toast.makeText(this, "No internet, saving files locally", Toast.LENGTH_SHORT).show();
                    //go to home menu
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                } else
                {
                    Toast.makeText(this, "Login Failed please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else
        {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
        }
    }


    private void getInputPrefs(View v)
    {
        SharedPreferences jaramSharedP = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        prefName = jaramSharedP.getString("userNameLogin", null);
        prefPWord = jaramSharedP.getString("userPwordLogin", null);
    }

    private boolean getUserInput()
    {
        boolean validInput = false;
        //TODO add input validation
        //get data from login fields
        if(true)
        {
            name = nameField.getText().toString().trim();
            pWord = pWordField.getText().toString().trim();
            validInput = true;
        }
        return validInput;
    }
}
