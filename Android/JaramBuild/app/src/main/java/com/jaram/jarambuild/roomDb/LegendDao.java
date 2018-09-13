package com.jaram.jarambuild.roomDb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface LegendDao
{
    @Insert(onConflict = IGNORE)
    long insertLegend(Legend legend);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceLegend(Legend... legends);

    @Delete
    void deleteLegend(Legend legend);

    @Query("select * from legend")
    List<Legend> getAllLegends();

    @Query("select * from legend where imgId = :imgId")
    List<Legend> getAllLegendsByImageId(int imgId);

    @Query("select * from Legend")
    LiveData<List<Legend>> getAllLiveLegends();

    @Query("select * from legend where legendId = :legendId")
    Legend loadLegendById(int legendId);

    @Query("DELETE FROM Legend")
    void deleteAll();
}
