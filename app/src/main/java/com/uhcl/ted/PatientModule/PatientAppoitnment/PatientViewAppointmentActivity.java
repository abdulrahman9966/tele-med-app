package com.uhcl.ted.PatientModule.PatientAppoitnment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.uhcl.ted.R;
import com.uhcl.ted.VideoChatActivity;

public class PatientViewAppointmentActivity extends AppCompatActivity implements View.OnClickListener {

    private String dNode_key = null;
    private DatabaseReference databaseReference;
    private DatabaseReference doctorReference;
    private ImageView imageDetail;
    private Button callBtn;
    private TextView nameDetail, appDate, appSubject, appNotes, appFileUrl;
    String doctorId, name, date, subject, notes, fileUrl;
    private FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_view_appointment);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getInstance().getCurrentUser();

        imageDetail = (ImageView) findViewById(R.id.imageDetail);
        nameDetail = (TextView) findViewById(R.id.patientName);
        appDate = (TextView) findViewById(R.id.appointmentDate);
        appSubject = (TextView) findViewById(R.id.appSubject);
        appNotes = (TextView) findViewById(R.id.appNotes);
        appFileUrl = (TextView) findViewById(R.id.appFileUrl);
        callBtn = (Button) findViewById(R.id.callBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/patients").child(user.getUid()).child("appointments");

        dNode_key = getIntent().getExtras().getString("doctor_id");
        doctorReference = FirebaseDatabase.getInstance().getReference().child("users/doctors").child(dNode_key).child("appointments");


        databaseReference.child(dNode_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = (String) dataSnapshot.child("name").getValue();
                date = (String) dataSnapshot.child("date").getValue();
                subject = (String) dataSnapshot.child("subject").getValue();
                notes = (String) dataSnapshot.child("notes").getValue();
                fileUrl = (String) dataSnapshot.child("fileUrl").getValue();

                nameDetail.setText(name);
                appDate.setText(date);
                appSubject.setText(subject);
                appNotes.setText(notes);
                appFileUrl.setText(fileUrl);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        callBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == callBtn) {
            finish();
            startActivity(new Intent(PatientViewAppointmentActivity.this, VideoChatActivity.class));
        }
    }
}
