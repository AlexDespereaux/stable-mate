package com.jaram.jarambuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import com.jaram.jarambuild.Utils.NetworkUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener
{
    //I leave as local variables until development is basically finished.
    //Simply as its easier to debug
    private EditText nameField;
    private EditText pWordField;
    private EditText emailField;
    private Button signUpBtn;
    private Switch saveLoginSwitch;

    private String name;
    private String pWord;
    private String email;
    private boolean saveLogin = false;

    //connection
    boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //buttons
        signUpBtn = findViewById(R.id.signUpBtn);

        //text fields
        nameField = findViewById(R.id.nameField);
        pWordField = findViewById(R.id.pWordField);
        emailField = findViewById(R.id.emailField);

        //switch booleans
        saveLoginSwitch = findViewById(R.id.saveLoginSwitch);

        //register listeners
        signUpBtn.setOnClickListener(this);
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
        //TODO: add Google keystore
        name = nameField.getText().toString().trim();
        pWord = pWordField.getText().toString().trim();
        email = emailField.getText().toString().trim();
        boolean accountCreated = false;

        //check input TODO: Add network check
        if (name.equals("") || pWord.equals("") || email.equals(""))
        {
            Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show();
        } else
            //TODO Submit to server set accountCreated to true upon success
            accountCreated = true;
        {
            if (accountCreated)
            {
                Toast.makeText(this, "Account Creation Sucessful", Toast.LENGTH_SHORT).show();
                //check status of switch
                saveLogin = saveLoginSwitch.isChecked();

                if(saveLogin)
                {
                    //save login to shared preferances
                    //ensuring user will be able to use app without wifi
                    saveLoginPref(name, pWord, email);
                }

                //Return back to sign in to login
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            } else
            {
                Toast.makeText(this, "Account Creation Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveLoginPref(String name, String pWord, String email)
    {
        //TODO exchange for secure storage
        //save details to shared preferances
        SharedPreferences jaramSharedP = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = jaramSharedP.edit();
        editor.putString("userNameLogin", name);
        editor.putString("userPwordLogin", pWord);
        editor.putString("userEmailLogin", email);
        editor.apply();

        Toast.makeText(this, "Login Saved" + name + pWord + email, Toast.LENGTH_LONG).show();
    }
}