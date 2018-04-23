package com.uhcl.ted.PatientModule.PatientAppoitnment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.uhcl.ted.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PatientSettingsActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int CHOOSE_IMAGE = 101;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    FirebaseUser user;
    ImageView imageProfile;
    BootstrapEditText nameProfile, ageProfile, phoneProfile;
    TextView emailProfile;
    BootstrapButton saveProfileBtn;
    private ImageView profileImage;
    private String profileImageUrl;
    private Uri uriProfileImage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_settings);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        imageProfile = (ImageView) findViewById(R.id.profileImage);
        emailProfile = (TextView) findViewById(R.id.emailTextFieldPatient);
        nameProfile = (BootstrapEditText) findViewById(R.id.nameTextFieldPatient);
        ageProfile = (BootstrapEditText) findViewById(R.id.ageTextFieldPatient);
        phoneProfile = (BootstrapEditText) findViewById(R.id.phoneTextFieldPatient);
        saveProfileBtn = (BootstrapButton) findViewById(R.id.saveInfoBtn);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.profilePicProgressBar);

        profileImage.setOnClickListener(this);
        saveProfileBtn.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        getUserInfo();
    }

    private void getUserInfo() {
        DatabaseReference currentUserRef = databaseReference.child("users/patients").child(user.getUid());

        currentUserRef.addValueEventListener(new ValueEventListener() {
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

    private boolean saveUserInfo() {

        if (!validateForm()) {
            return false;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference currentUserRef = databaseReference.child("users/patients").child(user.getUid());
        final String email = user.getEmail();
        final String name = nameProfile.getText().toString().trim();
        final String age = ageProfile.getText().toString().trim();
        final String phone = phoneProfile.getText().toString().trim();
        final String type = "patient";

        Map userInfo = new HashMap();
        userInfo.put("name", name);
        userInfo.put("age", age);
        userInfo.put("phone", phone);
        userInfo.put("imageUrl", profileImageUrl);
        currentUserRef.updateChildren(userInfo);


        return true;
    }

    private boolean validateForm() {
        boolean valid = true;

        String name = nameProfile.getText().toString().trim();
        String age = ageProfile.getText().toString().trim();
        String phone = phoneProfile.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameProfile.setError("Required.");
            valid = false;
        } else {
            nameProfile.setError(null);
        }

        if (TextUtils.isEmpty(age)) {
            ageProfile.setError("Required.");
            valid = false;
        } else {
            ageProfile.setError(null);
        }
        int ageN = Integer.parseInt(age);
        if (ageN > 99) {
            ageProfile.setError("Age Can't be more than 99.");
            valid = false;
        } else {
            ageProfile.setError(null);
        }
        if (TextUtils.isEmpty(phone)) {
            phoneProfile.setError("Required.");
            valid = false;
        } else {
            phoneProfile.setError(null);
        }
        if (phone.length() != 10) {
            phoneProfile.setError("should be 10 digits!");
            valid = false;
        } else {
            phoneProfile.setError(null);
        }


        return valid;
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
                            Picasso.get().load(profileImageUrl).fit().centerCrop().into(imageProfile);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(PatientSettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (view == saveProfileBtn) {
            saveUserInfo();
            if (saveUserInfo()) {
                startActivity(new Intent(PatientSettingsActivity.this, PatientHomeActivity.class));
                finish();
            }

        }
    }
}
