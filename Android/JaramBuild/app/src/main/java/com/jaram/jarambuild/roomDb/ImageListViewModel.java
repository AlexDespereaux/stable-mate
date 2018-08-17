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

    private final LiveData<List<Image>> imageList;

    private AppDatabase appDatabase;

    public ImageListViewModel(Application application)
    {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());

        imageList = appDatabase.getImageDao().getAllLiveImages();
    }

    public LiveData<List<Image>> getImageList()
    {
        return imageList;
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
