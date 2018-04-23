package com.uhcl.ted.PatientModule.PatientAppoitnment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.uhcl.ted.DoctorModule.DoctorAppointment.RequestedAppointmentListActivity;
import com.uhcl.ted.Helpers.AppointmentAdapter;
import com.uhcl.ted.Helpers.DoctorAdapter;
import com.uhcl.ted.Model.Appointment;
import com.uhcl.ted.Model.Doctor;
import com.uhcl.ted.R;

import java.util.ArrayList;

public class DoctorListActivity extends AppCompatActivity {

    private RecyclerView myrecyclerView;
    private DatabaseReference databaseReference;
    private EditText searchTextField;
    private ImageButton searchBtn;
    private RecyclerView.Adapter mDoctorAdapter;
    private RecyclerView.LayoutManager mDoctorLayoutManager;
    SearchView sv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);

        databaseReference = FirebaseDatabase.getInstance().getReference("users/doctors");
        databaseReference.keepSynced(true);

        myrecyclerView = (RecyclerView) findViewById(R.id.myrecyclerView);
        myrecyclerView.setHasFixedSize(true);
        myrecyclerView.setItemAnimator(new DefaultItemAnimator());
        mDoctorLayoutManager = new LinearLayoutManager(DoctorListActivity.this);
        myrecyclerView.setLayoutManager(mDoctorLayoutManager);
        final DoctorAdapter mDoctorAdapter = new DoctorAdapter(getDataDoctors(), DoctorListActivity.this);
        myrecyclerView.setAdapter(mDoctorAdapter);

        sv = (SearchView) findViewById(R.id.mSearch);
        sv.setFocusable(true);
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                mDoctorAdapter.getFilter().filter(query);
                return true;
            }
        });
        getDoctorIds();
    }


    @Override
    protected void onStart() {
        super.onStart();
        sv.setFocusable(true);
        sv.setIconified(false);
        sv.requestFocusFromTouch();
    }

    private ArrayList resultDoctors = new ArrayList<Doctor>();

    private ArrayList<Doctor> getDataDoctors() {
        return resultDoctors;
    }


    private void fetchDoctorInformation(final String doctorKey) {
        DatabaseReference appointmentDb = FirebaseDatabase.getInstance().getReference().child("users/doctors").child(doctorKey);
        appointmentDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = (String) dataSnapshot.child("name").getValue();
                    String spec = (String) dataSnapshot.child("spec").getValue();
                    String exp = (String) dataSnapshot.child("exp").getValue();
                    String status = (String) dataSnapshot.child("status").getValue();
                    String email = (String) dataSnapshot.child("email").getValue();
                    String imageUrl = (String) dataSnapshot.child("imageUrl").getValue();
                    String uid = (String) dataSnapshot.child("uid").getValue();
                    String type = (String) dataSnapshot.child("type").getValue();

                    Doctor obj = new Doctor(name, email, spec, exp, imageUrl, status, uid, type);
                    resultDoctors.add(obj);
                    //  mDoctorAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void getDoctorIds() {
        DatabaseReference doctorRef = FirebaseDatabase.getInstance().getReference().child("users/doctors");
        doctorRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot doctor : dataSnapshot.getChildren()) {
                        fetchDoctorInformation(doctor.getKey());

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
