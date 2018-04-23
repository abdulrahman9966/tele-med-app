package com.uhcl.ted.Helpers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uhcl.ted.AppointmentViewActivity;
import com.uhcl.ted.R;

/**
 * Created by LENOVO on 08-04-2018.
 */

public class AppointmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView appointmentId;
    public TextView patientName;
    public TextView doctorName;
    public TextView subject;
    public TextView appointmentDate;
    public LinearLayout appStatusBar;

    public AppointmentViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        appointmentId = (TextView) itemView.findViewById(R.id.appId);
        doctorName = (TextView) itemView.findViewById(R.id.appListDName);
        patientName = (TextView) itemView.findViewById(R.id.appListPName);
        subject = (TextView) itemView.findViewById(R.id.appListSubject);
        appointmentDate = (TextView) itemView.findViewById(R.id.appListDate);
        appStatusBar = (LinearLayout) itemView.findViewById(R.id.appStatusBar);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), AppointmentViewActivity.class);
        Bundle b = new Bundle();
        b.putString("appointmentId", appointmentId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
