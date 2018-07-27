package com.jaram.jarambuild.utils;

import com.jaram.jarambuild.R;

public class StickerConstants
{
    //static array containing Sticker drawables
    private static int[] stickerlist = new int[]{
            R.drawable.aa,
            R.drawable.bb
    };
    //static array of server file paths to stickers
    private static String[] stickerListPaths = new String[]{
            "aa.png",
            "bb.png"
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
