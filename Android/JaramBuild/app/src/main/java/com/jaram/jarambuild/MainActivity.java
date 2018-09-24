package com.jaram.jarambuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.User;
import com.jaram.jarambuild.utils.NetworkUtils;
import com.jaram.jarambuild.utils.TinyDB;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

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

    //validation
    AwesomeValidation awesomeValidation;

    //logging
    private String TAG = "MainAct";

    //shared Prefs
    TinyDB tinydb;

    //login status - if !null user is logged in
    private String loggedInAccount;
    private RequestQueue queue;

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

        //validation
        awesomeValidation = new AwesomeValidation(BASIC);
        AwesomeValidation.disableAutoFocusOnFirstFailure();
        awesomeValidation.addValidation(this, R.id.emailField, Patterns.EMAIL_ADDRESS, R.string.emailerror);
        String regexPassword = "(?=.*?[A-Z])(?=.*?[0-9]).{8,}";
        awesomeValidation.addValidation(this, R.id.pWordField, regexPassword, R.string.passworderror);

        //register listeners
        loginBtn.setOnClickListener(this);
        goToSignUpBtn.setOnClickListener(this);

        //shared prefs
        tinydb = new TinyDB(this);

        //check if logged in & skip to home screen
        loggedInCheck();

        //home button in action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null)
        {
            toolbar.setLogo(R.drawable.my_logo_shadow_96px);
        }
        //instantiate request queue
        queue = Volley.newRequestQueue(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.loginBtn:
                //validate input
                if (awesomeValidation.validate())
                {
                    getUserInput();
                    login();
                }
                break;
            case R.id.goToSignUpBtn:
                //Go to signup activity
                Intent intent = new Intent(this, SignUpActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void login()
    {
        if (checkAccount(email, pWord))
        {
            if (NetworkUtils.isNetworkConnected(this))
            {
                String url = "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/user";

                // Request a string response from the provided URL.
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                Log.d(TAG, "Response:" + response);
                                if(response.equalsIgnoreCase("Authenticated"))
                                {
                                    goHome();
                                    Log.d(TAG, "server check successful open Home activity");
                                }
                            }
                        }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.d(TAG, "Response:" + error);
                    }
                })
                {
                    //custom header for basic auth
                    @Override
                    public Map<String, String> getHeaders()
                    {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put(
                                "Authorization",
                                String.format("Basic %s", Base64.encodeToString(
                                        //String.format("%s:%s", email, password).getBytes(), Base64.DEFAULT)));  TODO: set to input credentials once user account set up is complete
                                        String.format("%s:%s", "marita", "fitz4321").getBytes(), Base64.DEFAULT)));
                        return params;
                    }
                };
                // Add the request to the RequestQueue.
                queue.add(stringRequest);
            }
        } else
        {
            Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserInput()
    {
        //get data from login fields
        email = emailField.getText().toString().trim();
        pWord = pWordField.getText().toString().trim();
    }

    private void loggedInCheck()
    {
        if (tinydb.getString("loggedInAccount") != "")
        {
            goHome();
        }
    }

    private void goHome()
    {
        //go to home menu
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private boolean checkAccount(String email, String pWord)
    {
        User userToCheck = getOneUserFromDb(email);
        boolean checked = false;
        //Log.d(TAG, "checkAccount - User to check = " + userToCheck.getEmail() + " " + userToCheck.getPWord());
        if (userToCheck != null)
        {
            //Log.d(TAG, "email from field: " + email + "pword from field" + pWord);
            if (userToCheck.getEmail().equals(email) && userToCheck.getPWord().equals(pWord))
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
