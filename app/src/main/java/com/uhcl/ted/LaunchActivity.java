package com.uhcl.ted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.uhcl.ted.DoctorModule.DoctorLogin.DoctorLoginActivity;
import com.uhcl.ted.PatientModule.PatientLogin.PatientLoginActivity;

public class LaunchActivity extends AppCompatActivity {


    RelativeLayout myLayout;
    AnimationDrawable animationDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        BootstrapButton patientBtn = (BootstrapButton) findViewById(R.id.patientBtn);
        BootstrapButton doctorBtn = (BootstrapButton) findViewById(R.id.doctorBtn);
        SharedPreferences sharedPref = getSharedPreferences("userTypeSelection", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPref.edit();

        patientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("userType", "patient");
                editor.apply();
                finish();
                startActivity(new Intent(LaunchActivity.this, PatientLoginActivity.class));
            }
        });

        doctorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("userType", "doctor");
                editor.apply();
                finish();
                startActivity(new Intent(LaunchActivity.this, DoctorLoginActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sharedPref = getSharedPreferences("userTypeSelection", Context.MODE_PRIVATE);
        String userType = sharedPref.getString("userType", "");

        switch (userType) {
            case "patient":
                finish();
                startActivity(new Intent(LaunchActivity.this, PatientLoginActivity.class));
                break;
            case "doctor":
                finish();
                startActivity(new Intent(LaunchActivity.this, DoctorLoginActivity.class));
        }
    }
}
