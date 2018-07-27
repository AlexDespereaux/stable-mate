package com.jaram.jarambuild;

public class TempLegend
{
    String drawablePath;
    int viewPosition;

    public TempLegend(String drawablePath, int viewPosition)
    {
        this.drawablePath = drawablePath;
        this.viewPosition = viewPosition;
    }

    public TempLegend(String drawablePath)
    {
        this.drawablePath = drawablePath;
        this.viewPosition = 0;
    }

    public String getDrawablePath()
    {
        return drawablePath;
    }

    public int getViewPosition()
    {
        return viewPosition;
    }

    public void setDrawablePath(String drawablePath)
    {
        this.drawablePath = drawablePath;
    }

    public void setViewPosition(int viewPosition)
    {
        this.viewPosition = viewPosition;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{TempLegend:")
                .append(" DrawablePath=").append(drawablePath)
                .append(", position=").append(viewPosition)
                .append("}").toString();
    }

}
