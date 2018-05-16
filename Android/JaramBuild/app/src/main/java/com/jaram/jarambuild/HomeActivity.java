package com.jaram.jarambuild;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    //variables (I leave as global until ready for release, and then convert to local variable)
    private Button cameraBtn;
    private Button calibrateBtn;
    private Button galleryBtn;
    private Button settingsBtn;

    //camera
    private static final int CAMERA_REQUEST = 52;
    public static final String EXTRA_IMAGE_PATHS = "extra_image_paths";

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
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                break;
            case R.id.goToSignUpBtn:
                //Go to calibrate activity TODO: create calibrate activity
                //Intent intentSign = new Intent(this, Calibrate.class);
                //startActivity(intentSign);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case CAMERA_REQUEST:
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Intent intent = new Intent(HomeActivity.this, EditImageActivity.class);
                    intent.putExtra("takenPhoto", photo);
                    startActivity(intent);
                    break;
            }
        }
    }
}