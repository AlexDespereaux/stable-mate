package com.jaram.jarambuild.roomDb;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(indices = {@Index("imgId")},
        foreignKeys = @ForeignKey(entity = Image.class,
        parentColumns = "imageId",
        childColumns = "imgId",
        onDelete = CASCADE))

public class Legend
{
    @PrimaryKey(autoGenerate = true)
    private int legendId;
    private String symbol;
    private String legendTxt;
    private int imgId;

    public Legend(String symbol, String legendTxt, int imgId)
    {
        this.symbol = symbol;
        this.legendTxt = legendTxt;
        this.imgId = imgId;
    }

    public int getLegendId()
    {
        return legendId;
    }

    public void setLegendId(int legendId)
    {
        this.legendId = legendId;
    }

    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

    public String getLegendTxt()
    {
        return legendTxt;
    }

    public void setLegendTxt(String legendTxt)
    {
        this.legendTxt = legendTxt;
    }

    public int getImgId()
    {
        return imgId;
    }

    public void setImgId(int imgId)
    {
        this.imgId = imgId;
    }
}
