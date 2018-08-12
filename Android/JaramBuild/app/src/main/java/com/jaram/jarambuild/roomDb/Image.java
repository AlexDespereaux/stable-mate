package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "email",
        childColumns = "email",
        onDelete = CASCADE))
public class Image
{
    @PrimaryKey(autoGenerate = true)
    private int imageId;
    private String title;
    private String description;
    private String notes;
    private String date;
    private String longitude;
    private String latitude;
    private String dFov;
    private String pixelsPerMicron;
    private String email;

    public Image(String title, String description, String notes, String date, String longitude, String latitude, String dFov, String pixelsPerMicron, String email)
    {
        this.title = title;
        this.description = description;
        this.notes = notes;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dFov = dFov;
        this.pixelsPerMicron = pixelsPerMicron;
        this.email = email;
    }

    public int getImageId()
    {
        return imageId;
    }

    public void setImageId(int imageId)
    {
        this.imageId = imageId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getLongitude()
    {
        return longitude;
    }

    public void setLongitude(String longitude)
    {
        this.longitude = longitude;
    }

    public String getLatitude()
    {
        return latitude;
    }

    public void setLatitude(String latitude)
    {
        this.latitude = latitude;
    }

    public String getDFov()
    {
        return dFov;
    }

    public void setDFov(String dFov)
    {
        this.dFov = dFov;
    }

    public String getPixelsPerMicron()
    {
        return pixelsPerMicron;
    }

    public void setPixelsPerMicron(String pixelsPerMicron)
    {
        this.pixelsPerMicron = pixelsPerMicron;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
}
