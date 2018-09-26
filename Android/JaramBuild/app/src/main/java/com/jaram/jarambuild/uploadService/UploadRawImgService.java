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

public class UploadRawImgService extends JobIntentService
{
    String TAG = "RawUpload";
    static final int JOB_ID = 3000;
    UploadServiceBroadcastReceiver broadcastReceiver;

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
        //get data from intent
        String raw_path = intent.getStringExtra("photoPath_raw");
        Log.d(TAG, "Raw path: " + raw_path);
        String username = intent.getStringExtra("loggedInUser");
        String pword = intent.getStringExtra("loggedInUserPWord");
        String serverImgId = intent.getStringExtra("serverImageId");
        String localImgId = intent.getStringExtra("localImageId");

        //receiver for image upload state
            broadcastReceiver = new UploadServiceBroadcastReceiver() {
            @Override
            public void onProgress(Context context, UploadInfo uploadInfo) {
                // your implementation
            }

            @Override
            public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                // your implementation
                Log.d(TAG, "Raw file upload error");
            }

            @Override
            public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                Log.d(TAG, "Raw file upload complete");
            }

            @Override
            public void onCancelled(Context context, UploadInfo uploadInfo) {
                Log.d(TAG, "Raw file upload cancelled");
            }
        };

        //register upload status listener
        broadcastReceiver.register(this);

        //start upload
        try
        {
            String uploadId =
                    new BinaryUploadRequest(this, "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/image")
                            .setBasicAuth(username, pword)
                            .setFileToUpload(raw_path)
                            .addHeader("Content-Type", "image/png")
                            .addHeader("imageId", serverImgId)
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(4)
                            .startUpload();
            Log.d(TAG, "Binary File uploaded");
        } catch (Exception exc)
        {
            Log.e("AndroidUploadService", exc.getMessage(), exc);
        }

    }
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        broadcastReceiver.unregister(this);
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
}
