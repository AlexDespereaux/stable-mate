package com.jaram.jarambuild;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity
    {
        private EditText compInputTxt;
        private Switch locationSwitch;
        private Button applyCompBtn;
        private SharedPreferences settings;
        private String TAG = "SettingsAct";

        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            compInputTxt = findViewById(R.id.compLvlTxt);
            locationSwitch = findViewById(R.id.locSwitch);
            applyCompBtn = findViewById(R.id.applyComp);

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
    }