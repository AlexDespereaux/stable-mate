package com.jaram.jarambuild;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

        //set toggle position
        getQSToggleStatus();

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

        //home button in action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null) {
            toolbar.setLogo(R.drawable.my_logo_shadow_96px);

            //Listener for item selection change
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goHome();
                }
            });
        }
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
            Log.d(TAG, "Help Btn Clicked");
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://docs.google.com/document/d/1CFCF-80XOzv55uB1acoBkKkKK8FgZzDq0q24luXXdzI/edit?usp=sharing"));
            startActivity(browserIntent);

        } else if (item.getItemId() == R.id.logoutMenuBtn)
        {
            Log.d(TAG, "Logout Btn Clicked");
            //set logged in user to null
            tinydb.putString("loggedInAccount", "");
            //return to Login Page
            Intent settingsIntent = new Intent(this, MainActivity.class);
            startActivity(settingsIntent);
            finish();
        }
        else if (item.getItemId() == R.id.aboutBtn)
        {
            Log.d(TAG, "About Btn Clicked");
            //Go to settings activity
            Intent settingsIntent = new Intent(this, AboutUsActivity.class);
            startActivity(settingsIntent);
        }
        else
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
                Toast.makeText(this, "Please restart the application to View Quickstart Guide", Toast.LENGTH_SHORT).show();

        }
    }

    public void getQSToggleStatus()
    {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean saveLocation = settings.getBoolean("locationToggleSwitch", false);
        locationToggleSwitch.setChecked(saveLocation);
        Log.d(TAG, "QS toggle set as :" + saveLocation);
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

    public void goHome()
    {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}