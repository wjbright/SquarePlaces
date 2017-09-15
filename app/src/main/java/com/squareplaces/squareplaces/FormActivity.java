package com.squareplaces.squareplaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

/**
 * Copyright Square Places. All Rights Reserved.
 */

public class FormActivity extends AppCompatActivity {

    private EditText user_first_name, user_last_name, user_phone_number, user_address;
    private Button form_submit_button;
    private static String DATABASE_URL = "https://square-places.firebaseio.com/";
    private String uid;
    private String first_name_input;
    private String last_name_input;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_activity);

        //initialize views
        user_first_name = (EditText) findViewById(R.id.user_first_name);
        user_last_name = (EditText) findViewById(R.id.user_last_name);
        user_phone_number = (EditText) findViewById(R.id.user_phone_number);
        user_address = (EditText) findViewById(R.id.user_address);
        form_submit_button = (Button) findViewById(R.id.form_submit_button);

        //firebase auth instance
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        //get user ID
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();

        //set button listeners
        submit_form();

        //get user input
        first_name_input = user_first_name.getText().toString();
        last_name_input = user_last_name.getText().toString();

    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseRef = database.getReferenceFromUrl(DATABASE_URL);

    public DatabaseReference getdatabaseRef() {
        return databaseRef;
    }

    private void setdatabaseRef(DatabaseReference databaseRef) {
        this.databaseRef = databaseRef;

        /*
        * ToDo: Create personal preference for user id
        */
        //example
        //databaseRef.child("users").child(userId).setValue(formInputObject);

    }

    @IgnoreExtraProperties
    private class User {
        private String first_name;
        private String last_name;
        private String full_name;
        private int phone_number;

        private User (String userID, String first_name, String last_name){
            this.first_name = first_name;
            this.last_name = last_name;
            this.full_name = first_name + " " + last_name;
            uid = userID;
        }

        private void writeNewUser(){
            User user = new User(uid, first_name_input, last_name_input);

            //write the info under the user's email
            //....child(user_email)
            //databaseRef.child("user").child(uid).setValue(user);
        }
    }

    private void submit_form(){
        form_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    setdatabaseRef(databaseRef);
                    getdatabaseRef();
                    Toast.makeText(FormActivity.this, user_first_name.getText().toString() + " " + user_last_name.getText().toString(), Toast.LENGTH_LONG).show();
                    FormActivity.this.finish();
                }catch (Exception e){
                    Toast.makeText(FormActivity.this,"Error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
