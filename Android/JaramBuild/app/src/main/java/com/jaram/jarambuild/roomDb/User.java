package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class User
{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "email")
    private String email;
    private String firstName;
    private String lastName;
    //private String userKey;
    private String pWord;

    public User(@NonNull String email, String firstName, String lastName, String pWord)
    {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pWord = pWord;
    }

    @NonNull
    public String getEmail()
    {
        return email;
    }

    public void setEmail(@NonNull String email)
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
/*
    public String getUserKey()
    {
        return userKey;
    }

    public void setUserKey(String userKey)
    {
        this.userKey = userKey;
    }*/

    public String getPWord()
    {
        return pWord;
    }

    public void setPWord(String pWord)
    {
        this.pWord = pWord;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", pWord='" + pWord + '\'' +
                '}';
    }
}
