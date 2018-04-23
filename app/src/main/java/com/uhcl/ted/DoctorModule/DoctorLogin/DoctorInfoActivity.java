package com.uhcl.ted.DoctorModule.DoctorLogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

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
import com.uhcl.ted.Model.Doctor;
import com.uhcl.ted.R;

import java.io.IOException;

public class DoctorInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CHOOSE_IMAGE = 101;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDoctorRef;
    private FirebaseUser user;
    private EditText nameTextField;
    private Spinner specTextField;
    private EditText expTextField;
    private Button saveDoctorInfoBtn;
    private ImageView profileImage;
    private String profileImageUrl;
    private Uri uriProfileImage;
    ProgressBar progressBar;
    String[] specArray = {" ", "Allergist", "Anesthesiologists", "Cardiologists ", "Dermatologists ", "Endocrinologists", "Gastroenterologists "};
    ArrayAdapter<String> adapter;
    String specSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_doctor_info);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        mDoctorRef = FirebaseDatabase.getInstance().getReference();
        nameTextField = (EditText) findViewById(R.id.nameTextFieldDoctor);
        specTextField = (Spinner) findViewById(R.id.specTextFieldDoctor);
        expTextField = (EditText) findViewById(R.id.expTextFieldDoctor);
        saveDoctorInfoBtn = (Button) findViewById(R.id.saveInfoBtnDoctor);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        progressBar = findViewById(R.id.profilePicProgressBar);

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, specArray);
        specTextField.setAdapter(adapter);
        specTextField.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                specSelection = specArray[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        profileImage.setOnClickListener(this);
        saveDoctorInfoBtn.setOnClickListener(this);
    }


    private boolean validateForm() {
        boolean valid = true;

        String name = nameTextField.getText().toString().trim();
        String spec = specSelection;
        String exp = expTextField.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameTextField.setError("Required.");
            valid = false;
        } else {
            nameTextField.setError(null);
        }


        if (TextUtils.isEmpty(exp)) {
            expTextField.setError("Required.");
            valid = false;
        } else {
            expTextField.setError(null);
        }

        return valid;
    }


    public void saveDoctorInfo() {

        if (!validateForm()) {
            return;
        }
        final String email = user.getEmail();
        final String name = nameTextField.getText().toString().trim();
        final String spec = specSelection;
        final String exp = expTextField.getText().toString().trim();
        final String imageUrl = profileImageUrl;
        final String status = "Available";
        final String uid = user.getUid();
        final String type = "doctor";


        Doctor doctor = new Doctor(name, email, spec, exp, imageUrl, status, uid, type);
        mDoctorRef.child("users").child("doctors").child(user.getUid()).setValue(doctor);

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

        StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child(user.getUid()).child("profilepics/" + System.currentTimeMillis() + ".jpg");
        ;
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
                            Toast.makeText(DoctorInfoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (view == saveDoctorInfoBtn) {
            saveDoctorInfo();
            finish();
            startActivity(new Intent(this, DoctorLoginActivity.class));

        }
    }

}
