package com.jaram.jarambuild.roomDb;

        import android.app.Application;
        import android.arch.lifecycle.AndroidViewModel;
        import android.arch.lifecycle.LiveData;

        import java.util.List;

public class CaliListByUserViewModel extends AndroidViewModel
{
    private AppDatabase appDatabase;

    public CaliListByUserViewModel(Application application)
    {
        super(application);
        appDatabase = AppDatabase.getDatabase(this.getApplication());
    }

    public LiveData<List<Calibration>> getLiveCalibrationListByUser(String email)
    {
        //return caliList;
        return appDatabase.getCalibrationDao().getLiveCalibrationListByUser(email);
    }
    public List<Calibration> getCalibrationListByUser(String email)
    {
        //return caliList;
        return appDatabase.getCalibrationDao().getCalibrationListByUser(email);
    }
    //TODO: delete calibration by user /// may not be required TBC
}

