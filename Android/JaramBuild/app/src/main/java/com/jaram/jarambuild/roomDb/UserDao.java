package com.jaram.jarambuild.roomDb;

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

    @Query("select * from user")
    List<User> loadAllUsers();

    @Query("select * from user where email = :email")
    User loadUserByEmail(String email);

    @Query("DELETE FROM User")
    void deleteAll();
}
