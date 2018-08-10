package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class User
{
    @PrimaryKey
    @NonNull
    private String email;
    private String firstName;
    private String lastName;
    private String userKey;

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

    public String getUserKey()
    {
        return userKey;
    }

    public void setUserKey(String userKey)
    {
        this.userKey = userKey;
    }
}
