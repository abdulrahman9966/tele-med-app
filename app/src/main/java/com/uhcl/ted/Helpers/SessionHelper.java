package com.uhcl.ted.Helpers;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.opentok.OpenTok;
import com.opentok.Session;
import com.opentok.exception.OpenTokException;


public class SessionHelper {




   private FirebaseAuth firebaseAuth  = FirebaseAuth.getInstance();
   private FirebaseDatabase database = FirebaseDatabase.getInstance();
   private DatabaseReference databaseReference = database.getReference().child("doctors");


   public void getSession() throws OpenTokException {
       int apiKey = 46092342; // YOUR API KEY
       String apiSecret = "36cf7be90ac8753663ffb537664791bd68adf295";
       OpenTok opentok = new OpenTok(apiKey, apiSecret);
       Session session = opentok.createSession();

       String sessionId = session.getSessionId();

       //store in firebase
       databaseReference.child("session").setValue(sessionId);

       String token = opentok.generateToken(sessionId);
       databaseReference.child("session").setValue(token);


   }


}
