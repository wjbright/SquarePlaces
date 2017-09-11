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
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by KRYPT TECH on 9/2/2017.
 */

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Button sign_up_user, sign_in_user;
    private EditText input_email, input_password;
    private ProgressBar progressBar;

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

        //initialize button and listeners
        signUp();
        signIn();
    }



    private void signUp(){

        sign_up_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBar.setVisibility(View.VISIBLE);
                String email = input_email.getText().toString().trim();
                String password= input_password.getText().toString().trim();

                if (!checkEmail()){
                    return;
                }

                if (!checkPassword()){
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        Toast.makeText(AuthActivity.this, "Authentication failed" + task.getException(), Toast.LENGTH_LONG).show();

                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(AuthActivity.this, "Sign up successful", Toast.LENGTH_LONG).show();
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
                        return;
                    }

                    if (!checkPassword()){
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);


                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(AuthActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        newUser();
                                        Toast.makeText(AuthActivity.this, "Authentication failed" + task.getException(), Toast.LENGTH_LONG).show();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        formIntent();
                                        Toast.makeText(AuthActivity.this, "Sign In successful", Toast.LENGTH_LONG).show();

                                        //Launch Map Activity or News Feed
                                        Intent intent = new Intent(AuthActivity.this, FormActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                }
            });
        } else {
            Toast.makeText(AuthActivity.this,"Already Signed In", Toast.LENGTH_SHORT).show();

            // Launch Map Screen or News Feed activity
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

    private static boolean newUser() {
        return true;
    }

    private void formIntent(){
        if (newUser()) {
            Intent intent = new Intent(AuthActivity.this, FormActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}
