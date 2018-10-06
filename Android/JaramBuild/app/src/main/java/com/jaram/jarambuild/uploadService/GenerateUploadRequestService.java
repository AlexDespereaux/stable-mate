package com.jaram.jarambuild.uploadService;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Image;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.roomDb.LegendListViewModel;

import java.util.List;

public class GenerateUploadRequestService extends JobIntentService
{
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;
    //db
    private LegendListViewModel legendViewModel;
    private ImageListViewModel imageViewModel;
    private AppDatabase db;

    //data source
    private List<Image> imagesToBeUploadedList;

    //logging
    private String TAG = "GenerateUploadRS";

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueGURSWork(Context context, Intent work)
    {
        enqueueWork(context, GenerateUploadRequestService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        Log.i("GenerateUploadRS", "Starting work: " + intent);
        //get user details to pass to upload services
        String username = intent.getStringExtra("loggedInUser");
        String pword = intent.getStringExtra("loggedInUserPWord");
        Log.d(TAG, "username & pword " + username + " / " + pword);

        //db
        db = AppDatabase.getDatabase(getApplicationContext());
        imageViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance((Application) getApplication()).create(ImageListViewModel.class);

        //get image list to be uploaded
        imagesToBeUploadedList = imageViewModel.getImagesToBeUploadedList();

        //send data to upload que
        for (Image image : imagesToBeUploadedList)
        {
            int imageIdToBeUploaded = image.getImageId();
            //Log.i(TAG, "Image in upload list = " + imageIdToBeUploaded);
            Intent mServiceIntent = new Intent();
            //Add data to intent
            mServiceIntent.putExtra("imageId", imageIdToBeUploaded);
            mServiceIntent.putExtra("imageTitle", image.getTitle());
            mServiceIntent.putExtra("imageDesc", image.getDescription());
            mServiceIntent.putExtra("imageNotes", image.getNotes());
            mServiceIntent.putExtra("imageDate", image.getDate());
            mServiceIntent.putExtra("imageLongitude", image.getLongitude());
            mServiceIntent.putExtra("imageLatitude", image.getLatitude());
            mServiceIntent.putExtra("imageDFov", image.getDFov());
            mServiceIntent.putExtra("pixelsPerMicron", image.getPixelsPerMicron());
            mServiceIntent.putExtra("photoPath_raw", image.getPhotoPath_raw());
            mServiceIntent.putExtra("photoPath_edited", image.getPhotoPath_edited());
            mServiceIntent.putExtra("loggedInUser", username);
            mServiceIntent.putExtra("loggedInUserPWord", pword);

            // Starts the JobIntentService
            UploadIntentService.enqueueUISWork(this,mServiceIntent);
        }
        //enqueueWork(getApplicationContext(), UploadIntentService.class, 2000, work);
        Log.i(TAG, "Completed service");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        toast("All work complete");
    }

    final Handler mHandler = new Handler();

    // Helper for showing tests
    void toast(final CharSequence text)
    {
        mHandler.post(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(GenerateUploadRequestService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
