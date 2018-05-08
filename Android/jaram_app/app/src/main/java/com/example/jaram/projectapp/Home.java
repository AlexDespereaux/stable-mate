package com.example.jaram.projectapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jaram.projectapp.Utils.NetworkUtils;

public class Home extends AppCompatActivity implements View.OnClickListener
{
    //variables (I leave as global until ready for release, and then convert to local variable)
    private Button cameraBtn;
    private Button calibrateBtn;
    private Button galleryBtn;
    private Button settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //buttons
        cameraBtn = findViewById(R.id.cameraBtn);
        calibrateBtn = findViewById(R.id.calibrateBtn);
        galleryBtn = findViewById(R.id.galleryBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        //register listeners
        cameraBtn.setOnClickListener(this);
        calibrateBtn.setOnClickListener(this);
        galleryBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.cameraBtn:
                //Go to camera activity TODO: create camera activity
                //Intent intent = new Intent(this, Camera.class);
                //startActivity(intent);
                break;
            case R.id.goToSignUpBtn:
                //Go to calibrate activity TODO: create calibrate activity
                Intent intent = new Intent(this, Calibrate.class);
                startActivity(intent);
                break;
            case R.id.galleryBtn:
                //Go to gallery activity TODO: create gallery activity
                //Intent intent = new Intent(this, Gallery.class);
                //startActivity(intent);
                break;
            case R.id.settingsBtn:
                //Go to settings activity TODO: create settings activity
                //Intent intent = new Intent(this, Settings.class);
                //startActivity(intent);
                break;
        }
    }
}
