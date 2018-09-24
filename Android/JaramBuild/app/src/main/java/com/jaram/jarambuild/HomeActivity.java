package com.jaram.jarambuild;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.jaram.jarambuild.uploadService.RequestQueueSingleton;
import com.jaram.jarambuild.utils.TinyDB;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import net.gotev.uploadservice.UploadService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener
{
    //variables (I leave as global until ready for release, and then convert to local variable)
    private Button cameraBtn;
    private Button calibrateBtn;
    private Button galleryBtn;
    private Button settingsBtn;

    //camera
    public static final int REQUEST_PERMISSION = 200;
    public static final int REQUEST_IMAGE = 100;
    public static final int REQUEST_CALIBRATE = 300;
    private String imageFilePath = "";

    //permissions
    private boolean permissionsGranted = false;

    //image
    Uri photoUri;

    //log
    private static final String TAG = "HomeActivity";

    //logged in user
    TinyDB tinydb;
    String loggedInUser;

    //volley
    private RequestQueue queue;
    static final String REQ_TAG = "UIS";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //shared prefs
        tinydb = new TinyDB(this);

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

        //check permissions (to avoid crashy funtime due to camera permission bug in Android M)
        requestPermissions();

        //logo in action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_bar);
        if (toolbar != null)
        {
            toolbar.setLogo(R.drawable.my_logo_shadow_96px);
        }

        //get queue instance (Home activity has an upload failsafe, where by logging out will cancel ALL uploads!)
        //only required during development
        queue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();
    }

    /**
     * Requesting multiple permissions (storage and location) at once
     * This uses multiple permission model from dexter
     * On permanent denial opens settings dialog
     */
    private void requestPermissions()
    {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(new MultiplePermissionsListener()
                {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report)
                    {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted())
                        {
                            Log.d(TAG, "All permissions are granted!");
                            permissionsGranted = true;
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied())
                        {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token)
                    {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener()
                {
                    @Override
                    public void onError(DexterError error)
                    {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("Annomate needs permissions to store and upload your photos. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings()
    {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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

        }
        else if (item.getItemId() == R.id.logoutMenuBtn)
        {
            Log.d(TAG, "Logout Btn Clicked");
            //set logged in user to null
            tinydb.putString("loggedInAccount", "");
            //return to Login Page
            Intent settingsIntent = new Intent(this, MainActivity.class);
            startActivity(settingsIntent);
            //stop all uploads
            UploadService.stopAllUploads();
            if (queue != null)
            {
                queue.cancelAll(REQ_TAG);
            }
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
        if (permissionsGranted)
        {
            switch (v.getId())
            {
                case R.id.cameraBtn:
                    //Open Camera
                    openCameraIntent("editActivity");
                    break;
                case R.id.calibrateBtn:
                    //Open Camera
                    openCameraIntent("calibrateActivity");
                    break;
                case R.id.galleryBtn:
                    //Go to gallery activity
                    Intent galleryIntent = new Intent(this, GalleryActivity.class);
                    startActivity(galleryIntent);
                    break;
                case R.id.settingsBtn:
                    //Go to settings activity
                    Intent settingsIntent = new Intent(this, SettingsActivity.class);
                    startActivity(settingsIntent);
                    break;
            }
        } else
        {
            showSettingsDialog();
        }
    }

    void openCameraIntent(String destination)
    {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (pictureIntent.resolveActivity(getPackageManager()) != null)
        {
            // Create the File where the photo should go
            File photoFile = null;
            try
            {
                photoFile = createImageFile();
            } catch (IOException e)
            {
                e.printStackTrace();
                return;
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                //
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                if (destination == "editActivity")
                {
                    startActivityForResult(pictureIntent, REQUEST_IMAGE);
                } else
                {
                    startActivityForResult(pictureIntent, REQUEST_CALIBRATE);
                }
                Log.d(TAG, "startAct Uri: " + photoUri);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case REQUEST_IMAGE:
                if (resultCode == RESULT_OK)
                {
                    Intent intent = new Intent(HomeActivity.this, CheckCalibrationActivity.class);
                    //add raw file path URI string to intent
                    intent.putExtra("rawPhotoPath", imageFilePath);
                    Log.d(TAG, "rawPhotoPath: " + imageFilePath);
                    //open edit Check calibration Activity
                    startActivity(intent);
                } else if (resultCode == RESULT_CANCELED)
                {
                    Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "result cancelled");
                }
                break;
            case REQUEST_CALIBRATE:
                if (resultCode == RESULT_OK)
                {
                    Intent intent = new Intent(HomeActivity.this, CalibrateActivity.class);
                    //add raw file path URI string to intent
                    intent.putExtra("rawPhotoPath", imageFilePath);
                    Log.d(TAG, "rawPhotoPath: " + imageFilePath);
                    //open edit Image Activity
                    startActivity(intent);
                } else if (resultCode == RESULT_CANCELED)
                {
                    Toast.makeText(this, "You cancelled the operation", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "result cancelled");
                }
                break;
        }
    }

    private File createImageFile() throws IOException
    {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "RAW_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".png", storageDir);
        imageFilePath = image.getAbsolutePath();
        return image;
    }
}

