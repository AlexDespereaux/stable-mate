package com.jaram.jarambuild;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.jaram.jarambuild.adapters.GalleryListAdapter;
import com.jaram.jarambuild.models.GalleryModel;
import com.jaram.jarambuild.roomDb.AppDatabase;
import com.jaram.jarambuild.roomDb.Image;
import com.jaram.jarambuild.roomDb.ImageListViewModel;
import com.jaram.jarambuild.utils.TinyDB;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity implements View.OnClickListener
{
    //db
    private ImageListViewModel imageViewModel;
    private AppDatabase db;

    //buttons
    Button clearBtn;
    Button applyBtn;

    //search
    EditText searchEt;
    String searchString;

    //user
    TinyDB tinydb;
    String loggedInUser;

    //log
    String TAG = "GalleryAct";

    //list of images
    public List<Image> imageListForGallery;

    //recycler view
    private RecyclerView galleryRecyclerView;
    private GalleryListAdapter galleryListAdapter;
    public ArrayList<GalleryModel> galleryModelArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        //db
        imageViewModel = ViewModelProviders.of(this).get(ImageListViewModel.class);
        db = AppDatabase.getDatabase(getApplicationContext());

        //get logged in user for db
        tinydb = new TinyDB(this);
        loggedInUser = tinydb.getString("loggedInAccount");
        Log.d(TAG, "loggedInUser: " + loggedInUser);

        //buttons
        clearBtn = findViewById(R.id.clearBtn);
        applyBtn = findViewById(R.id.applyBtn);

        //search
        searchEt = findViewById(R.id.searchEt);

        //set listeners
        clearBtn.setOnClickListener(this);
        applyBtn.setOnClickListener(this);

        initRecycler();
    }

    //listener
    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.clearBtn:
                searchEt.getText().clear();
                galleryListAdapter.notifyDataSetChanged();
                break;
            case R.id.applyBtn:
                searchString = searchEt.getText().toString();
                galleryListAdapter.notifyDataSetChanged();
                break;
        }
    }

    private ArrayList<GalleryModel> populateGalleryList()
    {
        Log.d(TAG, "Getting imageListForGallery");
        imageListForGallery = db.getImageDao().getAllImagesByEmail(loggedInUser);
        ArrayList<GalleryModel> list = new ArrayList<>();

        Log.d(TAG, "imageListForGalleryLength: " + imageListForGallery.size());
        for (Image image : imageListForGallery)
        {
            GalleryModel galleryModel = new GalleryModel();
            galleryModel.setImageId(image.getImageId());
            galleryModel.setTitle(image.getTitle());
            galleryModel.setDescription(image.getDescription());
            galleryModel.setNotes(image.getNotes());
            galleryModel.setDate(image.getDate());
            galleryModel.setLongitude(image.getLongitude());
            galleryModel.setLatitude(image.getLatitude());
            galleryModel.setDFov(image.getDFov());
            galleryModel.setPixelsPerMicron(image.getPixelsPerMicron());
            galleryModel.setUploadId(image.getUploadId());
            galleryModel.setPhotoPath_raw(image.getPhotoPath_raw());
            galleryModel.setPhotoPath_edited(image.getPhotoPath_edited());
            list.add(galleryModel);
            Log.d(TAG, "listcheck");
            Log.d(TAG, image.toString());
        }
        Log.d(TAG, "list populated ");
        return list;
    }

    public void initRecycler()
    {
        //Recycler View
        galleryRecyclerView = findViewById(R.id.galleryRecycler);
        galleryModelArrayList = populateGalleryList();
        galleryListAdapter = new GalleryListAdapter(this, galleryModelArrayList);
        galleryRecyclerView.setAdapter(galleryListAdapter);
        galleryRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }
}
