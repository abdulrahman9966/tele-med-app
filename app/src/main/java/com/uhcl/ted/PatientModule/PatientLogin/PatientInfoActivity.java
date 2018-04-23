package com.uhcl.ted.PatientModule.PatientLogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uhcl.ted.Model.Patient;
import com.uhcl.ted.R;

import java.io.IOException;

public class PatientInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;


    ProgressBar progressBar;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private BootstrapEditText nameTextField;
    private BootstrapEditText ageTextField;
    private BootstrapEditText phoneTextField;
    private BootstrapButton savePatientInfoBtn;
    private ImageView profileImage;
    private String profileImageUrl;
    private Uri uriProfileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_info);
        TypefaceProvider.registerDefaultIconSets();

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        nameTextField = (BootstrapEditText) findViewById(R.id.nameTextFieldPatient);
        ageTextField = (BootstrapEditText) findViewById(R.id.ageTextFieldPatient);
        phoneTextField = (BootstrapEditText) findViewById(R.id.phoneTextFieldPatient);
        savePatientInfoBtn = (BootstrapButton) findViewById(R.id.saveInfoBtn);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.profilePicProgressBar);


        profileImage.setOnClickListener(this);
        savePatientInfoBtn.setOnClickListener(this);
    }


    private boolean validateForm() {
        boolean valid = true;
        String name = nameTextField.getText().toString().trim();
        String age = ageTextField.getText().toString().trim();
        String phone = phoneTextField.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameTextField.setError("Required.");
            valid = false;
        } else {
            nameTextField.setError(null);
        }

        if (TextUtils.isEmpty(age)) {
            ageTextField.setError("Required.");
            valid = false;
        } else {
            ageTextField.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            phoneTextField.setError("Required.");
            valid = false;
        } else {
            phoneTextField.setError(null);
        }

        return valid;
    }


    public void savePatientInfo() {

        if (!validateForm()) {
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        final String email = user.getEmail();
        final String name = nameTextField.getText().toString().trim();
        final String age = ageTextField.getText().toString().trim();
        final String phone = phoneTextField.getText().toString().trim();
        final String imageUrl = profileImageUrl;
        final String type = "patient";

        Patient patient = new Patient(name, email, age, phone, imageUrl, type);
        databaseReference.child("users").child("patients").child(user.getUid()).setValue(patient);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                profileImage.setImageBitmap(bitmap);
                uplaodProfileImageToFirebaseStorage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uplaodProfileImageToFirebaseStorage() {

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference("profilepics/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {

            progressBar.setVisibility(View.VISIBLE);
            profileImageRef.putFile(uriProfileImage)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressBar.setVisibility(View.GONE);
                            profileImageUrl = taskSnapshot.getDownloadUrl().toString();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PatientInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "select profile image"), CHOOSE_IMAGE);
    }


    @Override
    public void onClick(View view) {
        if (view == profileImage) {
            showImageChooser();
        }
        if (view == savePatientInfoBtn) {
            savePatientInfo();
            finish();
            startActivity(new Intent(this, PatientLoginActivity.class));
        }
    }
}


