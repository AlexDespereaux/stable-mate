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
public class Calibration
{
    @PrimaryKey(autoGenerate = true)
    private int caliId;
    private String caliName;
    private String dFov;
    private String pixelsPerMicron;
    private int objectiveLens;
    private int ocularLens;
    private String email;

    public Calibration(String caliName, String dFov, String pixelsPerMicron, int objectiveLens, int ocularLens, String email)
    {
        this.caliName = caliName;
        this.dFov = dFov;
        this.pixelsPerMicron = pixelsPerMicron;
        this.objectiveLens = objectiveLens;
        this.ocularLens = ocularLens;
        this.email = email;
    }

    public int getCaliId()
    {
        return caliId;
    }

    public void setCaliId(int caliId)
    {
        this.caliId = caliId;
    }

    public String getCaliName()
    {
        return caliName;
    }

    public void setCaliName(String caliName)
    {
        this.caliName = caliName;
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

    public int getObjectiveLens()
    {
        return objectiveLens;
    }

    public void setObjectiveLens(int objectiveLens)
    {
        this.objectiveLens = objectiveLens;
    }

    public int getOcularLens()
    {
        return ocularLens;
    }

    public void setOcularLens(int ocularLens)
    {
        this.ocularLens = ocularLens;
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
        return "Calibration{" +
                "caliId=" + caliId +
                ", caliName='" + caliName + '\'' +
                ", dFov='" + dFov + '\'' +
                ", pixelsPerMicron='" + pixelsPerMicron + '\'' +
                ", objectiveLens=" + objectiveLens +
                ", ocularLens=" + ocularLens +
                ", email='" + email + '\'' +
                '}';
    }
}
