package com.jaram.jarambuild;

import android.arch.lifecycle.ViewModelProviders;
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
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//binary creation utils
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
//data
import com.jaram.jarambuild.adapters.LegendListAdapter;
import com.jaram.jarambuild.models.EditModel;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Image;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.roomDb.Legend;
import com.jaram.jarambuild.roomDb.LegendListViewModel;
import com.jaram.jarambuild.roomDb.User;
import com.jaram.jarambuild.roomDb.UserListViewModel;
import com.jaram.jarambuild.utils.ImageIdEvent;
import com.jaram.jarambuild.utils.LegendCreatedEvent;
import com.jaram.jarambuild.utils.TinyDB;

import static com.jaram.jarambuild.roomDb.AppDatabase.getDatabase;

public class AddDataActivity extends AppCompatActivity implements View.OnClickListener
{
    //set log name
    private String TAG = "AddData";

    //layout
    private EditText imgTitleInput;
    private EditText descriptionInput;
    private EditText notesInput;
    private TextView legendHeading;

    //variables
    private String imgTitle;
    private String description;
    private String notes;
    private String editedImgUri;
    private String rawImgUri;
    private String pathName;
    private String jsonPathName;
    private String fusedLocationLong;
    private String fusedLocationLat;
    private Double dFov;
    private Double pixelsPerMicron;
    private int uploadId = -1;

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

    //counter to check number of legends saved to database = total legends in image (reset each image upload)
    int legendUpLoadCounter = 0;
    int imageIdFromEvent;

    //db
    private LegendListViewModel legendViewModel;
    private ImageListViewModel imageViewModel;
    Context context;
    private AppDatabase db;

    //get logged in user
    TinyDB tinydb;
    String loggedInUser; // email address, which is primary key of user db
    //user details
    String userFirstName;
    String userLastName;
    String userPword;

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
        descriptionInput = findViewById(R.id.descInput);
        notesInput = findViewById(R.id.notesInput);

        //register listeners
        saveBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        //Get raw image from intent
        rawImgUri = getIntent().getStringExtra("rawImageUri");
        //Get edited bitmap from EditImageActivity and add to image view
        editedImgUri = getIntent().getStringExtra("editedImageUri");
        //get edited image dfov & pixels per micron from intent
        dFov = Objects.requireNonNull(getIntent().getExtras()).getDouble("dFov");
        pixelsPerMicron = Objects.requireNonNull(getIntent().getExtras()).getDouble("pixelsPerMicron");

        //set image in view
        setImageView();

        //db
        legendViewModel = ViewModelProviders.of(this).get(LegendListViewModel.class);
        imageViewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        db = AppDatabase.getDatabase(getApplicationContext());

        //get logged in user for db
        tinydb = new TinyDB(this);
        loggedInUser = tinydb.getString("loggedInAccount");
        Log.d(TAG, "loggedInUser: " + loggedInUser);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop()
    {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onImageIdEvent(ImageIdEvent event)
    {
        legendUpLoadCounter = 0;
        Log.d(TAG, "Image ID from ImageIdEvent " + event.imageIdMessage);
        imageIdFromEvent =(int)event.imageIdMessage;
        saveLegendToDb(imageIdFromEvent);
    }

    @Subscribe
    public void onLegendCreatedEvent(LegendCreatedEvent event)
    {
        Log.d(TAG, "legend created " + event.legendCreated);
        legendUpLoadCounter++;
        Log.d(TAG, "inEL count = " + legendUpLoadCounter + "arr " + LegendListAdapter.editModelArrayList.size());
        //temp
        if(legendUpLoadCounter == LegendListAdapter.editModelArrayList.size() )
        {
            createJsonObj(imageIdFromEvent);
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
        if (item.getItemId() == R.id.settingsMenuBtn)
        {
            Log.d(TAG, "Settings Btn Clicked");
            //Go to settings activity
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        } else if (item.getItemId() == R.id.helpMenuBtn)
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
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.saveBtn:
                //get text data from fields (not legend list)
                getTexts();
                //get User Details for upload
                getUserDetails();
                getLocation();
                //Save Image in database
                saveImageToDb();
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

    private void saveLegendToDb(int imageId)
    {
        String legendText;
        String stickerImgName;
        String[] stickerListNamesArr = com.jaram.jarambuild.utils.StickerConstants.getStickerListPaths();
        for (int i = 0; i < LegendListAdapter.editModelArrayList.size(); i++)
        {

            legendText = LegendListAdapter.editModelArrayList.get(i).getEditTextValue();
            stickerImgName = stickerListNamesArr[LegendListAdapter.editModelArrayList.get(i).getStickerIndex()];
            context = getApplicationContext();
            //saveLegendToDatabase(stickerImgName, legendText, imageId);
            legendViewModel.addOneLegend(new Legend(stickerImgName, legendText, imageId));
            Log.d(TAG, "Legend saved to dataBase");
        }
    }

    private boolean saveImageToDb()
    {
        imageViewModel.addOneImage(new Image(imgTitle, description, notes, getUnixEpochTime(), fusedLocationLong, fusedLocationLat, Double.toString(dFov), Double.toString(pixelsPerMicron), uploadId, rawImgUri, editedImgUri, loggedInUser));
        Log.d(TAG, "Image saved to dataBase");
        return true;
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
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(editedImgUri, bmOptions);
            imageView.setImageBitmap(bitmap);
        }
    }

    private void getLocation()
    {
        //TODO: put settings conditional
        if (true)
        {
            //deliberately invalid valid values are lat +90 to -90 long +180 to -180
            fusedLocationLong = "181";
            fusedLocationLat = "181";
        }
    }

    private void getTexts()
    {
        //get data as strings from image input fields
        imgTitle = imgTitleInput.getText().toString().trim();
        description = descriptionInput.getText().toString().trim();
        notes = notesInput.getText().toString().trim();
    }

    private void getUserDetails()
    {
        User user =
                getDatabase(this)
                        .getUserDao()
                        .getUserbyId(loggedInUser);
        if (user != null)
        {
            Log.d(TAG, "User exists " + user.toString());
            userFirstName = user.getPWord();
            userLastName = user.getFirstName();
            userPword = user.getLastName();
        } else
        {
            Log.d(TAG, "user does not exist");
        }
    }

    public String getUnixEpochTime()
    {
        Date dateObj = new Date();
        return Long.toString(dateObj.getTime());
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
    }

    private ArrayList<EditModel> populateList()
    {
        ArrayList<EditModel> list = new ArrayList<>();
        for (int a = 0; a < sliList.size(); a++)
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
        if (!isLegend)
        {
            legendHeading.setVisibility(View.GONE);
        }
    }

    private void saveLegendToDatabase(String symbol, String legendTxt, int imgId)
    {
        legendViewModel.addOneLegend(new Legend(symbol, legendTxt, imgId));
        Log.d(TAG, "Legend saved to dataBase");
    }


    private void createJsonObj(int imageId)
    {
        //AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        //get image data by imageId
        Image imageDataObject =
                db.getImageDao().getImageById(imageId);

        //get legend data by imageId
        List<Legend> listOfLegendsFromDb =
                db.getLegendDao().getAllLegendsByImageId(imageId);

        //check legend list
        Log.d(TAG, "listOfLegendsFromDb size" + listOfLegendsFromDb.size());

        //create JSON object
        JSONObject uploadObj = new JSONObject();
        JSONObject locationObj = new JSONObject();
        JSONArray legendArr = new JSONArray();
        try
        {
            uploadObj.put("filename", imageDataObject.getTitle());
            uploadObj.put("description", imageDataObject.getDescription());
            uploadObj.put("notes", imageDataObject.getNotes());
            uploadObj.put("datetime", Double.parseDouble(imageDataObject.getDate()));
            //put location details in location Object
            locationObj.put("latitude", Double.parseDouble(imageDataObject.getLatitude()));
            locationObj.put("longitude", Double.parseDouble(imageDataObject.getLongitude()));
            //put location object in upload object
            uploadObj.put("location", locationObj);
            uploadObj.put("dFov", Double.parseDouble(imageDataObject.getDFov()));
            uploadObj.put("ppm", Double.parseDouble(imageDataObject.getPixelsPerMicron()));
            for (Legend legend : listOfLegendsFromDb)
            {
                JSONObject singleLegend = new JSONObject();
                singleLegend.put("name", legend.getSymbol());
                singleLegend.put("text", legend.getLegendTxt());
                legendArr.put(singleLegend);
            }
            uploadObj.put("Legend", legendArr);
        } catch (JSONException e)
        {
            Log.e(TAG, "JSONException: " + e.getMessage());
        } catch (java.lang.NumberFormatException e)
        {
            Log.e(TAG, "NumberFormatException" + e.getMessage());
            return;
        }
        Log.d(TAG, "Created JSON Object");
        //debug jsonObjPretty
        try
        {
            Log.d(TAG, uploadObj.toString(4));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    //UPLOAD TEST CODE*****************************************************************************************************

    /*

        private String convertToBase64(String imagePath)
    {
        Bitmap bm = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 75, baos);
        byte[] byteArrayImage = baos.toByteArray();
        Log.d(TAG, "Image converted to Base64");
        return Base64.encodeToString(byteArrayImage, Base64.NO_WRAP);
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
    }

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
    }
*/
}

