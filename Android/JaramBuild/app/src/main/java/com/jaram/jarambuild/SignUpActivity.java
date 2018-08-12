package com.jaram.jarambuild;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.User;
import com.jaram.jarambuild.roomDb.UserDao;
import com.jaram.jarambuild.roomDb.UserListViewModel;
import com.jaram.jarambuild.utils.NetworkUtils;

import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    //I leave as local variables until development is basically finished.
    //Simply as its easier to debug
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText pWordField;
    private EditText emailField;
    private Button signUpBtn;

    private String firstName;
    private String lastName;
    private String pWord;
    private String email;
    private boolean userExists = true;

    boolean accountCreated = false;

    //set log name
    private String TAG = "SignUp";

    //connection
    boolean isConnected = false;

    //context
    Context context;

    //db
    private UserListViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //buttons
        signUpBtn = findViewById(R.id.signUpBtn);

        //text fields
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        pWordField = findViewById(R.id.pWordField);
        emailField = findViewById(R.id.emailField);

        //register listeners
        signUpBtn.setOnClickListener(this);

        //db
        userViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signUpBtn:
                if (NetworkUtils.isNetworkConnected(this))
                {
                    signUp();
                } else
                {
                    Toast.makeText(this, "Check internet connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void signUp()
    {
        firstName = firstNameField.getText().toString().trim();
        lastName = lastNameField.getText().toString().trim();
        pWord = pWordField.getText().toString().trim();
        email = emailField.getText().toString().trim();

        if (firstName.equals("") || lastName.equals("") || pWord.equals("") || email.equals(""))
        {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
        } else
        {
            //TODO Submit to server set accountCreated to true upon success

            //check user does not already exist
            getOneUserFromDb(email);
            if(!userExists)
            {
                context = getApplicationContext();
                saveAccountToDb(firstName, lastName, pWord, email, context);
                accountCreated = true;
                Log.d(TAG, "Account created");
            }
            else
            {
                Log.d(TAG, "Account already exists");
                Toast.makeText(this, "Account already exists", Toast.LENGTH_SHORT).show();
            }
        }
        if (accountCreated)
        {
            Toast.makeText(this, "Account Creation Sucessful", Toast.LENGTH_SHORT).show();
            //Return back to sign in to login
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else
        {
            Log.d(TAG, "Account creation failed");
        }

    }

    private void saveAccountToDb(String firstName, String lastName, String pWord, String email, Context context)
    {

        userViewModel.addOneUser(new User(email, firstName, lastName, pWord));
        Log.d(TAG, "User saved to dataBase");

        //for debugging
        getAllUsersFromDb();
    }

    //for debug only
    private void getAllUsersFromDb()
    {

        userViewModel.getUserList().observe(this, new Observer<List<User>>()
        {
            @Override
            public void onChanged(@Nullable List<User> users)
            {
                for (User user : users)
                {
                    Log.d(TAG, "in loop" + user.toString());
                }
            }
        });
    }

    private void getOneUserFromDb(String email)
    {
        User test = AppDatabase
                .getDatabase(this)
                .getUserDao()
                .getUserbyId(email);
        if(test != null)
        {
            userExists = true;
            Log.d(TAG, "User exists " + test.toString());
        }
        else{
            userExists = false;
            Log.d(TAG, "user does not exist");
        }
    }

    @Override
    protected void onDestroy()
    {
        AppDatabase.destroyInstance();
        super.onDestroy();
    }
}