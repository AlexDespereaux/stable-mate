package com.jaram.jarambuild.uploadService;

import android.app.Application;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.roomDb.Legend;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadIntentService extends JobIntentService
{
    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 2000;
    private String TAG = "UploadIntentService";
    //volley
    private RequestQueue queue;
    static final String REQ_TAG = "UIS";
    //server upload confirmationnumber to be added to images upload
    private String serverImageId;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    static void enqueueUISWork(Context context, Intent work)
    {
        enqueueWork(context, UploadIntentService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent)
    {
        // We have received work to do.  The system or framework is already
        // holding a wake lock for us at this point, so we can just go.
        Log.i("UploadIntentService", "Starting work: " + intent);
        final int imageId = intent.getIntExtra("imageId", -1);
        String fileName = intent.getStringExtra("imageTitle");
        String description = intent.getStringExtra("imageDesc");
        String notes = intent.getStringExtra("imageNotes");
        String date = intent.getStringExtra("imageDate");
        String longitude = intent.getStringExtra("imageLongitude");
        String latitude = intent.getStringExtra("imageLatitude");
        String dFov = intent.getStringExtra("imageDFov");
        String ppm = intent.getStringExtra("pixelsPerMicron");
        final String raw_path = intent.getStringExtra("photoPath_raw");
        final String edit_path = intent.getStringExtra("photoPath_edited");
        final String username = intent.getStringExtra("loggedInUser");
        final String pword = intent.getStringExtra("loggedInUserPWord");

        //initialise Android networking for json upload
        AndroidNetworking.initialize(getApplicationContext());

        //database instance
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());

        //get legend data by imageId
        List<Legend> listOfLegendsFromDb =
                db.getLegendDao().getAllLegendsByImageId(imageId);

        //create JSON object
        JSONObject uploadObj = new JSONObject();
        JSONObject locationObj = new JSONObject();
        JSONArray legendArr = new JSONArray();
        try
        {
            uploadObj.put("filename", fileName);
            uploadObj.put("description", description);
            uploadObj.put("notes", notes);
            uploadObj.put("datetime", Double.parseDouble(date));
            //put location details in location Object
            locationObj.put("latitude", Double.parseDouble(latitude));
            locationObj.put("longitude", Double.parseDouble(longitude));
            //put location object in upload object
            uploadObj.put("location", locationObj);
            uploadObj.put("dFov", Double.parseDouble(dFov));
            uploadObj.put("ppm", Double.parseDouble(ppm));
            for (Legend legend : listOfLegendsFromDb)
            {
                JSONObject singleLegend = new JSONObject();
                singleLegend.put("name", legend.getSymbol());
                singleLegend.put("text", legend.getLegendTxt());
                legendArr.put(singleLegend);
            }
            uploadObj.put("legend", legendArr);
        } catch (JSONException e)
        {
            Log.e(TAG, "JSONException: " + e.getMessage());
        } catch (java.lang.NumberFormatException e)
        {
            Log.e(TAG, "NumberFormatException" + e.getMessage());
            return;
        }
        Log.d(TAG, "Created JSON Object image id: " + imageId);

        //debug jsonObjPretty
        try
        {
            Log.d(TAG, uploadObj.toString(4));
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        //get queue instance
        queue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        //set upload url
        String url = "http://stablemateplus-env.rjhpu9majw.ap-southeast-2.elasticbeanstalk.com/api/image/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, uploadObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            serverImageId = response.getString("imageId");
                            Log.d(TAG, "Server Response: " + response.toString() + ", Local image id :" + imageId +" Server image id: " + serverImageId);
                            if(Integer.parseInt(serverImageId)> 0)
                            {
                                Intent mServiceIntent = new Intent();
                                mServiceIntent.putExtra("loggedInUser", username);
                                mServiceIntent.putExtra("loggedInUserPWord", pword);
                                mServiceIntent.putExtra("serverImageId", serverImageId);
                                mServiceIntent.putExtra("localImageId", Integer.toString(imageId));
                                mServiceIntent.putExtra("photoPath_raw", raw_path);
                                mServiceIntent.putExtra("photoPath_edit", edit_path);

                                // Starts the JobIntentService
                                UploadRawImgService.enqueueURISWork(getApplicationContext(), mServiceIntent);
                                Log.d(TAG, "enqueueURISWork call to Raw upload JobIntentService");
                            }
                        } catch (JSONException e)
                        {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Response: " + error.toString() + "image id :" + imageId);
            }
        }){
            //add Basic Auth header
            @Override
            public Map<String, String> getHeaders()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put(
                        "Authorization",
                        String.format("Basic %s", Base64.encodeToString(
                                String.format("%s:%s", username, pword).getBytes(), Base64.DEFAULT)));
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        jsonObjectRequest.setTag(REQ_TAG);
        queue.add(jsonObjectRequest);

        Log.i("UploadIntentService", "Completed service @ " + SystemClock.elapsedRealtime());
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
                Toast.makeText(UploadIntentService.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
}