package com.jaram.jarambuild.utils;

import com.jaram.jarambuild.R;

public class StickerConstants
{
    //static array containing Sticker drawables
    private static int[] stickerlist = new int[]{
            R.drawable.black_arrow,
            R.drawable.black_solid_arrow,
            R.drawable.grey_arrow,
            R.drawable.grey_solid_arrow,
            R.drawable.white_arrow,
            R.drawable.white_solid_arrow
    };
    //static array of server file paths to stickers
    private static String[] stickerListPaths = new String[]{
            "black_arrow.png",
            "black_solid_arrow.png",
            "grey_arrow.png",
            "grey_solid_arrow",
            "white_arrow.png",
            "white_solid_arrow"
    };

    //getters
    public static int[] getStickerList()
    {
        return stickerlist;
    }

    public static String[] getStickerListPaths()
    {
        return stickerListPaths;
    }

}
