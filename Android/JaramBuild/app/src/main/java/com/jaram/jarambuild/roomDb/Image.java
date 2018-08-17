package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {@Index("email")},
        foreignKeys = @ForeignKey(entity = User.class,
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
    private int uploadId;
    private String photoPath_raw;
    private String photoPath_edited;
    private String email;

    public Image(String title, String description, String notes, String date, String longitude, String latitude, String dFov, String pixelsPerMicron, int uploadId, String photoPath_raw, String photoPath_edited, String email)
    {
        this.title = title;
        this.description = description;
        this.notes = notes;
        this.date = date;
        this.longitude = longitude;
        this.latitude = latitude;
        this.dFov = dFov;
        this.pixelsPerMicron = pixelsPerMicron;
        this.uploadId = uploadId;
        this.photoPath_raw = photoPath_raw;
        this.photoPath_edited = photoPath_edited;
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

    public int getUploadId()
    {
        return uploadId;
    }

    public void setUploadId(int uploadId)
    {
        this.uploadId = uploadId;
    }

    public String getPhotoPath_raw()
    {
        return photoPath_raw;
    }

    public void setPhotoPath_raw(String photoPath_raw)
    {
        this.photoPath_raw = photoPath_raw;
    }

    public String getPhotoPath_edited()
    {
        return photoPath_edited;
    }

    public void setPhotoPath_edited(String photoPath_edited)
    {
        this.photoPath_edited = photoPath_edited;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Override
    public String toString()
    {
        return "Image{" +
                "imageId=" + imageId +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", notes='" + notes + '\'' +
                ", date='" + date + '\'' +
                ", longitude='" + longitude + '\'' +
                ", latitude='" + latitude + '\'' +
                ", dFov='" + dFov + '\'' +
                ", pixelsPerMicron='" + pixelsPerMicron + '\'' +
                ", uploadId=" + uploadId +
                ", photoPath_raw='" + photoPath_raw + '\'' +
                ", photoPath_edited='" + photoPath_edited + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
