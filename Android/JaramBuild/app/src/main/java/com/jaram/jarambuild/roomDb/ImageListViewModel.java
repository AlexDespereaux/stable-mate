package com.jaram.jarambuild.roomDb;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.jaram.jarambuild.utils.ImageIdEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ImageListViewModel extends AndroidViewModel
{

    private final LiveData<List<Image>> imageLiveList;

    private final List<Image> imageList;

    private final List<Image> imagesToBeUploadedList;

    private final List<Image> imagesByEmailList;

    private AppDatabase appDatabase;

    public ImageListViewModel(Application application)
    {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        imageLiveList = appDatabase.getImageDao().getAllLiveImages();

        imageList = appDatabase.getImageDao().getAllImages();

        imagesToBeUploadedList = appDatabase.getImageDao().getAllImagesByUploadId(-1);

        imagesByEmailList = appDatabase.getImageDao().getAllImagesByEmail("-1");
    }

    public LiveData<List<Image>> getLiveImageList()
    {
        return imageLiveList;
    }

    public List<Image> getImageList()
    {
        return imageList;
    }

    public List<Image> getImagesToBeUploadedList()
    {
        return imagesToBeUploadedList;
    }

    public List<Image> getImagesByEmailList(String email)
    {
        return imagesByEmailList;
    }

    public void addOneImage(Image image)
    {
        new insertAsyncTask(appDatabase).execute(image);
    }

    public void deleteOneImage(Image image)
    {
        new deleteAsyncTask(appDatabase).execute(image);
    }


    private static class deleteAsyncTask extends AsyncTask<Image, Void, Void>
    {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Image... params)
        {
            db.getImageDao().deleteImage(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Image, Void, Long>
    {

        private AppDatabase db;

        insertAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Long doInBackground(final Image... params)
        {
            return db.getImageDao().insertImage(params[0]);
        }

        @Override
        protected void onPostExecute(Long result) {
            EventBus.getDefault().post(new ImageIdEvent(result));
        }
    }
}
