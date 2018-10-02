package com.jaram.jarambuild.uploadService;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import com.jaram.jarambuild.roomDb.AppDatabase;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceSingleBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

public class UploadEditImgService extends JobIntentService implements UploadStatusDelegate
{
    String TAG = "editUpload";
    static final int JOB_ID = 4000;
    private AppDatabase db;

    private UploadServiceSingleBroadcastReceiver uploadReceiver;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueUEISWork(Context context, Intent work)
    {
        enqueueWork(context, UploadEditImgService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        db = AppDatabase.getDatabase(getApplicationContext());
        //register upload status listener
        uploadReceiver = new UploadServiceSingleBroadcastReceiver(this);
        uploadReceiver.register(this);
        //get data from intent
        String edit_path = intent.getStringExtra("photoPath_edit");
        Log.d(TAG, "Edit path: " + edit_path);
        String username = intent.getStringExtra("loggedInUser");
        String pword = intent.getStringExtra("loggedInUserPWord");
        String serverImgId = intent.getStringExtra("serverImageId");
        String localImgId = intent.getStringExtra("localImageId");

        //start upload
        try
        {
            String uploadId =
                    new BinaryUploadRequest(this, "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/image")
                            .setBasicAuth(username, pword)
                            .setFileToUpload(edit_path)
                            .addHeader("Content-Type", "image/png")
                            .addHeader("imageId", serverImgId)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(4)
                            .startUpload();
            Log.d(TAG, "Edit Binary File upload started");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

        //edit upload status
        db.getImageDao().updateUploadId(Integer.parseInt(serverImgId), Integer.parseInt(localImgId));
        Log.d(TAG, "Database updated image id:" + localImgId + ", new upload id: " + serverImgId);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        uploadReceiver.unregister(this);
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
                Toast.makeText(UploadEditImgService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo)
    {
        Log.d(TAG, "Edit File upload in progress");
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception)
    {
        Log.d(TAG, "Edit File upload error");
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse)
    {
        Log.d(TAG, "Edit File upload complete");
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo)
    {
        Log.d(TAG, "Edit File upload cancelled");
    }
}
