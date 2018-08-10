package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

public interface CalibrationDao
{
    @Insert(onConflict = IGNORE)
    void insertCalibration(Calibration calibration);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceCalibration(Calibration... calibrations);

    @Delete
    void deleteCalibration(Calibration calibration);

    @Query("select * from calibration")
    List<Calibration> loadAllCalibrations();

    @Query("select * from calibration where caliId = :caliId")
    Calibration loadCalibrationById(int caliId);

    @Query("DELETE FROM Calibration")
    void deleteAll();
}
