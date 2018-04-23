package com.uhcl.ted.Helpers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.uhcl.ted.PatientModule.PatientAppoitnment.DetailActivity;
import com.uhcl.ted.PatientModule.PatientAppoitnment.DoctorListActivity;
import com.uhcl.ted.R;

/**
 * Created by LENOVO on 12-04-2018.
 */

public class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView dname;
    public TextView dspec;
    public ImageView dimg;
    public TextView dexp;
    public TextView dstatus;
    String doctorId;

    public DoctorViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        dname = (TextView) itemView.findViewById(R.id.nameList);
        dspec = (TextView) itemView.findViewById(R.id.specList);
        dimg = (ImageView) itemView.findViewById(R.id.imageList);
        dexp = (TextView) itemView.findViewById(R.id.expList);
        dstatus = (TextView) itemView.findViewById(R.id.statusList);
        doctorId = null;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), DetailActivity.class);
        Bundle b = new Bundle();
        b.putString("doctor_id", doctorId);
        Toast.makeText(view.getContext(), doctorId, Toast.LENGTH_SHORT).show();
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}

