package com.uhcl.ted.DoctorModule.DoctorLogin;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.uhcl.ted.PatientModule.PatientLogin.PatientInfoActivity;
import com.uhcl.ted.PatientModule.PatientLogin.PatientSignUpActivity;
import com.uhcl.ted.R;

public class DoctorSignUpActivity extends AppCompatActivity implements View.OnClickListener {


    private FirebaseAuth firebaseAuth;
    private BootstrapButton signUpBtn;
    private BootstrapEditText emailTextField;
    private BootstrapEditText passwordTextField;
    RelativeLayout myLayout;
    AnimationDrawable animationDrawable;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private TextView doctorSignInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_signup);
        TypefaceProvider.getRegisteredIconSets();

        myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        firebaseAuth = FirebaseAuth.getInstance();
        signUpBtn = (BootstrapButton) findViewById(R.id.doctorSignUpBtn);
        emailTextField = (BootstrapEditText) findViewById(R.id.emailTextFieldDoctor);
        passwordTextField = (BootstrapEditText) findViewById(R.id.passwordTextFieldDoctor);
        doctorSignInLink = (TextView) findViewById(R.id.doctorSignInLink);

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(DoctorSignUpActivity.this, DoctorInfoActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        signUpBtn.setOnClickListener(this);
        doctorSignInLink.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthListner);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(firebaseAuthListner);

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailTextField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailTextField.setError("Required.");
            valid = false;
        } else {
            emailTextField.setError(null);
        }

        String password = passwordTextField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordTextField.setError("Required.");
            valid = false;
        } else {
            passwordTextField.setError(null);
        }

        return valid;
    }


    public void registerDoctor() {
        final String email = emailTextField.getText().toString().trim();
        String password = passwordTextField.getText().toString().trim();


        if (!validateForm()) {
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    //user registration is successfull
                    //go to profile activity
                    Toast.makeText(DoctorSignUpActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();


                } else {
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("doctors").child(user_id);
                    current_user_db.setValue(true);
                    Toast.makeText(DoctorSignUpActivity.this, "user Registered succesfully", Toast.LENGTH_SHORT).show();

                }


            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view == signUpBtn) {
            registerDoctor();
        }

        if (view == doctorSignInLink) {
            finish();
            startActivity(new Intent(this, DoctorLoginActivity.class));
            return;
        }
    }
}
