package com.jaram.jarambuild;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.ColorRes;
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
import com.jaram.jarambuild.roomDb.UserListViewModel;
import com.jaram.jarambuild.utils.NetworkUtils;
import com.jaram.jarambuild.utils.TinyDB;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    //(I leave variables as global until ready for release, and then convert to local variable)
    //Easier to debug

    //variables
    private EditText emailField;
    private EditText pWordField;
    private Button loginBtn;

    private String email;
    private String pWord;
    private boolean validInput = false;
    private boolean userExists = false;

    //validation
    AwesomeValidation awesomeValidation;

    //logging
    private String TAG = "MainActDebug";

    //shared Prefs
    TinyDB tinydb;
    Double showQuickstart;

    //db
    private UserListViewModel userViewModel;
    private AppDatabase db;

    //login status - if !null user is logged in
    private String loggedInAccount;
    private RequestQueue queue;

    //quickstart
    private static final String SHOWCASE_ID = "main_act_login";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //db
        userViewModel = ViewModelProviders.of(this).get(UserListViewModel.class);
        db = AppDatabase.getDatabase(getApplicationContext());

        //buttons
        loginBtn = findViewById(R.id.loginBtn);

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
                //if (awesomeValidation.validate())// TODO:put validation back once accounts set up
                //if (true)
                //{
                getUserInput();
                login();
                //}
                break;
        }
    }

    private void login()
    {
        if (NetworkUtils.isNetworkConnected(this)) //if internet is available, check use details from cloud
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
                            if (response.contains("admin") || response.contains("user") || response.contains("student")) //if user account passes cloud db check
                            {
                                if (!doesUserExistInDb(email)) //check user is in local database
                                {
                                    Log.d(TAG, "adding user to local db");
                                    saveAccountToDb(pWord, email, getApplicationContext()); // add user to localDb
                                }
                                //set logged in user prefs
                                tinydb.putString("loggedInAccount", email);
                                tinydb.putString("loggedInName", pWord);
                                goHome(); //as user is in local db
                                Log.d(TAG, "server check successful open Home activity");
                            }
                        }
                    }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Log.d(TAG, "Response:" + error);
                    Log.d(TAG, "failed cloud account check");
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
                                    String.format("%s:%s", email, pWord).getBytes(), Base64.DEFAULT)));
                    return params;
                }
            };
            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        } else // check if user is in local db and password is correct as internet is not available
        {
            checkAccount(email, pWord);
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
        String loginState = tinydb.getString("loggedInAccount");
        if (loginState == "" || loginState == "loggedOut" || loginState == null)
        {
            Log.d(TAG, "Not logged in: " + loginState);
        } else
        {
            Log.d(TAG, "logged in: " + loginState + "go to home");
            goHome();
        }
    }

    //to check if user exists
    private boolean doesUserExistInDb(String email)
    {
        User test = AppDatabase
                .getDatabase(this)
                .getUserDao()
                .getUserbyId(email);
        if (test != null)
        {
            Log.d(TAG, "User exists " + test.toString());
            return true;
        } else
        {
            Log.d(TAG, "user does not exist");
            return false;
        }
    }

    //get users details from local db
    private User getOneUserFromDb(String email)
    {
        User test = AppDatabase
                .getDatabase(this)
                .getUserDao()
                .getUserbyId(email);
        return test;
    }

    private void saveAccountToDb(String pWord, String email, Context context)
    {
        //as user creation backend code was not ready in time we are using logins created in the backend.. sigh..
        userViewModel.addOneUser(new User(email, "Demo", "User", pWord));
        Log.d(TAG, "User saved to dataBase");
    }

    private void goHome()
    {
        //go to home menu
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private void checkAccount(String email, String pWord)
    {
        User userToCheck = getOneUserFromDb(email);
        if (userToCheck != null)
        {
            if (userToCheck.getEmail().equals(email) && userToCheck.getPWord().equals(pWord)) // if input user data matches user data from db
            {
                Log.d(TAG, "passed local account check");
                //set logged in user
                tinydb.putString("loggedInAccount", email);
                tinydb.putString("loggedInUserPWord", pWord);
                goHome(); // progress to home menu
            }
        } else
        {
            Log.d(TAG, "failed local account check");
            Toast.makeText(this, "Account Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed()
    {
        clearUserInput();
    }

    @Override
    public void onStop()
    {
        super.onStop();
        clearUserInput();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        clearUserInput();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        clearUserInput();
    }

    public void clearUserInput()
    {
        email = "";
        pWord = "";
    }
}
