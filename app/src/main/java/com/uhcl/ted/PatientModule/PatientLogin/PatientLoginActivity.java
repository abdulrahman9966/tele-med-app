package com.uhcl.ted.PatientModule.PatientLogin;

import android.app.ProgressDialog;
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
import com.uhcl.ted.PatientModule.PatientAppoitnment.PatientHomeActivity;
import com.uhcl.ted.R;

public class PatientLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView patientSignUp;
    private BootstrapButton loginBtn;
    private BootstrapEditText emailTextField;
    private BootstrapEditText passwordTextField;
    private ProgressDialog progressDialog;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_login);
        TypefaceProvider.registerDefaultIconSets();

        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn = (BootstrapButton) findViewById(R.id.loginBtn);
        emailTextField = (BootstrapEditText) findViewById(R.id.emailTextFieldPatient);
        passwordTextField = (BootstrapEditText) findViewById(R.id.passwordTextFieldPatient);
        patientSignUp = (TextView) findViewById(R.id.patientSignUpLink);
        progressDialog = new ProgressDialog(this);

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    finish();
                    Intent intent = new Intent(PatientLoginActivity.this, PatientHomeActivity.class);

                    startActivity(intent);
                    return;
                }
            }
        };

        patientSignUp.setOnClickListener(this);
        loginBtn.setOnClickListener(this);

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
//

    private void userLogin() {
        String email = emailTextField.getText().toString().trim();
        String password = passwordTextField.getText().toString().trim();

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

        progressDialog.setMessage("Login In ....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (!task.isSuccessful()) {
                    Toast.makeText(PatientLoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PatientLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == patientSignUp) {
            startActivity(new Intent(this, PatientSignUpActivity.class));
        }

        if (view == loginBtn) {
            userLogin();

        }
    }
}
