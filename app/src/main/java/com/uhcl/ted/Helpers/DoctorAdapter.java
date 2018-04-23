package com.uhcl.ted.Helpers;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.squareup.picasso.Picasso;
import com.uhcl.ted.Model.Appointment;
import com.uhcl.ted.Model.Doctor;
import com.uhcl.ted.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LENOVO on 12-04-2018.
 */

public class DoctorAdapter extends RecyclerView.Adapter<DoctorViewHolder> implements Filterable {

    ArrayList<Doctor> itemList, filterList;
    Context context;
    CustomFilter filter;

    public DoctorAdapter(ArrayList<Doctor> itemList, Context context) {
        this.itemList = itemList;
        this.filterList = itemList;
        this.context = context;
    }

    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_model, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        DoctorViewHolder dvh = new DoctorViewHolder(layoutView);
        return dvh;

    }


    @Override
    public void onBindViewHolder(DoctorViewHolder holder, int position) {
        holder.dname.setText(itemList.get(position).getName());
        holder.dspec.setText(itemList.get(position).getSpec());
        holder.dexp.setText(itemList.get(position).getExp());
        //  holder.dstatus.setText(itemList.get(position).getImageUrl());
        holder.dstatus.setText(itemList.get(position).getStatus());
        holder.doctorId = itemList.get(position).getUid();
        Picasso.get().load(itemList.get(position).getImageUrl()).fit().centerCrop().into(holder.dimg);
    }

    //get total number of doctors
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //return filtered object
    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter(filterList, this);
        }

        return filter;
    }


}

