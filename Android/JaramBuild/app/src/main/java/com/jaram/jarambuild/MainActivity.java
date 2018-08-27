package com.jaram.jarambuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.User;
import com.jaram.jarambuild.utils.NetworkUtils;
import com.jaram.jarambuild.utils.TinyDB;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //(I leave variables as global until ready for release, and then convert to local variable)
    //Easier to debug

    //variables
    private EditText emailField;
    private EditText pWordField;
    private Button loginBtn;
    private Button goToSignUpBtn;

    private String email;
    private String pWord;
    private boolean validInput = false;

    //logging
    private String TAG = "MainAct";

    //shared Prefs
    TinyDB tinydb;

    //login status - if !null user is logged in
    private String loggedInAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //buttons
        loginBtn = findViewById(R.id.loginBtn);
        goToSignUpBtn = findViewById(R.id.goToSignUpBtn);

        //text fields
        emailField = findViewById(R.id.emailField);
        pWordField = findViewById(R.id.pWordField);

        //register listeners
        loginBtn.setOnClickListener(this);
        goToSignUpBtn.setOnClickListener(this);

        //shared prefs
        tinydb = new TinyDB(this);

        //check if logged in & skip to home screen
        loggedInCheck();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginBtn:
                //validate input
                validInput = getUserInput();
                login(validInput);
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
        if (validInput && checkAccount(email, pWord))
        {
            if (NetworkUtils.isNetworkConnected(this))
            {
                //TODO: login to server
            }
            //go to home menu
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else
        {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean getUserInput()
    {
        boolean validInput = false;
        //TODO add input validation
        //get data from login fields
        email = emailField.getText().toString().trim();
        pWord = pWordField.getText().toString().trim();
        if (pWord.equals("") || email.equals(""))
        {
            validInput = false;
            Log.d(TAG, "valid input - false");
        }
        else
        {
            validInput = true;
            Log.d(TAG, "valid input - true:" + " email:" + email + " pword:" + pWord);
        }
        return validInput;
    }

    private void loggedInCheck()
    {
        if (tinydb.getString("loggedInAccount") != "")
        {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    private boolean checkAccount(String email, String pWord)
    {
        User userToCheck = getOneUserFromDb(email);
        boolean checked = false;
        //Log.d(TAG, "checkAccount - User to check = " + userToCheck.getEmail() + " " + userToCheck.getPWord());
        if (userToCheck != null)
        {
            //Log.d(TAG, "email from field: " + email + "pword from field" + pWord);
            if (userToCheck.getEmail().equals(email)&& userToCheck.getPWord().equals(pWord))
            {
                Log.d(TAG, "here");

                checked = true;
                Log.d(TAG, "passed account check");
                //set prefs
                tinydb.putString("loggedInAccount", userToCheck.getEmail());
                tinydb.putString("loggedInName", userToCheck.getFirstName());
            }
        } else
        {
            Log.d(TAG, "failed account check");
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
        return checked;
    }

    private User getOneUserFromDb(String email)
    {
        User user = AppDatabase
                .getDatabase(this)
                .getUserDao()
                .getUserbyId(email);
        return user;
    }
}
