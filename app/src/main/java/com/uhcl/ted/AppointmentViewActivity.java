package com.uhcl.ted;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.TypefaceProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class AppointmentViewActivity extends AppCompatActivity implements View.OnClickListener {
    private String aNode_key = null;
    private DatabaseReference databaseReference;
    private DatabaseReference patientReference;
    private ImageView imageDetail;
    private BootstrapButton acceptBtn, rejectBtn, callBtn;
    private TextView appDName, appPName, appDate, appSubject, appNotes, appFileUrl, appStatus;
    String doctorId, pname, dname, date, subject, notes, fileUrl, patientId;
    private FirebaseAuth firebaseAuth;
    FirebaseStorage storage;
    StorageReference storageRef;
    FirebaseUser user;
    String appointmentId;
    DatabaseReference appRef;
    boolean accepted, rejected;
    private BootstrapButton downloadBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_view);
        TypefaceProvider.registerDefaultIconSets();

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getInstance().getCurrentUser();

        imageDetail = (ImageView) findViewById(R.id.imageDetail);
        appDName = (TextView) findViewById(R.id.doctorName);
        appPName = (TextView) findViewById(R.id.patientName);
        appDate = (TextView) findViewById(R.id.appointmentDate);
        appSubject = (TextView) findViewById(R.id.appSubject);
        appNotes = (TextView) findViewById(R.id.appNotes);
        appFileUrl = (TextView) findViewById(R.id.appFileUrl);
        appStatus = (TextView) findViewById(R.id.appStatus);
        acceptBtn = (BootstrapButton) findViewById(R.id.acceptBtn);
        rejectBtn = (BootstrapButton) findViewById(R.id.rejectBtn);
        callBtn = (BootstrapButton) findViewById(R.id.callBtn);
        downloadBtn = (BootstrapButton) findViewById(R.id.downloadBtn);

        acceptBtn.setOnClickListener(this);
        rejectBtn.setOnClickListener(this);
        callBtn.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setAppintmentIntialUI();
    }

    private void setAppintmentIntialUI() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users/doctors").child(user.getUid()).child("appointments");
        appointmentId = getIntent().getExtras().getString("appointmentId");

        appRef = FirebaseDatabase.getInstance().getReference().child("appointments").child(appointmentId);

        appRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dname = (String) dataSnapshot.child("doctorName").getValue();
                    pname = (String) dataSnapshot.child("patientName").getValue();
                    patientId = (String) dataSnapshot.child("patientId").getValue();
                    date = (String) dataSnapshot.child("appointmentDate").getValue();
                    subject = (String) dataSnapshot.child("subject").getValue();
                    notes = (String) dataSnapshot.child("notes").getValue();
                    fileUrl = (String) dataSnapshot.child("fileUrl").getValue();
                    storage = FirebaseStorage.getInstance();
                    storageRef = storage.getReference();

                    accepted = (boolean) dataSnapshot.child("accepted").getValue();
                    rejected = (boolean) dataSnapshot.child("rejected").getValue();

                    appDName.setText(dname);
                    appPName.setText(pname);
                    appDate.setText(date);
                    appSubject.setText(subject);
                    appNotes.setText(notes);
                    appFileUrl.setText(fileUrl);
                    downloadBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(fileUrl)));

                        }
                    });


                    SharedPreferences sharedPref = getSharedPreferences("userTypeSelection", Context.MODE_PRIVATE);
                    String userType = sharedPref.getString("userType", "");
                    Toast.makeText(AppointmentViewActivity.this, userType, Toast.LENGTH_SHORT).show();
                    switch (userType) {
                        case "patient":
                            acceptBtn.setVisibility(View.GONE);
                            callBtn.setVisibility(View.GONE);
                            rejectBtn.setVisibility(View.GONE);
                            if (!accepted && !rejected) {
                                appStatus.setText("Appointment Request Sent to Doctor");
                            } else if (accepted && !rejected) {
                                callBtn.setVisibility(View.VISIBLE);
                                callBtn.setText("Join Session");
                                appStatus.setText("Appointment Confirmed");
                            } else if (!accepted && rejected) {
                                callBtn.setVisibility(View.GONE);
                                appStatus.setText("Appointment Rejected");

                            }

                            break;
                        case "doctor":
                            if (!accepted && !rejected) {
                                acceptBtn.setVisibility(View.VISIBLE);
                                rejectBtn.setVisibility(View.VISIBLE);
                                callBtn.setVisibility(View.GONE);
                                appStatus.setText("Pending Request");

                            } else if (accepted && !rejected) {
                                acceptBtn.setVisibility(View.GONE);
                                rejectBtn.setVisibility(View.GONE);
                                callBtn.setVisibility(View.VISIBLE);
                                appStatus.setText("Appointment Accepted");

                            } else if (!accepted && rejected) {
                                acceptBtn.setVisibility(View.GONE);
                                rejectBtn.setVisibility(View.GONE);
                                callBtn.setVisibility(View.GONE);
                                appStatus.setText("Appointment Rejected");

                            }
                            break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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


                    sendToUserID = patientId;
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
                                + "\"contents\": {\"en\": \"Your Appointment Request has been Accepted by doctor " + dname + " for Date " + appDate.getText() + "\"}"
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


    @Override
    public void onClick(View view) {
        if (view == acceptBtn) {
            appRef.child("accepted").setValue(true);
            appRef.child("rejected").setValue(false);
            callBtn.setVisibility(View.VISIBLE);
            acceptBtn.setVisibility(View.GONE);
            rejectBtn.setVisibility(View.GONE);
            appStatus.setText("Appointment Confirmed");
            sendNotification();

        } else if (view == rejectBtn) {
            appRef.child("accepted").setValue(false);
            appRef.child("rejected").setValue(true);
            acceptBtn.setVisibility(View.GONE);
            appStatus.setText("Appointment Rejected");

            rejectBtn.setVisibility(View.GONE);
        }

        if (view == callBtn) {

            SharedPreferences sharedPref = getSharedPreferences("userTypeSelection", Context.MODE_PRIVATE);
            String userType = sharedPref.getString("userType", "");
            switch (userType) {
                case "patient":
                    Intent intent1 = new Intent(AppointmentViewActivity.this, TestVCActivity.class);
                    Bundle a = new Bundle();
                    a.putString("appointmentId", appointmentId);
                    intent1.putExtras(a);
                    startActivity(intent1);
                    break;
                case "doctor":
                    Intent intent = new Intent(AppointmentViewActivity.this, VideoChatActivity.class);
                    Bundle b = new Bundle();
                    b.putString("appointmentId", appointmentId);
                    intent.putExtras(b);
                    startActivity(intent);
                    break;
            }
            finish();
        }
    }
}
