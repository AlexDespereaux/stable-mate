package com.jaram.jarambuild;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//upload utils
import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;
//json utils
import org.json.JSONException;
import org.json.JSONObject;
//binary creation utils
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
//shared prefs
import com.jaram.jarambuild.adapters.LegendListAdapter;
import com.jaram.jarambuild.models.EditModel;
import com.jaram.jarambuild.utils.TinyDB;

public class AddDataActivity extends AppCompatActivity implements View.OnClickListener
{
    //set log name
    private String TAG = "AddData";

    private EditText imgTitleInput;
    private EditText subjectInput;
    private EditText descInput;
    private TextView legendHeading;

    //JSON data variables
    private String imgTitle;
    private String subject;
    private String desc;
    private String editedImgUri;
    private String rawImgUri;
    private String userName;
    private String userPassword;
    private String studentNo;
    private JSONObject uploadObj;
    private String utcTime;
    private String pathName;
    private String jsonPathName;

    //Shared Prefs
    TinyDB tinydb;

    //List of sticker images (drawable resource files)
    int[] stickerList;

    //Array list of sticklerlist indexs of drawables used.
    private ArrayList<String> sliList;

    //Recycler View
    private RecyclerView legendRecyclerView;
    private LegendListAdapter legendListAdapter;
    public ArrayList<EditModel> editModelArrayList;

    //check if there are legend rows to discern if the heading should be displayed
    private boolean isLegend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //generate Legend Input
        genLegend();

        //Recycler View
        legendRecyclerView = (RecyclerView) findViewById(R.id.legendRecycler);
        legendRecyclerView.setNestedScrollingEnabled(false);
        editModelArrayList = populateList();
        legendListAdapter = new LegendListAdapter(this, editModelArrayList);
        legendRecyclerView.setAdapter(legendListAdapter);
        legendRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        //display legend heading if legend exists
        legendHeading = findViewById(R.id.legendHeading);
        setLegendHeadingVis();

        //buttons
        Button saveBtn = findViewById(R.id.saveBtn);
        Button cancelBtn = findViewById(R.id.cancelBtn);

        //text fields
        imgTitleInput = findViewById(R.id.imgTitleInput);
        subjectInput = findViewById(R.id.subjectInput);
        descInput = findViewById(R.id.descInput);

        //register listeners
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        //Get raw image from intent
        rawImgUri = getIntent().getStringExtra("rawImageUri");
        //Get bitmap from EditImageActivity and add to image view
        editedImgUri = getIntent().getStringExtra("editedImageUri");
        setImageView();
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
        if(item.getItemId()== R.id.settingsMenuBtn)
        {
            Log.d(TAG, "Settings Btn Clicked");
            //Go to settings activity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if(item.getItemId()== R.id.helpMenuBtn)
        {
            //TODO Make help activity
            Toast.makeText(this, "Help Menu TBC", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Help Btn Clicked");

        }

        else if(item.getItemId()== R.id.logoutMenuBtn)
        {
            Log.d(TAG, "Logout Btn Clicked");
            //set logged in user to null
            tinydb.putString("loggedInAccount", "");
            //return to Login Page
            Intent settingsIntent = new Intent(this, MainActivity.class);
            startActivity(settingsIntent);
            finish();
        }
        else
        {
            return super.onOptionsItemSelected(item);
        }
        return true;
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
                //uploadToCloudJson(v);

                //Test Upload
                //uploadTest(getApplicationContext());
                break;
            case R.id.cancelBtn:
                //Return to home activity
                Intent cancelIntent = new Intent(this, HomeActivity.class);
                startActivity(cancelIntent);
                //TODO delete generated files
                Log.e(TAG, "Returned to home");
                finish();
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
            //TODO: scale image for display if required-> https://github.com/codepath/android_guides/wiki/Working-with-the-ImageView
            //Bitmap scaledImg = JBitmapScaler.scaleToFitWidth(BitmapFactory.decodeFile(editedImgUri), 400);
            //imageView.setImageBitmap(scaledImg);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(editedImgUri, bmOptions);
            imageView.setImageBitmap(bitmap);
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
        bm.compress(Bitmap.CompressFormat.PNG, 75, baos);
        byte[] byteArrayImage = baos.toByteArray();
        Log.d(TAG, "Image converted to Base64");
        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
    }

    public void getUTCTime(View view)
    {
        //TODO: make date UTC
        //long time = getTime();
        utcTime = "123456";
    }

    public void genLegend()
    {
        //get sliList (ArrayList containing index numbers of used stickers from stickerlist) from SP
        tinydb = new com.jaram.jarambuild.utils.TinyDB(this);
        sliList = tinydb.getListStringTinyDB("stickerIndexAL");

        //get stickerList
        stickerList = com.jaram.jarambuild.utils.StickerConstants.getStickerList();

        //remove duplicates from legend image arraylist by conversting to hashset and back.
        Set<String> hs = new HashSet<>();
        hs.addAll(sliList);
        sliList.clear();
        sliList.addAll(hs);

        //For Debugging
        /*
        Iterator itr = sliList.iterator();
        while (itr.hasNext())
        {
            Log.d(TAG, "iterated array list " + itr.next());
        }*/
    }

    private ArrayList<EditModel> populateList()
    {
        ArrayList<EditModel> list = new ArrayList<>();
        for (int a = 0; a <sliList.size(); a++)
        {
            isLegend = true;
            String sLindex = sliList.get(a);
            if (!sLindex.equals("notSticker"))
            {
                EditModel editModel = new EditModel();
                //editModel.setEditTextValue("Enter legend here");
                editModel.setStickerIndex(Integer.parseInt(sLindex));
                list.add(editModel);
                Log.d(TAG, "populated sticker index " + Integer.parseInt(sLindex));
            }
        }
        return list;
    }

    private void setLegendHeadingVis()
    {
        //if there is no legend symbols to collect data for, hide the legend section heading
        if(!isLegend)
        {
            legendHeading.setVisibility(View.GONE);
        }
    }



    

    //UPLOAD TEST CODE*****************************************************************************************************


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
            Log.e(TAG, "JSONException: " + e.getMessage());
        } catch (java.lang.NumberFormatException e)
        {
            Log.e(TAG, "NumberFormatException" + e.getMessage());
            return;
        }

        //write JSON Object to string and save in file
        if (uploadObj != null)
        {
            Log.d(TAG, "Created JSON Object");
            writeJsonToBinaryFile(context, uploadObj);
            writeJsonToFile(context, uploadObj);
        } else
        {
            Log.d(TAG, "JSON Object is null, Upload failed");
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
            Log.d(TAG, "File " + newFile.getName() + " is saved successfully at " + newFile.getAbsolutePath());
            pathName = newFile.getAbsolutePath();
        } catch (Exception e)
        {
            Log.d(TAG, "Unable to save file", e);
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
            Log.d(TAG, "File " + newFile.getName() + " is saved successfully at " + newFile.getAbsolutePath());
            jsonPathName = newFile.getAbsolutePath();
        } catch (Exception e)
        {
            Log.d(TAG, "Unable to save file", e);
        }
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
            Log.d(TAG, "Binary File uploaded");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

        //TODO: check sucessful upload & delete system image files & Obj upon confirm
        //TODO: save binary files to database and load from database in loop IF wifi access available. Otherwise wait for broadcast RX msg
    }

    //TODO Just added for testing
    public void uploadTestBinary(final Context context)
    {
        try
        {
            String uploadId =
                    new BinaryUploadRequest(context, "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/image")
                            .setFileToUpload(rawImgUri)
                            .addHeader("token", "1F8065545D842E0098709630DBDBEB596D4D6194")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
            Log.d(TAG, "Binary File uploaded " + rawImgUri);
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
    }

    public void uploadTest(final Context context) {
        try {
            String uploadId =
                    new MultipartUploadRequest(context, "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/image")
                            // starting from 3.1+, you can also use content:// URI string instead of absolute file
                            .addFileToUpload(editedImgUri, "raw_img")
                            .addHeader("token", "1F8065545D842E0098709630DBDBEB596D4D6194")
                            .addHeader("content", "png")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();
        } catch (Exception exc) {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }
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
            Log.d(TAG, "Binary File uploaded");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

        //TODO: check sucessful upload & delete system image files & Obj upon confirm
        //TODO: save binary files to database and load from database in loop IF wifi access available. Otherwise wait for broadcast RX msg
    }




}

