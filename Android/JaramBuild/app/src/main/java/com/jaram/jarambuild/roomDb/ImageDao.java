package com.jaram.jarambuild.roomDb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface ImageDao
{
    /*
    @Insert(onConflict = IGNORE)
    void insertImage(Image image);*/

    @Insert(onConflict = IGNORE)
    long insertImage(Image image);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceImage(Image... images);

    @Delete
    void deleteImage(Image image);

    @Query("select * from image where uploadId = :uploadId")
    List<Image> getAllImagesByUploadId(int uploadId);

    @Query("select * from image where email = :email order by date desc")
    List<Image> getAllImagesByEmail(String email);

    @Query("select * from image " +
            "where email = :email and title like :searchText")
    List<Image> getAllImagesByEmailWithFilter(String email, String searchText);

    @Query("select * from image")
    List<Image> getAllImages();

    @Query("select * from Image")
    LiveData<List<Image>> getAllLiveImages();

    @Query("select * from image where imageId = :imageId")
    Image getImageById(int imageId);

    @Query("select * from image where uploadId = :uploadId") // if used, will need to check uploadId does not = -1
    Image getOneImageByUploadId(int uploadId);

    @Query("DELETE FROM Image")
    void deleteAll();

    @Query("UPDATE image SET uploadId = :uploadId WHERE imageId = :imageId")
    void updateUploadId(int uploadId, int imageId);
}
