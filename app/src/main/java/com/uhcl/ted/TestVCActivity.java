package com.uhcl.ted;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.Manifest;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.OpentokError;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Subscriber;
import com.opentok.android.SubscriberKit;
import com.uhcl.ted.Model.Appointment;
import com.uhcl.ted.OpenTokHelper.OpenTokConfig;
import com.uhcl.ted.OpenTokHelper.WebServiceCoordinator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TestVCActivity extends AppCompatActivity {
    Appointment appointment;
    String appointmentId;
    String notes;
    private static final String LOG_TAG = TestVCActivity.class.getSimpleName();
    String APIKEY = "46092342";
    String SESSIONID = " ";
    String TOKEN = " ";
    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_vc);
        TypefaceProvider.registerDefaultIconSets();

        myref = FirebaseDatabase.getInstance().getReference();
        getSessionData();
    }


    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences("userTypeSelection", Context.MODE_PRIVATE);
        String userType = sharedPref.getString("userType", "");
        switch (userType) {
            case "patient":

                break;
            case "doctor":

                break;
        }
    }


    public void getSessionData() {


        appointmentId = getIntent().getExtras().getString("appointmentId");
        DatabaseReference sessionRef = myref.child("appointments").child(appointmentId);


        sessionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                appointment = dataSnapshot.getValue(Appointment.class);
                notes = dataSnapshot.child("notes").getValue(String.class);
                SESSIONID = dataSnapshot.child("sessionid").getValue(String.class);
                TOKEN = dataSnapshot.child("token").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Toast.makeText(TestVCActivity.this, appointmentId, Toast.LENGTH_SHORT).show();
        Toast.makeText(TestVCActivity.this, notes, Toast.LENGTH_SHORT).show();
        Toast.makeText(TestVCActivity.this, SESSIONID, Toast.LENGTH_SHORT).show();
        Toast.makeText(TestVCActivity.this, TOKEN, Toast.LENGTH_SHORT).show();
    }
}

