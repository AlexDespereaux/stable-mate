package com.jaram.jarambuild.roomDb;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class CaliListViewModel extends AndroidViewModel
{

    private final LiveData<List<Calibration>> caliList;
    private String loggedInUser;

    private AppDatabase appDatabase;

    public CaliListViewModel(Application application)
    {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
        caliList = appDatabase.getCalibrationDao().getAllCalibrations();
    }

    public LiveData<List<Calibration>> getCalibrationList()
    {
        return caliList;
    }

    public void addOneCalibration(Calibration calibration)
    {
        new insertAsyncTask(appDatabase).execute(calibration);
    }

    public void deleteOneCalibration(Calibration calibration)
    {
        new deleteAsyncTask(appDatabase).execute(calibration);
    }

    private static class deleteAsyncTask extends AsyncTask<Calibration, Void, Void>
    {

        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Calibration... params)
        {
            db.getCalibrationDao().deleteCalibration(params[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Calibration, Void, Void>
    {

        private AppDatabase db;
        insertAsyncTask(AppDatabase appDatabase)
        {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Calibration... params)
        {
            db.getCalibrationDao().insertCalibration(params[0]);
            return null;
        }
    }
}
