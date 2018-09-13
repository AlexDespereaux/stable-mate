package com.jaram.jarambuild.roomDb;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.IGNORE;

@Dao
public interface UserDao
{
    @Insert(onConflict = IGNORE)
    void insertUser(User user);

    @Insert(onConflict = IGNORE)
    void insertOrReplaceUsers(User... users);

    @Delete
    void deleteUser(User user);

    @Query("select * from User")
    LiveData<List<User>> getAllUsers();

    @Query("select * from User")
    List<User> getAllUsers2();

    @Query("select * from User where email = :email")
    User getUserbyId(String email);

    @Query("DELETE FROM User")
    void deleteAll();

}
