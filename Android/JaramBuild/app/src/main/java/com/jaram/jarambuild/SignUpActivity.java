package com.jaram.jarambuild;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.User;
import com.jaram.jarambuild.roomDb.UserDao;
import com.jaram.jarambuild.roomDb.UserListViewModel;
import com.jaram.jarambuild.utils.NetworkUtils;

import java.util.List;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    //this class is unused as backend was not ready in time. However I have left it in as it would be useful if developing further..
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText pWordField;
    private EditText pWordFieldconfirm;
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

    //validation
    AwesomeValidation awesomeValidation;

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
        pWordFieldconfirm = findViewById(R.id.pWordFieldconfirm);
        emailField = findViewById(R.id.emailField);

        //register listeners
        signUpBtn.setOnClickListener(this);

        //db
        userViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);

        //validation
        awesomeValidation = new AwesomeValidation(BASIC);
        awesomeValidation.addValidation(this, R.id.firstNameField, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.lastNameField, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.emailField, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        String regexPassword = "(?=.*?[A-Z])(?=.*?[0-9]).{8,}";
        awesomeValidation.addValidation(this, R.id.pWordField, regexPassword, R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.pWordFieldconfirm, R.id.pWordField, R.string.passworderrorconfirm);

        //home button in action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null) {
            toolbar.setLogo(R.drawable.my_logo_shadow_96px);

            //Listener for item selection change
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goSignIn();
                }
            });
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.signUpBtn:
                if (NetworkUtils.isNetworkConnected(this))
                {
                    if(awesomeValidation.validate())
                    {
                        signUp();
                    }
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

    //to check if user exists
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

    public void goSignIn()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}