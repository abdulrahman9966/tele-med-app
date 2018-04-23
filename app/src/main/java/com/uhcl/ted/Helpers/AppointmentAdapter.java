package com.uhcl.ted.Helpers;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.uhcl.ted.Model.Appointment;
import com.uhcl.ted.R;

import java.util.List;

/**
 * Created by LENOVO on 08-04-2018.
 */

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentViewHolder> {

    private List<Appointment> itemList;
    private Context context;

    public AppointmentAdapter(List<Appointment> itemList, Context context) {
        this.itemList = itemList;
        this.context = context;
    }

    @Override
    public AppointmentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_appointment, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        AppointmentViewHolder avh = new AppointmentViewHolder(layoutView);
        return avh;
    }


    @Override
    public void onBindViewHolder(AppointmentViewHolder holder, int position) {

        holder.appointmentId.setText(itemList.get(position).getAppointmentId());
        holder.doctorName.setText(itemList.get(position).getDoctorName());
        holder.patientName.setText(itemList.get(position).getPatientName());
        holder.subject.setText(itemList.get(position).getSubject());
        holder.appointmentDate.setText(itemList.get(position).getAppointmentDate());
        boolean accepted = itemList.get(position).isAccepted();
        boolean rejected = itemList.get(position).isRejected();
        if (accepted && !rejected) {
            holder.appStatusBar.setBackgroundColor(Color.parseColor("#5cb85c"));
        }
        if (!accepted && rejected) {
            holder.appStatusBar.setBackgroundColor(Color.parseColor("#d9534f"));
        }
        if (!accepted && !rejected) {
            holder.appStatusBar.setBackgroundColor(Color.parseColor("#5bc0de"));
        }

    }

    @Override
    public int getItemCount() {
        return this.itemList.size();
    }
}
