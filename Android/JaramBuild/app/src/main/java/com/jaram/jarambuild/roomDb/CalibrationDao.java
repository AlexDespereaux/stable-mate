package com.jaram.jarambuild.roomDb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface CalibrationDao
{
    @Insert(onConflict = IGNORE)
    void insertCalibration(Calibration calibration);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceCalibration(Calibration... calibrations);

    @Delete
    void deleteCalibration(Calibration calibration);

    @Query("select * from calibration")
    LiveData<List<Calibration>> getAllCalibrations();

    @Query("select * from calibration where caliId = :caliId")
    Calibration getCalibrationById(int caliId);

    @Query("select * from calibration where email = :email")
    List<Calibration> getCalibrationListByUser(String email);

    @Query("select * from calibration where email = :email")
    LiveData<List<Calibration>> getLiveCalibrationListByUser(String email);

    @Query("DELETE FROM Calibration")
    void deleteAll();
}
