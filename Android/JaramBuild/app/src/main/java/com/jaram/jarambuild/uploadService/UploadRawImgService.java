package com.jaram.jarambuild.uploadService;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;
import android.widget.Toast;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceBroadcastReceiver;
import net.gotev.uploadservice.UploadServiceSingleBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

public class UploadRawImgService extends JobIntentService implements UploadStatusDelegate
{
    String TAG = "RawUpload";
    static final int JOB_ID = 3000;
    private UploadServiceSingleBroadcastReceiver uploadReceiver;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueURISWork(Context context, Intent work)
    {
        enqueueWork(context, UploadRawImgService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        //register upload status listener
        uploadReceiver = new UploadServiceSingleBroadcastReceiver(this);
        uploadReceiver.register(this);
        //get data from intent
        String raw_path = intent.getStringExtra("photoPath_raw");
        String edit_path = intent.getStringExtra("photoPath_edit");
        Log.d(TAG, "Raw path: " + raw_path);
        String username = intent.getStringExtra("loggedInUser");
        String pword = intent.getStringExtra("loggedInUserPWord");
        String serverImgId = intent.getStringExtra("serverImageId");
        String localImgId = intent.getStringExtra("localImageId");

        String uploadUrl = "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/image/raw/" + serverImgId;

        //start upload
        try
        {
            String uploadId =
                    new BinaryUploadRequest(this, uploadUrl)
                            .setBasicAuth(username, pword)
                            .setFileToUpload(raw_path)
                            .addHeader("Content-Type", "image/png")
                            .addHeader("imageId", serverImgId)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(4)
                            .startUpload();
            Log.d(TAG, "Binary File upload started");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

        //start edited image upload intent TODO Move to onCompleted
        if(Integer.parseInt(serverImgId)> 0)
        {
            Intent mServiceIntent = new Intent();
            mServiceIntent.putExtra("loggedInUser", username);
            mServiceIntent.putExtra("loggedInUserPWord", pword);
            mServiceIntent.putExtra("serverImageId", serverImgId);
            mServiceIntent.putExtra("localImageId", localImgId);
            mServiceIntent.putExtra("photoPath_edit", edit_path);

            // Starts the JobIntentService
            UploadEditImgService.enqueueUEISWork(getApplicationContext(), mServiceIntent);
            Log.d(TAG, "enqueueUEISWork call to Edit upload JobIntentService");
        }
    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        uploadReceiver.unregister(this);
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
                Toast.makeText(UploadRawImgService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo)
    {
        Log.d(TAG, "Raw File upload in progress");
    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception)
    {
        Log.d(TAG, "Raw File upload error");
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse)
    {
        Log.d(TAG, "Raw File upload complete");
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo)
    {
        Log.d(TAG, "Raw File upload cancelled");
    }
}
