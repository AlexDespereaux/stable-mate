package com.jaram.jarambuild.roomDb;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.jaram.jarambuild.utils.LegendCreatedEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


public class LegendListViewModel extends AndroidViewModel
{

    private final LiveData<List<Legend>> liveLegendList;
    private final List<Legend> legendList;

    private AppDatabase appDatabase;

    public LegendListViewModel(Application application)
    {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        liveLegendList = appDatabase.getLegendDao().getAllLiveLegends();
        legendList = appDatabase.getLegendDao().getAllLegends();
    }

    public LiveData<List<Legend>> getLiveLegendList()
    {
        return liveLegendList;
    }

    public List<Legend> getLegendList()
    {
        return legendList;
    }

    public void addOneLegend(Legend legend)
    {
        new insertAsyncTask(appDatabase).execute(legend);
    }

    public void deleteOneLegend(Legend legend)
    {
        new deleteAsyncTask(appDatabase).execute(legend);
    }

    private static class deleteAsyncTask extends AsyncTask<Legend, Void, Void>
    {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Legend... params)
        {
            db.getLegendDao().deleteLegend(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Legend, Void, Long>
    {

        private AppDatabase db;

        insertAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Long doInBackground(final Legend... params)
        {
            return db.getLegendDao().insertLegend(params[0]);

        }
        @Override
        protected void onPostExecute(Long result) {
            EventBus.getDefault().post(new LegendCreatedEvent(result));
        }
    }
}
