package com.uhcl.ted.PatientModule.PatientAppoitnment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.uhcl.ted.Model.Appointment;
import com.uhcl.ted.R;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Scanner;

public class DoctorAppointmentActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    private static final String TAG = "MyActivity";
    private static final int REQUEST_CODE_DOC = 101;
    private TextView dateTextView, fileNameTextView, doctorNameTextField, patientNameTextField;
    private BootstrapEditText subTextField, notesTextField;
    int day, month, year, hour, minute;
    int yearFinal, dayFinal, monthFinal, hourFinal, minuteFinal;
    private BootstrapButton uploadBtn, confirmAppointmentBtn;
    Uri uriDocument;
    String fileName;
    Uri downloadUrl;
    DatabaseReference databaseReference;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String pname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_appointment);

        doctorNameTextField = (TextView) findViewById(R.id.doctorNameTextField);
        patientNameTextField = (TextView) findViewById(R.id.patientNameTextField);
        uploadBtn = (BootstrapButton) findViewById(R.id.uploadBtn);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        fileNameTextView = (TextView) findViewById(R.id.fileNameTextView);
        subTextField = (BootstrapEditText) findViewById(R.id.subjectTextField);
        notesTextField = (BootstrapEditText) findViewById(R.id.notesTextField);
        confirmAppointmentBtn = (BootstrapButton) findViewById(R.id.confirmAppointmetBtn);
        String fileUrl = fileName;
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = firebaseAuth.getInstance().getCurrentUser();


        dateTextView.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        confirmAppointmentBtn.setOnClickListener(this);


    }


    @Override
    protected void onStart() {
        super.onStart();


        doctorNameTextField.setText(getIntent().getExtras().getString("doctor_name"));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = databaseReference.child("users").child("patients").child(user.getUid());

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pname = (String) dataSnapshot.child("name").getValue();
                patientNameTextField.setText(pname);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal = i;
        monthFinal = i1 + 1;
        dayFinal = i2;

        Calendar cal = Calendar.getInstance();
        hour = cal.get(Calendar.HOUR);
        minute = cal.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(DoctorAppointmentActivity.this, DoctorAppointmentActivity.this, hour, minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        dateTextView.setText(monthFinal + "/" + dayFinal + "/" + yearFinal + " " + hourFinal + ":" + minuteFinal);
    }


    public void uploadFileToFirebase() {

        if (uriDocument != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading File...");
            dialog.show();

            storage = FirebaseStorage.getInstance();
            storageRef = storage.getReference();
            firebaseAuth = FirebaseAuth.getInstance();
            user = firebaseAuth.getCurrentUser();


            fileName = "Record" + "_" + System.currentTimeMillis() + "." + getFileExtension(uriDocument);
            fileNameTextView.setText(fileName);
            StorageReference docRef = storageRef.child(user.getUid()).child("docs/" + fileName);

            UploadTask uploadTask = docRef.putFile(uriDocument);


            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d("uploadFail", "" + exception);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    // sendNotification("upload backup", 1);
                    dialog.dismiss();
                    downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.d("downloadUrl", "" + downloadUrl);
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded" + (int) progress + "%");
                }
            });

        }

    }


    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_DOC && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriDocument = data.getData();
            Toast.makeText(DoctorAppointmentActivity.this, uriDocument.toString(), Toast.LENGTH_LONG).show();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriDocument);
                //  profileImage.setImageBitmap(bitmap);

                uploadFileToFirebase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDocumentChooser() {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip",
                        "image/jpeg", "image/png",
                        "audio/mpeg4-generic", "audio/mpeg"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), REQUEST_CODE_DOC);
    }

    private boolean validateForm() {
        boolean valid = true;

        String subject = subTextField.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();
        String notes = notesTextField.getText().toString().trim();

        if (TextUtils.isEmpty(subject)) {
            subTextField.setError("Required.");
            valid = false;
        } else {
            subTextField.setError(null);
        }

        if (TextUtils.isEmpty(date)) {
            dateTextView.setError("Required.");
            valid = false;
        } else {
            dateTextView.setError(null);
        }

        if (TextUtils.isEmpty(notes)) {
            notesTextField.setError("Required.");
            valid = false;
        } else {
            notesTextField.setError(null);
        }
        return valid;
    }


    public void makeAppointment() {


        if (!validateForm()) {
            return;
        }

        String doctorName = getIntent().getExtras().getString("doctor_name");

        String patientName = pname;
        String doctorId = getIntent().getExtras().getString("doctor_id");
        ;
        String patientId = user.getUid();
        String subject = subTextField.getText().toString();
        String appointmentDate = dateTextView.getText().toString();
        String fileUrl = downloadUrl.toString();
        String notes = notesTextField.getText().toString();
        boolean accepted = false;
        boolean rejected = false;


        DatabaseReference patientRef = databaseReference.child("users").child("patients").child(user.getUid()).child("appointments");
        DatabaseReference doctorRef = databaseReference.child("users").child("doctors").child(doctorId).child("appointments");
        DatabaseReference requestRef = databaseReference.child("appointments");
        String request_id = requestRef.push().getKey();
        String appointmentId = request_id;
        Appointment appointment = new Appointment(appointmentId, doctorName, patientName, doctorId, patientId, subject, appointmentDate, fileUrl, notes, accepted, rejected);

        patientRef.child(request_id).setValue(true);
        doctorRef.child(request_id).setValue(true);
        requestRef.child(request_id).setValue(appointment);

        sendNotification();
        startActivity(new Intent(this, PatientHomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == dateTextView) {
            Calendar cal = Calendar.getInstance();
            year = cal.get(Calendar.YEAR);
            month = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(DoctorAppointmentActivity.this, DoctorAppointmentActivity.this, year, month, day);
            datePickerDialog.show();
        }

        if (view == uploadBtn) {
            showDocumentChooser();
        }

        if (view == confirmAppointmentBtn) {
            makeAppointment();

        }
    }

    private void sendNotification() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    String sendToUserID;

                    String doctorId = getIntent().getExtras().getString("doctor_id");
                    sendToUserID = doctorId;

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic NzNiNWE0YWQtZDM1ZS00Y2NiLWExNGUtNDYwODI2YzNjZDQ2");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"1e2c35a3-73b5-47a3-8277-109d924d9289\","

                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"user_id\", \"relation\": \"=\", \"value\": \"" + sendToUserID + "\"}],"

                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \"New Appointment Request Recieved from" + pname + " for Date " + dateTextView.getText() + "\"}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });
    }

}
