package com.jaram.jarambuild.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaram.jarambuild.R;
import com.jaram.jarambuild.models.ViewListModel;

import java.util.ArrayList;

public class ViewActListAdapter extends RecyclerView.Adapter<ViewActListAdapter.MyViewHolder>
{
    private LayoutInflater inflater;
    public static ArrayList<ViewListModel> viewModelArrayList;

    public ViewActListAdapter(Context ctx, ArrayList<ViewListModel> viewModelArrayList)
    {

        inflater = LayoutInflater.from(ctx);
        ViewActListAdapter.viewModelArrayList = viewModelArrayList;
    }

    @Override
    public ViewActListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.activity_legendview_listview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewActListAdapter.MyViewHolder holder, int position)
    {
        //get stickerList
        int[] stickerList = com.jaram.jarambuild.utils.StickerConstants.getStickerList();

        holder.legendTxt.setText(viewModelArrayList.get(position).getLegendTextValue());
        int imgIndex = viewModelArrayList.get(position).getStickerIndex();
        holder.viewLegendImage.setImageResource(stickerList[imgIndex]);
        Log.d("print", "yes");
    }

    @Override
    public int getItemCount()
    {
        return viewModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        protected TextView legendTxt;
        protected ImageView viewLegendImage;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            legendTxt = itemView.findViewById(R.id.legendTxt);
            viewLegendImage = itemView.findViewById(R.id.stickerLegendImg);

        }
    }
}
