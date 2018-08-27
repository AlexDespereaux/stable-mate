package com.jaram.jarambuild.utils;

import com.jaram.jarambuild.R;

public class StickerConstants
{
    //****IMPORTANT!!****//
    // if adding images please ensure BOTH arrays have images in the same order!!

    //static array containing Sticker drawables
    private static int[] stickerlist = new int[]{
            //black
            R.drawable.black_arrow,
            R.drawable.black_solid_arrow,
            R.drawable.black_radio,
            R.drawable.black_star,
            //grey
            R.drawable.grey_arrow,
            R.drawable.grey_solid_arrow,
            R.drawable.grey_radio,
            R.drawable.grey_star,
            //white
            R.drawable.white_arrow,
            R.drawable.white_solid_arrow,
            R.drawable.white_radio,
            R.drawable.white_star
    };

    //****IMPORTANT!!****//
    // if adding images please ensure BOTH arrays have images in the same order!!

    //static array of server file paths to stickers
    private static String[] stickerListPaths = new String[]{
            //black
            "black_arrow.png",
            "black_solid_arrow.png",
            "black_radio.png",
            "black_star.png",
            //grey
            "grey_arrow.png",
            "grey_solid_arrow.png",
            "grey_radio.png",
            "grey_star.png",
            //white
            "white_arrow.png",
            "white_solid_arrow.png",
            "white_radio.png",
            "white_star.png"
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
