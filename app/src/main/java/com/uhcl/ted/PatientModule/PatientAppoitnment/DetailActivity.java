package com.uhcl.ted.PatientModule.PatientAppoitnment;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uhcl.ted.R;

public class DetailActivity extends AppCompatActivity implements View.OnClickListener {

    private String dNode_key = null;
    private DatabaseReference databaseReference;
    private ImageView imageDetail;
    private BootstrapButton makeAppointmentBtn;
    private TextView nameDetail, specDetail, expDetail, statusDetail;
    String doctorId, name, exp, spec, status, imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        imageDetail = (ImageView) findViewById(R.id.imageDetail);
        nameDetail = (TextView) findViewById(R.id.nameDetail);
        specDetail = (TextView) findViewById(R.id.specDetail);
        expDetail = (TextView) findViewById(R.id.expDetail);
        statusDetail = (TextView) findViewById(R.id.statusDetail);
        makeAppointmentBtn = (BootstrapButton) findViewById(R.id.makeAppointmentBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference("users/doctors");

        dNode_key = getIntent().getExtras().getString("doctor_id");
        Toast.makeText(DetailActivity.this, dNode_key, Toast.LENGTH_SHORT);

        databaseReference.child(dNode_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("name").getValue();
                spec = (String) dataSnapshot.child("spec").getValue();
                exp = (String) dataSnapshot.child("exp").getValue();
                status = (String) dataSnapshot.child("status").getValue();
                imgUrl = (String) dataSnapshot.child("imageUrl").getValue();
                doctorId = (String) dataSnapshot.child("uid").getValue();

                nameDetail.setText(name);
                specDetail.setText(spec);
                expDetail.setText(exp);
                statusDetail.setText(status);
                //   changeStatusColor();
                Picasso.get().load(imgUrl).into(imageDetail);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        makeAppointmentBtn.setOnClickListener(this);
    }

    public void changeStatusColor() {
        if (status.equals("Not Available")) {
            statusDetail.setTextColor(Color.RED);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == makeAppointmentBtn) {
            Intent doctorAppointmentIntent = new Intent(DetailActivity.this, DoctorAppointmentActivity.class);
            doctorAppointmentIntent.putExtra("doctor_id", doctorId);
            doctorAppointmentIntent.putExtra("doctor_name", name);
            startActivity(doctorAppointmentIntent);
        }
    }
}
