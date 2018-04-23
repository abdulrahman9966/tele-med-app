package com.uhcl.ted.PatientModule.PatientAppoitnment;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.uhcl.ted.DoctorModule.DoctorAppointment.DoctorHomeActivity;
import com.uhcl.ted.PatientModule.PatientLogin.PatientLoginActivity;
import com.uhcl.ted.R;

public class PatientHomeActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FirebaseUser user;
    private String userID;
    private CardView showDoctorsCard, myAppointementsCard, editProfileCard, aboutCard, logoutCard;
    ImageView imageProfile;
    TextView nameProfile;
    TextView ageProfile;
    TextView emailProfile;
    TextView phoneProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home);
        TypefaceProvider.getRegisteredIconSets();
        SharedPreferences pref = getSharedPreferences("PREF", MODE_PRIVATE);
        pref.edit().putString("type", "patient").apply();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        OneSignal.startInit(getApplicationContext()).init();
        OneSignal.setEmail(user.getEmail());

        showDoctorsCard = (CardView) findViewById(R.id.showDoctorsCard);
        myAppointementsCard = (CardView) findViewById(R.id.myAppointmentsCard);
        editProfileCard = (CardView) findViewById(R.id.editProfileCard);
        aboutCard = (CardView) findViewById(R.id.aboutCard);
        logoutCard = (CardView) findViewById(R.id.logoutCard);
        imageProfile = (ImageView) findViewById(R.id.profileImage);
        nameProfile = (TextView) findViewById(R.id.profileName);
        ageProfile = (TextView) findViewById(R.id.profileAge);
        emailProfile = (TextView) findViewById(R.id.profileEmail);
        phoneProfile = (TextView) findViewById(R.id.profilePhone);

        String userID = user.getUid();
        OneSignal.sendTag("user_id", userID);
        loadCurrentPatientData();

        showDoctorsCard.setOnClickListener(this);
        myAppointementsCard.setOnClickListener(this);
        editProfileCard.setOnClickListener(this);
        aboutCard.setOnClickListener(this);
        logoutCard.setOnClickListener(this);
    }

    private void loadCurrentPatientData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference.child("users/patients").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    String name = ds.child("name").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    String age = ds.child("age").getValue(String.class);
                    String phone = ds.child("phone").getValue(String.class);
                    String profileUrl = ds.child("imageUrl").getValue(String.class);
                    Picasso.get().load(profileUrl).fit().centerCrop().into(imageProfile);

                    nameProfile.setText(name);
                    ageProfile.setText(age);
                    emailProfile.setText(email);
                    phoneProfile.setText(phone);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }


    public void showAbout() {
        final Dialog dialog = new Dialog(PatientHomeActivity.this);
        dialog.setContentView(R.layout.dialog_about);
        dialog.show();
        BootstrapButton closeBtn = (BootstrapButton) dialog.findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        if (view == showDoctorsCard) {
            startActivity(new Intent(this, DoctorListActivity.class));
            return;
        }
        if (view == myAppointementsCard) {
            startActivity(new Intent(this, PatientAppointmentListActivity.class));
            return;
        }
        if (view == editProfileCard) {
            startActivity(new Intent(this, PatientSettingsActivity.class));
            return;
        }
        if (view == aboutCard) {
            final Dialog dialog = new Dialog(PatientHomeActivity.this);
            dialog.setContentView(R.layout.dialog_about);
            dialog.show();
            BootstrapButton closeBtn = (BootstrapButton) dialog.findViewById(R.id.closeBtn);
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
        if (view == logoutCard) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, PatientLoginActivity.class));
            finish();
            return;
        }
    }
}
