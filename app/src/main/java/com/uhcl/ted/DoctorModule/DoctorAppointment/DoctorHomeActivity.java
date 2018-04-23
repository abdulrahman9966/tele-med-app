package com.uhcl.ted.DoctorModule.DoctorAppointment;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
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
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.squareup.picasso.Picasso;
import com.uhcl.ted.DoctorModule.DoctorLogin.DoctorLoginActivity;
import com.uhcl.ted.R;

import org.json.JSONObject;

public class DoctorHomeActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    BootstrapButton showPAppointmentsBtn, logoutBtn;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser user;
    Switch availabilitySwitch;
    TextView nameProfile, expProfile, specProfile, emailProfile;
    ImageView imageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        TypefaceProvider.registerDefaultIconSets();
        OneSignal.startInit(this).setNotificationOpenedHandler((OneSignal.NotificationOpenedHandler) new ExampleNotificationOpenedHandler()).init();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        showPAppointmentsBtn = (BootstrapButton) findViewById(R.id.showPAppointmentBtn);
        logoutBtn = (BootstrapButton) findViewById(R.id.logoutBtn);
        availabilitySwitch = (Switch) findViewById(R.id.availabilitySwitch);
        nameProfile = (TextView) findViewById(R.id.profileName);
        emailProfile = (TextView) findViewById(R.id.profileEmail);
        specProfile = (TextView) findViewById(R.id.profileSpec);
        expProfile = (TextView) findViewById(R.id.profileExp);
        imageProfile = (ImageView) findViewById(R.id.profileImage);


        String userID = user.getUid();
        OneSignal.sendTag("user_id", userID);
        OneSignal.setEmail(user.getEmail());

        loadCurrentDoctorData();

        availabilitySwitch.setOnCheckedChangeListener(this);
        logoutBtn.setOnClickListener(this);
        showPAppointmentsBtn.setOnClickListener(this);
    }


    private void loadCurrentDoctorData() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference.child("users/doctors").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot ds) {
                    String name = ds.child("name").getValue(String.class);
                    String email = ds.child("email").getValue(String.class);
                    String spec = ds.child("spec").getValue(String.class);
                    String exp = ds.child("exp").getValue(String.class);
                    String profileUrl = ds.child("imageUrl").getValue(String.class);
                    Picasso.get().load(profileUrl).fit().centerCrop().into(imageProfile);

                    nameProfile.setText(name);
                    specProfile.setText(spec);
                    emailProfile.setText(email);
                    expProfile.setText(exp);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }

    @Override
    public void onClick(View view) {
        if (view == logoutBtn) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, DoctorLoginActivity.class));
        }

        if (view == showPAppointmentsBtn) {
            Intent intent = new Intent(DoctorHomeActivity.this, RequestedAppointmentListActivity.class);
            intent.putExtra("patientOrDoctor", "doctors");
            startActivity(intent);
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference swithRef = databaseReference.child("users/doctors").child(user.getUid());
        swithRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String status = dataSnapshot.child("status").getValue(String.class);
                switch (status) {
                    case "Available":
                        availabilitySwitch.setChecked(true);
                        break;
                    case "Not Available":
                        availabilitySwitch.setChecked(false);
                        break;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (availabilitySwitch.isChecked()) {
            databaseReference.child("users/doctors").child(user.getUid()).child("status").setValue("Available");
        } else {
            databaseReference.child("users/doctors").child(user.getUid()).child("status").setValue("Not Available");
        }
    }




    // Fires when a notificaiton is opened by tapping on it or one is received while the app is runnning.
    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String customKey;

            if (data != null) {
                customKey = data.optString("customkey", null);
                if (customKey != null)
                    Log.i("OneSignalExample", "customkey set with value: " + customKey);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
//            // The following can be used to open an Activity of your choice.
//            // Replace - getApplicationContext() - with any Android Context.
            // The following can be used to open an Activity of your choose.

            Intent intent = new Intent(getApplication(), RequestedAppointmentListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // Follow the insturctions in the link below to prevent the launcher Activity from starting.
            // https://documentation.onesignal.com/docs/android-notification-customizations#changing-the-open-action-of-a-notification
        }

    }
}
