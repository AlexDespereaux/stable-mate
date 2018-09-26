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

import com.jaram.jarambuild.utils.TinyDB;

public class SettingsActivity extends AppCompatActivity
    {
        private EditText compInputTxt;
        private Switch locationSwitch;
        private Button applyCompBtn;
        private SharedPreferences settings;
        private String TAG = "SettingsAct";
        //get logged in user
        TinyDB tinydb;
        String loggedInUser; // email address, which is primary key of user db

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            compInputTxt = findViewById(R.id.compLvlTxt);
            locationSwitch = findViewById(R.id.locSwitch);
            applyCompBtn = findViewById(R.id.applyComp);

            //get logged in user for db
            tinydb = new TinyDB(this);
            loggedInUser = tinydb.getString("loggedInAccount");
            Log.d(TAG, "loggedInUser: " + loggedInUser);

            settings = PreferenceManager.getDefaultSharedPreferences(this);

            locationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveLocPref(isChecked);
                }
            });

            applyCompBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (compInputTxt.getText().toString().length() > 0)
                    {
                        saveCompressionPref(Integer.parseInt(compInputTxt.getText().toString()));
                    }
                }
            });


        }

        public void saveLocPref(boolean switchState)
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("LocationSwitch", switchState);
            editor.apply();
            Log.d(TAG, "Location switch set: " + switchState);
        }

        public void saveCompressionPref(int compressionLvl)
        {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt("CompressionLvl", compressionLvl);
            editor.apply();
            Log.d(TAG, "Compression lvl set: " + compressionLvl);
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
    }