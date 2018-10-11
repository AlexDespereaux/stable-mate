package com.jaram.jarambuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jaram.jarambuild.utils.TinyDB;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener
{
    private Switch locationToggleSwitch;
    private String TAG = "SettingsAct";
    private Button qsBtn;
    //get logged in user
    TinyDB tinydb;
    String loggedInUser; // email address, which is primary key of user db

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        locationToggleSwitch = findViewById(R.id.locSwitch);
        qsBtn = findViewById(R.id.qsBtn);

        //register listeners
        qsBtn.setOnClickListener(this);
        locationToggleSwitch.setOnClickListener(new ToggleButton.OnClickListener()
        {

            public void onClick(View v)
            {
                SharedPreferences sharedPreferences = PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("locationToggleSwitch", locationToggleSwitch.isChecked());
                editor.apply();
                Log.d(TAG, "locationToggleSwitch: " + locationToggleSwitch.isChecked());
            }
        });

        //get logged in user for db
        tinydb = new TinyDB(this);
        loggedInUser = tinydb.getString("loggedInAccount");
        Log.d(TAG, "loggedInUser: " + loggedInUser);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.bar_menu, menu);
        return true;
    }

    //custom menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.helpMenuBtn)
        {
            //TODO Make help activity
            Toast.makeText(this, "Help Menu TBC", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Help Btn Clicked");

        } else if (item.getItemId() == R.id.logoutMenuBtn)
        {
            Log.d(TAG, "Logout Btn Clicked");
            //set logged in user to null
            tinydb.putString("loggedInAccount", "");
            //return to Login Page
            Intent settingsIntent = new Intent(this, MainActivity.class);
            startActivity(settingsIntent);
            finish();
        } else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.qsBtn:
                Log.d(TAG, "QS Btn clicked");
                resetQuickStart();
        }
    }

    public void resetQuickStart()
    {
        //reset the Quickstart views
        MaterialShowcaseView.resetAll(this);
        TinyDB tinydb = new TinyDB(this);
        //reset the auto scrolls
        tinydb.putDouble("viewQuickstartShown", 0.0);
        tinydb.putDouble("addDataQuickstartShown", 0.0);
        Log.d(TAG, "QS reset");
    }
}