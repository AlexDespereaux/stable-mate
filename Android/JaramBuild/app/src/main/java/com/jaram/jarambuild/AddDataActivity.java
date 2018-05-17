package com.jaram.jarambuild;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jaram.jarambuild.Utils.BitmapScaler;

public class AddDataActivity extends AppCompatActivity implements View.OnClickListener
{
    private ImageView imageView;
    private EditText imgTitleInput;
    private EditText subjectInput;
    private EditText descInput;
    private Button saveBtn;

    private String imgTitle;
    private String subject;
    private String desc;
    String editedImgUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //buttons
        saveBtn = findViewById(R.id.saveBtn);

        //text fields
        imgTitleInput = findViewById(R.id.imgTitleInput);
        subjectInput = findViewById(R.id.subjectInput);
        descInput = findViewById(R.id.descInput);


        //register listeners
        saveBtn.setOnClickListener(this);

        //Get bitmap from EditImageActivity and add to image view
        editedImgUri = getIntent().getStringExtra("editedImageUri");
        setImageView();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.saveBtn:
                saveUpload();
                break;
        }
    }

    private void setImageView()
    {
        imageView = findViewById(R.id.imageView);
        if (editedImgUri.equals(""))
        {
            //TODO: add failBitmap
            //imageView.setImageBitmap(BitmapFactory.decodeFile(editedImgUri));
            Toast.makeText(this, "Unable to set imageView", Toast.LENGTH_SHORT).show();
        } else
        {
            //TODO: scale image for display -> https://github.com/codepath/android_guides/wiki/Working-with-the-ImageView
            Bitmap scaledImg = BitmapScaler.scaleToFitWidth(BitmapFactory.decodeFile(editedImgUri), 500);
            imageView.setImageBitmap(scaledImg);
        }
    }

    private void saveUpload()
    {
        // TODO: Get Texts
        // TODO: save data to database & upload
    }
}