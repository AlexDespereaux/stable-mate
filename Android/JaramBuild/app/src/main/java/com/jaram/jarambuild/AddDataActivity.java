package com.jaram.jarambuild;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
//image utils

//upload utils
import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
//json utils
import org.json.JSONException;
import org.json.JSONObject;
//binary creation utils
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class AddDataActivity extends AppCompatActivity implements View.OnClickListener
{
    //set log name
    private String LOG_TAG = "AddData";

    private EditText imgTitleInput;
    private EditText subjectInput;
    private EditText descInput;

    //JSON data variables
    private String imgTitle;
    private String subject;
    private String desc;
    private String editedImgUri;
    private String userName;
    private String userPassword;
    private String studentNo;
    private JSONObject uploadObj;
    private String utcTime;
    private String pathName;
    private String jsonPathName;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //buttons
        Button saveBtn = findViewById(R.id.saveBtn);

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
                // Binary upload
                //uploadToCloudBinary(v);

                //JSON upload
                uploadToCloudJson(v);
                break;
        }
    }

    private void setImageView()
    {
        ImageView imageView = findViewById(R.id.imageView);
        if (editedImgUri.equals(""))
        {
            //TODO: add failBitmap
            Toast.makeText(this, "Unable to set imageView", Toast.LENGTH_SHORT).show();
        } else
        {
            //TODO: scale image for display -> https://github.com/codepath/android_guides/wiki/Working-with-the-ImageView
            //Bitmap scaledImg = JBitmapScaler.scaleToFitWidth(BitmapFactory.decodeFile(editedImgUri), 400);
            //imageView.setImageBitmap(scaledImg);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(editedImgUri,bmOptions);
            imageView.setImageBitmap(bitmap);
        }
    }


    private void uploadToCloudBinary(View v)
    {
        getUTCTime(v);
        createJsonObj(getApplicationContext());
        uploadBinary(getApplicationContext());
    }

    private void uploadToCloudJson(View v)
    {
        getUTCTime(v);
        createJsonObj(getApplicationContext());
        uploadJson(getApplicationContext());
    }

    private void createJsonObj(Context context)
    {
        //get user data from shared prefs
        getUserDetails();
        //get data input by user into this activity
        getTexts();
        //convert image from image path to Base64 string
        String editedImgBase64 = convertToBase64(editedImgUri);
        //create JSON object
        uploadObj = new JSONObject();
        try
        {
            uploadObj.put("user", userName);
            uploadObj.put("password", userPassword);
            uploadObj.put("studentNo", studentNo);
            uploadObj.put("editedImage64", editedImgBase64);
            uploadObj.put("imgTitle", imgTitle);
            uploadObj.put("subject", subject);
            uploadObj.put("description", desc);

        } catch (JSONException e)
        {
            Log.e(LOG_TAG, "JSONException: " + e.getMessage());
        } catch (java.lang.NumberFormatException e)
        {
            Log.e(LOG_TAG, "NumberFormatException" + e.getMessage());
            return;
        }

        //write JSON Object to string and save in file
        if (uploadObj != null)
        {
            Log.d(LOG_TAG, "Created JSON Object");
            writeJsonToBinaryFile(context, uploadObj);
            writeJsonToFile(context, uploadObj);
        } else
        {
            Log.d(LOG_TAG, "JSON Object is null, Upload failed");
        }
    }

    private void writeJsonToBinaryFile(Context context, JSONObject uploadObj)
    {
        //write binary file
        try
        {
            //note I have had to include a replace backslash as during conversion to JSON escape characters \ are added in every instance there is a /
            //in the base64 image string(ugh) corrupting the image. Apparently this can be also avoided by putting the base64 in a JSON array inside the object. I'll try that next!
            //OR we can use multipart upload and I will only replace in the base64 files which don't have a \ in the alphabet choices choices
            String objString = uploadObj.toString().replace("\\", "");

            File path = context.getFilesDir();
            //File newFile = new File(path + utcTime + ".dat");  //final code
            File newFile = new File(path + "testfile" + ".dat");  //for testing only
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(objString.getBytes());
            //fos.write(uploadObj.toString().getBytes());  //to use if base64 image no ling requires replace
            fos.flush();
            fos.close();
            Log.d(LOG_TAG, "File " + newFile.getName() + " is saved successfully at " + newFile.getAbsolutePath());
            pathName = newFile.getAbsolutePath();
        } catch (Exception e)
        {
            Log.d(LOG_TAG, "Unable to save file", e);
        }
    }

    private void writeJsonToFile(Context context, JSONObject uploadObj)
    {
        //write binary file
        try
        {
            //note I have had to include a replace backslash as during conversion to JSON escape characters \ are added in every instance there is a /
            //in the base64 image string(ugh) corrupting the image. Apparently this can be also avoided by putting the base64 in a JSON array inside the object. I'll try that next!
            //OR we can use multipart upload and I will only replace in the base64 files which don't have a \ in the alphabet choices choices
            String objString = uploadObj.toString().replace("\\", "");

            File path = context.getFilesDir();
            //File newFile = new File(path + utcTime + ".dat");  //final code
            File newFile = new File(path + "testfile" + ".json");  //for testing only
            FileOutputStream fos = new FileOutputStream(newFile);
            fos.write(objString.getBytes());
            //fos.write(uploadObj.toString().getBytes());  //to use if base64 image no ling requires replace
            fos.flush();
            fos.close();
            Log.d(LOG_TAG, "File " + newFile.getName() + " is saved successfully at " + newFile.getAbsolutePath());
            jsonPathName = newFile.getAbsolutePath();
        } catch (Exception e)
        {
            Log.d(LOG_TAG, "Unable to save file", e);
        }
    }

    private void getTexts()
    {
        //get data as strings from image input fields
        imgTitle = imgTitleInput.getText().toString().trim();
        subject = subjectInput.getText().toString().trim();
        desc = descInput.getText().toString().trim();
    }

    private void getUserDetails()
    {
        userName = "test";
        userPassword = "pass";
        studentNo = "12345678";
        //TODO: get data from shared preferences once login & signup set
    }

    private String convertToBase64(String imagePath)
    {
        //TODO make quality settings user controlled in settings
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 75, baos);
        byte[] byteArrayImage = baos.toByteArray();
        Log.d(LOG_TAG, "Image converted to Base64");
        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
    }

    public void getUTCTime(View view)
    {
        //TODO: make date UTC
        //long time = getTime();
        utcTime = "123456";
    }

    public void uploadBinary(final Context context)
    {
        try
        {
            String uploadId =
                    new BinaryUploadRequest(context, "http://192.168.1.108:3000/upload/binary")
                            .setFileToUpload(pathName)
                            .addHeader("file-name", new File(pathName).getName())
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
            Log.d(LOG_TAG, "Binary File uploaded");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

        //TODO: check sucessful upload & delete system image files & Obj upon confirm
        //TODO: save binary files to database and load from database in loop IF wifi access available. Otherwise wait for broadcast RX msg
    }

    public void uploadJson(final Context context)
    {
        try
        {
            String uploadId =
                    new BinaryUploadRequest(context, "http://192.168.1.108:3000/upload/binary")
                            .setFileToUpload(pathName)
                            .addHeader("file-name", new File(jsonPathName).getName())
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
            Log.d(LOG_TAG, "Binary File uploaded");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

        //TODO: check sucessful upload & delete system image files & Obj upon confirm
        //TODO: save binary files to database and load from database in loop IF wifi access available. Otherwise wait for broadcast RX msg
    }
}

