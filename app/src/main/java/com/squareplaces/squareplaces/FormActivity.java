package com.squareplaces.squareplaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Copyright Square Places. All Rights Reserved.
 */

public class FormActivity extends AppCompatActivity {

    private final String DATABASE_URL = "https://square-places.firebaseio.com/";
    private String uid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        //firebase auth instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //get user ID
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        //check to avoid NPE
        if (firebaseUser != null) {
            uid = firebaseUser.getUid();
        }

    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReferenceFromUrl(DATABASE_URL);

    public DatabaseReference getdatabaseReference() {
        return databaseReference;
    }

    private void setdatabaseReference(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
        /*
        * ToDo: Create personal preference for user id
        */
    }

    @IgnoreExtraProperties
    private class User {

        private String name;

        private User () {

        }

        private User (String userID, String name){
            this.name = name;
            uid = userID;
        }

    }

    @IgnoreExtraProperties
    private class UserPreference {

    }

}
