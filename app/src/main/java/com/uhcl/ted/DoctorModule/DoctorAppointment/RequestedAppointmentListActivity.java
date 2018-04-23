package com.uhcl.ted.DoctorModule.DoctorAppointment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.uhcl.ted.Helpers.AppointmentAdapter;
import com.uhcl.ted.Model.Appointment;
import com.uhcl.ted.R;

import java.util.ArrayList;

public class RequestedAppointmentListActivity extends AppCompatActivity {


    private RecyclerView requestAppRecyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private RecyclerView.Adapter mAppointmentAdapter;
    private RecyclerView.LayoutManager mAppointmentLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requested_appointment_list);



        user = firebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/doctors").child(user.getUid()).child("appointments");
        databaseReference.keepSynced(true);


        requestAppRecyclerView = (RecyclerView) findViewById(R.id.requestAppListrecyclerView);
        requestAppRecyclerView.setHasFixedSize(true);
        requestAppRecyclerView.setNestedScrollingEnabled(true);

        mAppointmentLayoutManager = new LinearLayoutManager(RequestedAppointmentListActivity.this);
        requestAppRecyclerView.setLayoutManager(mAppointmentLayoutManager);
        mAppointmentAdapter = new AppointmentAdapter(getDataAppointments(),RequestedAppointmentListActivity.this);
        requestAppRecyclerView.setAdapter(mAppointmentAdapter);



        getDoctorAppointmentIds();


    }

    private ArrayList resultAppointments = new ArrayList<Appointment>();
    private ArrayList<Appointment> getDataAppointments() {
        return resultAppointments;
    }


    private void fetchAppointmentInformation(final String appointmentKey){

        DatabaseReference appointmentDb = FirebaseDatabase.getInstance().getReference().child("appointments").child(appointmentKey);
        appointmentDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String appointmentId = dataSnapshot.getKey();
                    String doctorName =(String) dataSnapshot.child("doctorName").getValue();
                    String patientName =(String) dataSnapshot.child("patientName").getValue();
                    String doctorId =(String) dataSnapshot.child("doctorId").getValue();
                    String patientId =(String) dataSnapshot.child("patientId").getValue();
                    String subject =(String) dataSnapshot.child("subject").getValue();
                    String appointmentDate =(String) dataSnapshot.child("appointmentDate").getValue();
                    String fileUrl =(String) dataSnapshot.child("fileUrl").getValue();
                    String notes =(String) dataSnapshot.child("notes").getValue();
                    boolean accepted =(boolean) dataSnapshot.child("accepted").getValue();
                    boolean rejected =(boolean) dataSnapshot.child("rejected").getValue();
                    Appointment obj = new Appointment(appointmentId,doctorName,patientName,doctorId,patientId,subject,appointmentDate,fileUrl,notes,accepted,rejected);
                     resultAppointments.add(obj);
                     mAppointmentAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void getDoctorAppointmentIds(){
       DatabaseReference appointmentRef = FirebaseDatabase.getInstance().getReference().child("users/doctors").child(user.getUid()).child("appointments");
       appointmentRef.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               if(dataSnapshot.exists()){
                   for(DataSnapshot appointment: dataSnapshot.getChildren()){
                       fetchAppointmentInformation(appointment.getKey());

                   }
               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
    }


}
