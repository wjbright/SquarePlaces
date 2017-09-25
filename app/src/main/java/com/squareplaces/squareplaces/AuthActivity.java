package com.squareplaces.squareplaces;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Copyright SQUARE PLACES all rights reserved
 */

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private FirebaseAnalytics firebaseAnalytics;
    private Button sign_up_user, sign_in_user;
    private EditText input_email, input_password;
    private ImageButton showPassword;
    private TextView forgotPassword;
    private View.OnClickListener onClickListener;
    private Vibrator vibrator;
    private Typeface regular;
    private ConnectionDetector cd;
    private ProgressDialog progressDialog;
    private boolean isPasswordVisible;
    private final String TAG = "AuthActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth_activity);

        firebaseAnalytics = FirebaseAnalytics.getInstance(AuthActivity.this);
        //TODO Log using Firebase Analytics

        regular = Typeface.createFromAsset(getAssets(), getString(R.string.font_regular));
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==sign_up_user){
//                    signUp();
                    showMessage("Not yet Implemented");

                } else if(v==sign_in_user){
                    if(isInternetAvailable()){
                        if(isEmailValid() && isPasswordValid()){
                            signIn();
                        }

                    } else {
                        showMessage(getString(R.string.no_internet));
                    }

                } else if(v==showPassword){
                    if(!isPasswordVisible){
                        isPasswordVisible = true;
                        showPassword.setImageResource(R.drawable.ic_text_invisible);
                        input_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        input_password.setSelection(input_password.getText().length());
                    } else {
                        isPasswordVisible = false;
                        showPassword.setImageResource(R.drawable.ic_text_visible);
                        input_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        input_password.setSelection(input_password.getText().length());
                    }
                } else if(v==forgotPassword){
                    final String em = input_email.getText().toString();
                    if(isEmailValid()){
                        progressDialog.setMessage(getString(R.string.please_wait));
                        progressDialog.show();
                        firebaseAuth.sendPasswordResetEmail(em).
                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            notifyPasswordReset( getString(R.string.forgot_password_email_sent1) +
                                                    em + getString(R.string.forgot_password_email_sent2));
                                        } else {
                                            notifyPasswordReset(getString(R.string.signin_error_email_1));
                                        }
                                        progressDialog.dismiss();
                                    }
                                });
                    }
                }
            }
        };

        //initialize views
        input_email = (EditText) findViewById(R.id.email);
        input_password = (EditText) findViewById(R.id.password);
        input_email.setHint(getString(R.string.email));
        input_password.setHint(getString(R.string.password));
        sign_up_user = (Button) findViewById(R.id.sign_up);
        sign_up_user.setOnClickListener(onClickListener);
        sign_in_user = (Button) findViewById(R.id.sign_in);
        sign_in_user.setOnClickListener(onClickListener);
        showPassword = (ImageButton) findViewById(R.id.password_reveal);
        showPassword.setOnClickListener(onClickListener);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(onClickListener);

        //Get FireBase Auth Instance
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        cd = new ConnectionDetector(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private void signUp(){
        final String email = input_email.getText().toString().trim();
        String password= input_password.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        if(task.getException() instanceof FirebaseAuthInvalidUserException){
                            showMessage(getString(R.string.signin_error_email_1));

//                          TODO: Prompt user of SignUp option in convoform

                        } else {
                            showMessage(getString(R.string.signin_error_email_2));
                        }
                    } else {
                        try{
                            showMessage("Not implemented yet");

//                            TODO: Launch convoform (conversational form)
                            Intent intent = new Intent(AuthActivity.this, ConvoForm.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e){

                            Log.e(TAG,"New User creation error:" + e);

                            //for developer easy test purposes only. Should not be published
                            Toast.makeText(AuthActivity.this, "Error:" + e, Toast.LENGTH_LONG).show();
                        }
                        Intent intent = new Intent(AuthActivity.this, FormActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            });
    }

    private void signIn(){
        String email = input_email.getText().toString().trim();
        String password= input_password.getText().toString().trim();

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(!task.isSuccessful()) {
                        if(task.getException() instanceof FirebaseAuthInvalidUserException){
                            showMessage(getString(R.string.signin_error_email_1));
                        } else {
                            showMessage(getString(R.string.signin_error_email_2));
                        }
                    } else {
                        //Launch Map Activity or News Feed
                        //For the sake of Testing FormActivity, we will be launching
                        //FormActivity instead
                        Intent intent = new Intent(AuthActivity.this, ConvoForm.class);
                        startActivity(intent);
                        AuthActivity.this.finish();
                    }
                }
//            }).
//                addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                    @Override
//                    public void onSuccess(AuthResult authResult) {
//                        Intent intent = new Intent(AuthActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        AuthActivity.this.finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if(e instanceof FirebaseAuthInvalidUserException){
//                            showMessage(getString(R.string.signin_error_email_1));
//                        } else {
//                            showMessage(getString(R.string.signin_error_email_2));
//                        }
//                    }
        });
        progressDialog.setMessage(getString(R.string.new_account_sign_in));
        progressDialog.show();
    }

    //TODO Create option in setting for signOut method(). This method should be moved to where logic
    // of Settings would be setup

    private void signOut() {
        try {
            firebaseAuth.signOut();

            if (firebaseAuth.getCurrentUser() == null) {
                //send user back to logon activity
                Intent intent = new Intent(this, AuthActivity.class);
                startActivity(intent);
                //return to AuthActivity
            }

        } catch (Exception e) {
            Log.e(TAG, "Error Signing out: " + e);
            showMessage("Sorry, we couldn't sign you out at the moment");
        }
    }

    private boolean isInternetAvailable(){
        return cd.isConnectingToInternet();
    }

    private boolean isEmailValid() {
        String email = input_email.getText().toString().trim();
        if(email.isEmpty()){
            showMessage(getString(R.string.email_error_1));
            input_email.requestFocus();
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage(getString(R.string.email_error_2));
            input_email.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isPasswordValid() {
        String password = input_password.getText().toString().trim();
        if (password.isEmpty()){
            showMessage(getString(R.string.password_error_1));
            input_password.requestFocus();
            return false;
        } else if(password.length()<8) {
            showMessage(getString(R.string.password_error_2));
            input_password.requestFocus();
            return false;
        }
        return true;
    }

    private void notifyPasswordReset(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.show();
        TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
        messageView.setTypeface(regular);
        messageView.setTextSize(17.5F);
        messageView.setGravity(Gravity.CENTER);
    }


    private void showMessage(String message) {
        Snackbar.make(sign_in_user, message, Snackbar.LENGTH_LONG).show();
        vibrator.vibrate(80);
    }

}
