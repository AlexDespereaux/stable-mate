package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = User.class,
        parentColumns = "email",
        childColumns = "emailId",
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

    public String getdFov()
    {
        return dFov;
    }

    public void setdFov(String dFov)
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
}
