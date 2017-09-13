package com.squareplaces.squareplaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ValueEventListener;

/**
 * Copyright SQUARE PLACES all rights reserved
 */

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uid;
    private Button sign_up_user, sign_in_user;
    private EditText input_email, input_password;
    private ProgressBar progressBar;
    private String squareplacesDatabase = "https://square-places.firebaseio.com/"; //Not sure whether to make this 'final'

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);

        //initialize views
        sign_up_user = (Button) findViewById(R.id.signUp);
        sign_in_user = (Button) findViewById(R.id.signIn);
        input_email = (EditText) findViewById(R.id.email);
        input_password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Get FireBase Auth Instance
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        uid = firebaseUser.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReferenceFromUrl(squareplacesDatabase);


        //initialize button and listeners
        signUp();
        signIn();
    }



    private void signUp(){

        sign_up_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                final String email = input_email.getText().toString().trim();
                String password= input_password.getText().toString().trim();

                if (!checkEmail()){
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                if (!checkPassword()){
                    progressBar.setVisibility(View.INVISIBLE);
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {

                                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(AuthActivity.this, "Sorry, Username already exists", Toast.LENGTH_LONG).show();

                                            //Provide Sign In option "Is this your account? Sign In!"
                                        }

                                        progressBar.setVisibility(View.INVISIBLE);

                                        //provide a more helpful error message
                                        Toast.makeText(AuthActivity.this, "Authentication failed" + task.getException(), Toast.LENGTH_LONG).show();

                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AuthActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();

                                        try{
                                            writeNewUser(email, uid);
                                        }catch (Exception e){
                                            Toast.makeText(AuthActivity.this, "Error:" + e, Toast.LENGTH_LONG).show();
                                        }
                                        Intent intent = new Intent(AuthActivity.this, FormActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                            }
                        });
            }
        });

    }

    private void signIn(){

        if (firebaseUser == null){
            sign_in_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String email = input_email.getText().toString().trim();
                    String password= input_password.getText().toString().trim();

                    if (!checkEmail()){
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }

                    if (!checkPassword()){
                        progressBar.setVisibility(View.INVISIBLE);
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);


                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AuthActivity.this, "Authentication failed" + task.getException(), Toast.LENGTH_LONG).show();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AuthActivity.this, "Sign In successful", Toast.LENGTH_LONG).show();

                                        //Launch Map Activity or News Feed

                                        //For the sake of Testing FormActivity, we will be launching
                                        //FormActivity instead
                                        Intent intent = new Intent(AuthActivity.this, FormActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            });
        } else {
            //Log this instead
            Toast.makeText(AuthActivity.this,"Already Signed In", Toast.LENGTH_SHORT).show();

            // Launch Map Screen or News Feed activity
            //For the sake of Testing FormActivity, we will be launching
            //FormActivity instead
            Intent intent = new Intent(AuthActivity.this, FormActivity.class);
            startActivity(intent);
        }

    }

    private boolean checkEmail() {
        String email = input_email.getText().toString().trim();
        if (email.isEmpty() || !isEmailValid(email)) {

            input_email.setError("Empty or invalid email field");
            requestFocus(input_email);
            return false;
        }
        return true;
    }

    private boolean checkPassword() {

        String password = input_password.getText().toString().trim();
        if (password.isEmpty() || !isPasswordValid(password)) {

            input_password.setError("Empty or Invalid password");
            requestFocus(input_password);
            return false;
        }
        return true;
    }

    private static boolean isEmailValid(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isPasswordValid(String password){
        return (password.length() >= 6);
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @IgnoreExtraProperties
    private class User {
        private String userId;
        private String userEmail;

        private User() {
            //Default Constructor for calls to DataSnapshot.getValue(User.class);
        }

        private User(String email, String uid) {
            this.userEmail = email;
            this.userId = uid;
        }

    }


    private void writeNewUser(String email, String userId) {
        User user = new User(email, userId);

        //when there has been an update in the User Email child
        databaseReference.child("users").child(userId).setValue(user);
    }


    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
