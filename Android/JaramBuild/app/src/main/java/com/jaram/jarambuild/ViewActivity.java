package com.jaram.jarambuild;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.roomDb.LegendListViewModel;
import com.jaram.jarambuild.utils.TinyDB;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener
{
    //text & buttons
    private TextView imgTitleDisplay;
    private TextView descDisplay;
    private TextView notesDisplay;
    private TextView dateDisplay;
    private TextView locationDisplay;
    private TextView legendHeading;
    private Button cancelBtn;
    private Button editBtn;
    private Boolean isLegend = false;
    //recycler
    private RecyclerView legendRecyclerDisplay;
    //db
    private LegendListViewModel legendViewModel;
    Context context;
    private AppDatabase db;
    //logging
    private String TAG = "ViewActivity";
    //get logged in user
    private TinyDB tinydb;
    private String loggedInUser; // email address, which is primary key of user db
    //image data
    private int imageId;
    private String description;
    private String notes;
    private String date;
    private String longitude;
    private String latitude;
    private String dFov;
    private String ppm;
    private String raw_path;
    private String edit_path;
    private String imageTitle;
    private String link;
    private String linkAddress;
    private String convertedDate;
    //quickstart
    private static final String SHOWCASE_ID = "view_act";
    private ScrollView viewScroller;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        //get data from intent
        getDataFromIntent();

        //set text from intent data
        imgTitleDisplay = findViewById(R.id.imgTitleDisplay);
        imgTitleDisplay.setText("Image Title: " + imageTitle);
        descDisplay = findViewById(R.id.descDisplay);
        descDisplay.setText("Image Description: " + description);
        notesDisplay = findViewById(R.id.notesDisplay);
        notesDisplay.setText("Image Notes: " + notes);
        dateDisplay = findViewById(R.id.dateDisplay);
        dateDisplay.setText("Image Date: " + convertedDate);
        locationDisplay = findViewById(R.id.locationDisplay);
        viewScroller = findViewById(R.id.viewScroller);
        if(longitude.equals("182") || longitude.equals("181"))
        {
            locationDisplay.setVisibility(View.GONE);
        }
        else
        {
            locationDisplay.setText("Image location: " + longitude + ", " + latitude);
        }

        //buttons
        cancelBtn = findViewById(R.id.cancelBtn);
        editBtn = findViewById(R.id.editBtn);

        //display legend heading if legend exists
        legendHeading = findViewById(R.id.legendHeading);
        setLegendHeadingVis();

        //recycler
        legendRecyclerDisplay = findViewById(R.id.legendDisplay);
        legendRecyclerDisplay.setNestedScrollingEnabled(false);

        //register listeners
        editBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        //set image in view
        setImageView();

        //get logged in user for db
        tinydb = new TinyDB(this);
        loggedInUser = tinydb.getString("loggedInAccount");
        Log.d(TAG, "loggedInUser: " + loggedInUser);

        //set position for first view
        double viewed = tinydb.getDouble("viewQuickstartShown", 0.0);
        if(viewed == 0.0)
        {
            tinydb.putDouble("viewQuickstartShown", 1.0);
            focusOnView();
            Log.d(TAG, "scrolled down ");
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
                    returnToHome();
                }
            });
        }

        startQuickstart();
    }

    //set focus to bottom of the scroll view
    private void focusOnView(){
        viewScroller.post(new Runnable() {
            @Override
            public void run() {
                viewScroller.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    private void startQuickstart()
    {
        //start Quickstart
        editBtn.post(new Runnable() {
            @Override
            public void run() {
                presentQuickStart();
            }
        });
    }

    private void getDataFromIntent()
    {
        Intent intent = getIntent();
        imageId = intent.getIntExtra("imageId", -1);
        imageTitle = intent.getStringExtra("imageTitle");
        description = intent.getStringExtra("imageDesc");
        notes = intent.getStringExtra("imageNotes");
        date = intent.getStringExtra("imageDate");
        longitude = intent.getStringExtra("imageLongitude");
        latitude = intent.getStringExtra("imageLatitude");
        dFov = intent.getStringExtra("imageDFov");
        ppm = intent.getStringExtra("pixelsPerMicron");
        raw_path = intent.getStringExtra("photoPath_raw");
        edit_path = intent.getStringExtra("photoPath_edited");
        convertedDate = intent.getStringExtra("convertedDate");
        if(longitude  == "181")
        {
            longitude = "Location: N/A";
            latitude = "Location: N/A";
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.editBtn:
                //go to crop view
                Intent intent = new Intent(this, CropActivity.class);
                //Add data to intent
                intent.putExtra("unixDate", date);
                intent.putExtra("imageLongitude", longitude);
                intent.putExtra("imageLatitude", latitude);
                intent.putExtra("confirmedDFOv", Double.parseDouble(dFov));
                intent.putExtra("confirmedPixelsPerMicron", Double.parseDouble(ppm));
                intent.putExtra("rawPhotoPath", raw_path);
                intent.putExtra("scaleBarColourIndex", 1);
                intent.putExtra("imgWidthInCCView", getScreenWidth());
                startActivity(intent);

                break;
            case R.id.cancelBtn:
                returnToHome();
                break;
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

    private void setImageView()
    {
        ImageView imageView = findViewById(R.id.imageView);
        if (edit_path.equals(""))
        {
            Toast.makeText(this, "Unable to set imageView", Toast.LENGTH_SHORT).show();
        } else
        {
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(edit_path, bmOptions);
            imageView.setImageBitmap(bitmap);
        }
    }

    private int getScreenWidth()
    {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return width;
    }

    private void presentQuickStart()
    {
        new MaterialShowcaseView.Builder(this)
                .setTarget(editBtn)
                .setDismissText("GOT IT")
                .setContentTextColor(Color.parseColor("#FFFFFFFF"))
                .setMaskColour(Color.parseColor("#E6E4690A"))
                .setContentText("Click to crop and annotate original image")
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .withRectangleShape()
                .show();
    }
}
