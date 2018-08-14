package com.jaram.jarambuild.roomDb;

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
    void insertLegend(Legend legend);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceLegend(Legend... legends);

    @Delete
    void deleteLegend(Legend legend);

    @Query("select * from legend")
    List<Legend> loadAllLegends();

    @Query("select * from legend where legendId = :legendId")
    Legend loadLegendById(int legendId);

    @Query("DELETE FROM Legend")
    void deleteAll();
}
