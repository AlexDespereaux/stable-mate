package com.jaram.jarambuild;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.Menu;

import com.jaram.jarambuild.utils.TinyDB;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.weyye.hipermission.HiPermission;
import me.weyye.hipermission.PermissionCallback;
import me.weyye.hipermission.PermissionItem;

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

    //image
    Uri photoUri;

    //log
    private static final String TAG = "HomeActivity";

    //logged in user
    TinyDB tinydb;
    String loggedInUser;

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
        checkPerms();
    }

    public void checkPerms()
    {
        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
        permissionItems.add(new PermissionItem(Manifest.permission.CAMERA, "Camera", R.drawable.ic_camera_black_24dp));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_FINE_LOCATION, "Fine Location", R.drawable.ic_location_on_black_24dp));
        permissionItems.add(new PermissionItem(Manifest.permission.INTERNET, "Internet", R.drawable.wifi));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_COARSE_LOCATION, "Coarse Location", R.drawable.ic_location_on_black_24dp));
        permissionItems.add(new PermissionItem(Manifest.permission.ACCESS_NETWORK_STATE, "Network state", R.drawable.network));
        permissionItems.add(new PermissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "Storage", R.drawable.storage));
        HiPermission.create(this)
                .permissions(permissionItems)
                .style(R.style.PermissionJaramStyle)
                .checkMutiPermission(new PermissionCallback()
                {
                    @Override
                    public void onClose()
                    {
                        Log.i(TAG, "onClose");
                    }

                    @Override
                    public void onFinish()
                    {
                        Log.i(TAG, "onFinish");
                    }

                    @Override
                    public void onDeny(String permission, int position)
                    {
                        Log.i(TAG, "onDeny");
                    }

                    @Override
                    public void onGuarantee(String permission, int position)
                    {
                        Log.i(TAG, "onGuarantee");
                    }
                });
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
        if (resultCode == RESULT_OK)
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
    /*
    //trial 4
    public void rotateBitmapOrientation(String photoFilePath)
    {
        Log.d(TAG, "in rotate, photoFilePath" + photoFilePath);
        // Create and configure BitmapFactory
        //BitmapFactory.Options bounds = new BitmapFactory.Options();
        //bounds.inJustDecodeBounds = true;
        //BitmapFactory.decodeFile(photoFilePath, bounds);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        Bitmap bm = BitmapFactory.decodeFile(photoFilePath, opts);
        Log.d(TAG, "in rotate, made new path");
        // Read EXIF Data
        ExifInterface exif = null;
        try
        {
            exif = new ExifInterface(photoFilePath);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        String orientString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
        int orientation = orientString != null ? Integer.parseInt(orientString) : ExifInterface.ORIENTATION_NORMAL;
        int rotationAngle = 0;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) rotationAngle = 90;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_180) rotationAngle = 180;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_270) rotationAngle = 270;
        Log.d(TAG, "in rotate orientation: " + orientation);
        // Rotate Bitmap
        Matrix matrix = new Matrix();
        Log.d(TAG, "in rotate made new matrix");
        matrix.postRotate(rotationAngle);
        Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        //matrix.setRotate(rotationAngle, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        //Bitmap rotatedBitmap = Bitmap.createBitmap(bm, 0, 0, bounds.outWidth, bounds.outHeight, matrix, true);

        //save bitmap to new file name
        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(createImageFile()); //sets variable imagefilepath to new absolute file path so fixed image is passed to intent
            rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }


    }


    //******************************trial2 (more of a one stop shop)

    public Bitmap rotateImageFixer(Bitmap bitmapIn)
    {
        Bitmap rotatedBitmapOut;
        ExifInterface exifInterface = null;
        try
        {
            exifInterface = new ExifInterface(imageFilePath);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix fixMatrix = new Matrix();
        switch (orientation)
        {
            case ExifInterface.ORIENTATION_ROTATE_90:
                fixMatrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                fixMatrix.setRotate(180);
                break;
            default:
        }
        return rotatedBitmapOut = Bitmap.createBitmap(bitmapIn, 0, 0, bitmapIn.getWidth(), bitmapIn.getHeight(), fixMatrix, true);
    }

    //****************************trial
    public static Bitmap rotateImageIfRequired(Bitmap img, Context context, Uri selectedImage) throws IOException
    {

        if (selectedImage.getScheme().equals("content"))
        {
            String[] projection = {MediaStore.Images.ImageColumns.ORIENTATION};
            Cursor c = context.getContentResolver().query(selectedImage, projection, null, null, null);
            if (c.moveToFirst())
            {
                final int rotation = c.getInt(0);
                c.close();
                return rotateImage(img, rotation);
            }
            return img;
        } else
        {
            ExifInterface ei = new ExifInterface(selectedImage.getPath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            Log.d(TAG, "orientation: " + orientation);

            switch (orientation)
            {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        }
    }

    //************************trial code works
    private static Bitmap rotateImage(Bitmap img, int degree)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        return rotatedImg;
    }

    //*********************trial code works
    private String getOrientation(Uri uri)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        String orientation = "landscape";
        try
        {
            String image = new File(uri.getPath()).getAbsolutePath();
            BitmapFactory.decodeFile(image, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            if (imageHeight > imageWidth)
            {
                orientation = "portrait";
            }
        } catch (Exception e)
        {
            //Do nothing
        }
        return orientation;
    }

    private static int fixOrientation(Bitmap bitmap)
    {
        if (bitmap.getWidth() > bitmap.getHeight())
        {
            return 90;
        }
        return 0;
    }

    public static Bitmap flipImage(Bitmap bitmap)
    {
        Matrix matrix = new Matrix();
        int rotation = fixOrientation(bitmap);
        matrix.postRotate(rotation);
        matrix.preScale(-1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }*/
