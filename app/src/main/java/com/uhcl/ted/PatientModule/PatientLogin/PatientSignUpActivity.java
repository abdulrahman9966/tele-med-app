package com.uhcl.ted.PatientModule.PatientLogin;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.uhcl.ted.R;

public class PatientSignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private BootstrapButton signUpBtn;
    private BootstrapEditText emailTextField;
    private BootstrapEditText passwordTextField;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;
    private TextView patientSignInLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_signup);
        TypefaceProvider.registerDefaultIconSets();

        firebaseAuth = FirebaseAuth.getInstance();
        signUpBtn = (BootstrapButton) findViewById(R.id.signUpBtnPatient);
        emailTextField = (BootstrapEditText) findViewById(R.id.emailTextFieldPatient);
        passwordTextField = (BootstrapEditText) findViewById(R.id.passwordTextFieldPatient);
        patientSignInLink = (TextView) findViewById(R.id.patientSignInLink);

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    finish();
                    Intent intent = new Intent(PatientSignUpActivity.this, PatientInfoActivity.class);

                    startActivity(intent);
                    return;
                }
            }
        };

        signUpBtn.setOnClickListener(this);
        patientSignInLink.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if (view == signUpBtn) {
            registerPatient();
        }
        if (view == patientSignInLink) {
            finish();
            startActivity(new Intent(this, PatientLoginActivity.class));
        }
    }


    public void registerPatient() {
        final String email = emailTextField.getText().toString().trim();
        final String password = passwordTextField.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "please enter Email", Toast.LENGTH_SHORT).show();
            //stop from execution
            return;
        }

        if (TextUtils.isEmpty(password)) {
            //password is empty
            Toast.makeText(this, "please enter Password", Toast.LENGTH_SHORT).show();
            //stop from execution
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    //user registration is successfull
                    //go to profile activity
                    Toast.makeText(PatientSignUpActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();

                } else {
                    String user_id = firebaseAuth.getCurrentUser().getUid();
                    DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("patients").child(user_id);
                    current_user_db.setValue(true);
                    Toast.makeText(PatientSignUpActivity.this, "user Registered succesfully", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }
}
