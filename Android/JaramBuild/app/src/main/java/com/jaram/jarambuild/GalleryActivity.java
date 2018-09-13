package com.jaram.jarambuild;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Image;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.utils.TinyDB;

import java.util.List;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener
{
    //db
    private ImageListViewModel imageViewModel;
    private AppDatabase db;

    //buttons
    Button dateSelectorBtn;
    Button clearBtn;
    Button applyBtn;

    //user
    TinyDB tinydb;
    String loggedInUser;

    //log
    String TAG = "GalleryAct";

    //test img
    String pathname = "/storage/emulated/0/Android/data/com.jaram.jarambuild/files/Pictures/EDIT_20180823_021602_3770984489016411492.png";
    ImageView mImageView;

    //list of images
    List<Image> imageListForGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //db
        imageViewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        db = AppDatabase.getDatabase(getApplicationContext());
        List<Image> allImageList;

        //get logged in user for db
        tinydb = new TinyDB(this);
        loggedInUser = tinydb.getString("loggedInAccount");
        Log.d(TAG, "loggedInUser: " + loggedInUser);

        //buttons
        clearBtn = findViewById(R.id.clearBtn);
        applyBtn = findViewById(R.id.applyBtn);
        dateSelectorBtn = findViewById(R.id.startDateBtn);

        //set listeners
        clearBtn.setOnClickListener(this);
        applyBtn.setOnClickListener(this);
        dateSelectorBtn.setOnClickListener(this);

        mImageView = findViewById(R.id.mImageView);

        //test thumbnail generation
        mImageView.setImageBitmap(
                decodeSampledBitmapFromFilePath(pathname, 100, 100));

        //get images and load gallery
        loadGallery(loggedInUser);

    }

    //listener
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.clearBtn:

                break;
            case R.id.applyBtn:

                break;
            case R.id.startDateBtn:

                break;
        }
    }

    //TODO Sub table join when more awake haha
    private void loadGallery(String loggedInUser)
    {
        //get data
        imageListForGallery = imageViewModel.getImageList();
        for(int i = 0; i < imageListForGallery.size(); i++)
        {
            Image oneImg = imageListForGallery.get(i);
            if(oneImg.getEmail() == loggedInUser)
            {
                //TODO get details
            }
        }
    }

    //to avoid loading a large bitmap, images are down sampled, this calculates required sample size
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth)
        {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth)
            {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    //decodeFile(String pathName, BitmapFactory.Options opts)

    public static Bitmap decodeSampledBitmapFromFilePath(String pathname,
                                                         int reqWidth, int reqHeight)
    {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathname, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathname, options);
    }
}
