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
import com.jaram.jarambuild.models.EditModel;

import java.util.ArrayList;


public class LegendListAdapter extends RecyclerView.Adapter<LegendListAdapter.MyViewHolder>
{

    private LayoutInflater inflater;
    public static ArrayList<EditModel> editModelArrayList;


    public LegendListAdapter(Context ctx, ArrayList<EditModel> editModelArrayList)
    {

        inflater = LayoutInflater.from(ctx);
        LegendListAdapter.editModelArrayList = editModelArrayList;
    }

    @Override
    public LegendListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {

        View view = inflater.inflate(R.layout.activity_legend_listview, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final LegendListAdapter.MyViewHolder holder, final int position)
    {
        //get stickerList
        int[] stickerList = com.jaram.jarambuild.utils.StickerConstants.getStickerList();

        holder.editText.setText(editModelArrayList.get(position).getEditTextValue());
        int imgIndex = editModelArrayList.get(position).getStickerIndex();
        holder.legendImage.setImageResource(stickerList[imgIndex]);
        Log.d("print", "yes");
    }

    @Override
    public int getItemCount()
    {
        return editModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {

        protected EditText editText;
        protected ImageView legendImage;

        public MyViewHolder(View itemView)
        {
            super(itemView);

            editText = (EditText) itemView.findViewById(R.id.editTextDescription);
            legendImage = (ImageView) itemView.findViewById(R.id.stickerLegend);

            editText.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
                {
                    editModelArrayList.get(getAdapterPosition()).setEditTextValue(editText.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable editable)
                {
                }
            });
        }
    }
}
