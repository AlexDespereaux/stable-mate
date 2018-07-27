package com.jaram.jarambuild;

public class TempLegend
{
    //legend description
    private String description;
    //index of sticker in stickerList array
    private String stickerListPosition;

    public TempLegend(String description, String stickerListPosition)
    {
        this.description = description;
        this.stickerListPosition = stickerListPosition;
    }

    public String getDescription()
    {
        return description;
    }

    public String getStickerListPosition()
    {
        return stickerListPosition;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setStickerListPosition(String stickerListPosition)
    {
        this.stickerListPosition = stickerListPosition;
    }

    @Override
    public String toString()
    {
        return new StringBuilder()
                .append("{TempLegend:")
                .append(" Description=").append(description)
                .append(", StickerListPosition=").append(stickerListPosition)
                .append("}").toString();
    }
}
