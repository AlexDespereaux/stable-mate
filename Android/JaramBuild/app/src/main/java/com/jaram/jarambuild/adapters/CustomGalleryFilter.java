package com.jaram.jarambuild.adapters;

import android.widget.Filter;

import com.jaram.jarambuild.roomDb.Image;

import java.util.ArrayList;

public class CustomGalleryFilter extends Filter
{
    GalleryListAdapter galleryListAdapter;
    ArrayList<Image> filterList;

    public CustomGalleryFilter(ArrayList<Image> filterList,GalleryListAdapter adapter)
    {
        this.galleryListAdapter=adapter;
        this.filterList=filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        //Check search string is valid
        if(constraint != null && constraint.length() > 0)
        {
            //setUpper
            constraint=constraint.toString().toUpperCase();
            //store results
            ArrayList<Image> filteredImages=new ArrayList<>();

            for (int i=0;i<filterList.size();i++)
            {
                //condition
                if(filterList.get(i).getTitle().toUpperCase().contains(constraint))
                {
                    //add to resultlist
                    filteredImages.add(filterList.get(i));
                }
            }

            results.count=filteredImages.size();
            results.values=filteredImages;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;

        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {

        //galleryListAdapter.image= (ArrayList<Image>) results.values;

        //Refresh
        galleryListAdapter.notifyDataSetChanged();
    }
}
