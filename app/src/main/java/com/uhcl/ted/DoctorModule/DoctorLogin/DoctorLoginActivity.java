package com.uhcl.ted.DoctorModule.DoctorLogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.uhcl.ted.DoctorModule.DoctorAppointment.DoctorHomeActivity;
import com.uhcl.ted.R;

public class DoctorLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private TextView doctorSignUp;
    private BootstrapButton loginBtn;
    private BootstrapEditText emailTextField;
    private BootstrapEditText passwordTextField;
    private ProgressDialog progressDialog;
    RelativeLayout myLayout;
    AnimationDrawable animationDrawable;
    private FirebaseAuth.AuthStateListener firebaseAuthListner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_login);
        TypefaceProvider.registerDefaultIconSets();

        myLayout = (RelativeLayout) findViewById(R.id.myLayout);
        animationDrawable = (AnimationDrawable) myLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();

        firebaseAuth = FirebaseAuth.getInstance();
        loginBtn = (BootstrapButton) findViewById(R.id.loginBtnDoctor);
        emailTextField = (BootstrapEditText) findViewById(R.id.emailTextFieldDoctor);
        passwordTextField = (BootstrapEditText) findViewById(R.id.passwordTextFieldDoctor);
        doctorSignUp = (TextView) findViewById(R.id.doctorSignUpLink);
        progressDialog = new ProgressDialog(this);

        firebaseAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(DoctorLoginActivity.this, DoctorHomeActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        doctorSignUp.setOnClickListener(this);
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

    private void userLogin() {
        String email = emailTextField.getText().toString().trim();
        String password = passwordTextField.getText().toString().trim();

        if (!validateForm()) {
            return;
        }

        progressDialog.setMessage("Login In ....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if (!task.isSuccessful()) {
                    Toast.makeText(DoctorLoginActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DoctorLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == doctorSignUp) {
            startActivity(new Intent(this, DoctorSignUpActivity.class));
        }

        if (view == loginBtn) {
            userLogin();
        }
    }
}
