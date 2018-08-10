package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

public interface ImageDao
{
    @Insert(onConflict = IGNORE)
    void insertImage(Image image);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceImage(Image... images);

    @Delete
    void deleteImage(Image image);

    @Query("select * from image")
    List<Image> loadAllImages();

    @Query("select * from image where imageId = :imageId")
    Image loadImageById(int imageId);

    @Query("DELETE FROM Image")
    void deleteAll();
}
