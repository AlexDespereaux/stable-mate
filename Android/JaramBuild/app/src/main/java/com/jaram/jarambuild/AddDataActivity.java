package com.jaram.jarambuild;

import android.Manifest;
import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.jaram.jarambuild.adapters.LegendListAdapter;
import com.jaram.jarambuild.models.EditModel;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Image;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.roomDb.Legend;
import com.jaram.jarambuild.roomDb.LegendListViewModel;
import com.jaram.jarambuild.roomDb.User;
import com.jaram.jarambuild.uploadService.GenerateUploadRequestService;
import com.jaram.jarambuild.utils.GPSTracker;
import com.jaram.jarambuild.utils.ImageIdEvent;
import com.jaram.jarambuild.utils.LegendCreatedEvent;
import com.jaram.jarambuild.utils.NetworkUtils;
import com.jaram.jarambuild.utils.TinyDB;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static com.jaram.jarambuild.HomeActivity.REQUEST_PERMISSION;
import static com.jaram.jarambuild.roomDb.AppDatabase.getDatabase;
import static java.security.AccessController.getContext;


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
    private int legendUpLoadCounter = 0;
    private int imageIdFromEvent;

    //db
    private LegendListViewModel legendViewModel;
    private ImageListViewModel imageViewModel;
    Context context;
    private AppDatabase db;

    //get logged in user
    private TinyDB tinydb;
    private String loggedInUser; // email address, which is primary key of user db

    //user details
    private String userFirstName;
    private String userLastName;
    private String userPword;

    //quickstart
    private static final String SHOWCASE_ID = "add_data_act";
    private ScrollView addDataScrollView;
    private LinearLayout imageDetailsLinearView;

    //location
    private GPSTracker gps;
    private String longitude;
    private String latitude;

    //date
    private String unixDate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);

        //generate Legend Input
        genLegend();

        //linear layout
        imageDetailsLinearView = findViewById(R.id.imgDetailInputs);

        //scrollview
        addDataScrollView = findViewById(R.id.addDataScroller);

        //Recycler View
        legendRecyclerView = findViewById(R.id.legendRecycler);
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
        //get date
        unixDate = Objects.requireNonNull(getIntent().getExtras()).getString("unixDate");

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

        //check internet permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, REQUEST_PERMISSION);
            Log.d(TAG, "Requesting Permissions ");
        } else
        {
            Log.d(TAG, "Has internet permission ");
        }

        //home button in action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null)
        {
            toolbar.setLogo(R.drawable.my_logo_shadow_96px);

            //Listener for item selection change
            toolbar.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    showBackPressDialog();
                }
            });
        }

        //set position for first view
        double viewed = tinydb.getDouble("addDataQuickstartShown", 0.0);
        if(viewed == 0.0)
        {
            tinydb.putDouble("addDataQuickstartShown", 1.0);
            focusOnView();
            Log.d(TAG, "scrolled down ");
        }

        //start Quickstart
        imageDetailsLinearView.post(new Runnable() {
            @Override
            public void run() {
                if(!isLegend) // if there is no legend show the quickstart without the legend
                {
                    presentQuickstartSequence();
                }
                else
                {
                    presentQuickstartSequenceLegend();
                }
            }
        });

        getLocation();
    }

    //set focus to bottom of the scroll view
    private void focusOnView(){
        addDataScrollView.post(new Runnable() {
            @Override
            public void run() {
                //addDataScrollView.scrollTo(0, addDataScrollView.getBottom());
                addDataScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }


    //**************RECEIVERS & EVENTS**************************************************

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION && grantResults.length > 0)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Thanks for granting Permission", Toast.LENGTH_SHORT).show();
            }
        }
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
    public void onImageIdEvent(ImageIdEvent event) //returns ImageId from database upon image save to database
    {
        legendUpLoadCounter = 0;
        Log.d(TAG, "Image ID from ImageIdEvent " + event.imageIdMessage);
        imageIdFromEvent = (int) event.imageIdMessage;
        saveLegendToDb(imageIdFromEvent);
    }

    @Subscribe
    public void onLegendCreatedEvent(LegendCreatedEvent event)
    {
        //this method ensures all the legend db entries are created prior to attempting data upload
        Log.d(TAG, "legend created " + event.legendCreated);
        legendUpLoadCounter++; //checks the no of legend entries sent to database
        Log.d(TAG, "inEL count = " + legendUpLoadCounter + "arr " + LegendListAdapter.editModelArrayList.size());
        //if the amount of legend objects entered in to room db = the size of the list of legends progress to upload
        if (legendUpLoadCounter == LegendListAdapter.editModelArrayList.size())//check that all legend rows have been added to the database prior to upload
        {
            startUpload();
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
                //Save Image in database
                saveImageToDb();
                break;
            case R.id.cancelBtn:
                showBackPressDialog();
                //TODO delete generated files
                break;
        }
    }

    private void startUpload()
    {
        if(NetworkUtils.isNetworkConnected(this))
        {
            Intent mServiceIntent = new Intent();
            //add user to intent
            mServiceIntent.putExtra("loggedInUser", loggedInUser);
            mServiceIntent.putExtra("loggedInUserPWord", userPword);

            // Starts the JobIntentService
            GenerateUploadRequestService.enqueueGURSWork(this, mServiceIntent);
            Log.d(TAG, "enqueueGURSWork call to JobIntentService");
            returnToHome();
        }
        else
        {
            //TODO: Add server check - https://github.com/gotev/android-host-monitor
            Toast.makeText(this, "No network available, images will upload when connection resumed", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Upload cancelled no network");
            returnToHome();
        }
    }

    //**************************helpers***********************************************************************

    private void saveImageToDb()
    {
        imageViewModel.addOneImage(new Image(imgTitle, description, notes, unixDate, longitude, latitude, Double.toString(dFov), Double.toString(pixelsPerMicron), uploadId, rawImgUri, editedImgUri, loggedInUser));
        Log.d(TAG, "Image saved to dataBase");
    }

    private void saveLegendToDb(int imageId) // after the image paths and data have been added to database image Id is returned and used as the foreign key in the legend rows
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
        if (LegendListAdapter.editModelArrayList.size() == 0) // start the upload in the case that there is no legend
        {
            startUpload();
        }
    }

    private void setImageView()
    {
        ImageView imageView = findViewById(R.id.imageView);
        if (editedImgUri.equals(""))
        {
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
        //default value = false
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        boolean saveLocation = settings.getBoolean("LocationSwitch", false);
        Log.d(TAG, "Location preference: " + saveLocation);
        if (saveLocation)
        {
            gps = new GPSTracker(AddDataActivity.this);
            if(gps.canGetLocation())
            {
                latitude = Double.toString(gps.getLatitude());
                longitude = Double.toString(gps.getLongitude());

                // \n is for new line
                Log.d(TAG,"Your Location is - \nLat: " + latitude + "\nLong: " + longitude);
            } else {
                // Can't get location.
                // GPS or network is not enabled.
                // Ask user to enable GPS/network in settings.
                Log.d(TAG, "Location error, not saved");
            }
            Log.d(TAG, "Location saved");
        } else
        {
            //deliberately invalid valid values are lat +90 to -90 long +180 to -180
            longitude = "181";
            latitude = "181";
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
            userPword = user.getPWord();
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

    private void returnToHome()
    {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        startActivity(homeIntent);
    }


    //************************DIALOGS****************************

    @Override
    public void onBackPressed()
    {

        showBackPressDialog();
        // Otherwise defer to system default behavior.
        //super.onBackPressed();
    }

    private void showBackPressDialog()
    {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        builder.setMessage("Are you want to exit without saving image ?");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Discard Image", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                finish();
            }
        });
        builder.create().show();
    }

    private void presentQuickstartSequence() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(imageDetailsLinearView)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Add image data to be uploaded to the cloud")
                        .withRectangleShape()
                        .build()
        );
        sequence.start();
    }

    //version to show if legend present
    private void presentQuickstartSequenceLegend() {

        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(500); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);

        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
            @Override
            public void onShow(MaterialShowcaseView itemView, int position) {
                //Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
            }
        });

        sequence.setConfig(config);

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(imageDetailsLinearView)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Add image data to be uploaded to the cloud")
                        .withRectangleShape()
                        .build()
        );

        sequence.addSequenceItem(
                new MaterialShowcaseView.Builder(this)
                        .setTarget(legendRecyclerView)
                        .setDismissText("GOT IT")
                        .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                        .setMaskColour(Color.parseColor("#E6000000"))
                        .setContentText("Add legend definitions to be uploaded to the cloud")
                        .withRectangleShape()
                        .build()
        );
        sequence.start();
    }
}

