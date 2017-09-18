package com.squareplaces.squareplaces;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private Button signUp;
    private EditText email,password1,password2;
    private Typeface regular;
    private FirebaseAuth firebaseAuth;
    private ConnectionDetector connectionDetector;
    private ProgressDialog progressDialog;
    private View.OnClickListener onClickListener;
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==signUp){
                    String userEmail = getText(email);
                    String userPassword1 = getText(password1);
                    if(isInternetAvailable()){
                        if(isInputVerified()){
                            createNewUser(userEmail, userPassword1);
                        }
                    } else {
                        showMessage(getString(R.string.no_internet));
                    }
                }
            }
        };
        regular = Typeface.createFromAsset(getAssets(), getString(R.string.font_regular));
        email = (EditText) findViewById(R.id.email);
        email.setHint(getString(R.string.email));
        password1 = (EditText) findViewById(R.id.password1);
        password1.setHint(getString(R.string.password_sign_up_1));
        password2 = (EditText) findViewById(R.id.password2);
        password2.setHint(getString(R.string.password_sign_up_2));
        signUp = (Button) findViewById(R.id.sign_up);
        signUp.setOnClickListener(onClickListener);
        firebaseAuth = FirebaseAuth.getInstance();
        connectionDetector = new ConnectionDetector(this);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    private boolean isInputVerified(){
        String email = this.email.getText().toString().trim();
        String password1 = this.password1.getText().toString().trim();
        String password2 = this.password2.getText().toString().trim();
        if(email.isEmpty()){
            this.email.requestFocus();
            showMessage(getString(R.string.email_error_1));
            vibrator.vibrate(80);
            return false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage(getString(R.string.email_error_2));
            this.email.requestFocus();
            vibrator.vibrate(80);
            return false;
        } else if(password1.isEmpty()){
            this.password1.requestFocus();
            showMessage(getString(R.string.password_error_1));
            vibrator.vibrate(80);
            return false;
        } else if(password2.isEmpty()){
            this.password2.requestFocus();
            showMessage(getString(R.string.password_error_1));
            vibrator.vibrate(80);
            return false;
        } else if(password1.length()<8 || password2.length()<8) {
            showMessage(getString(R.string.password_error_2));
            vibrator.vibrate(80);
            return false;
        } else if(!password1.matches(password2)){
            showMessage(getString(R.string.password_error_3));
            vibrator.vibrate(80);
            return false;
        }
        return true;
    }

    private void createNewUser(final String email, final String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
//                            FORM ACTIVITY SHOULD BE HERE
                            promptGoToAccount();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        vibrator.vibrate(80);
                        showFailureMessage(e.getMessage());
                    }
                });
        progressDialog.setMessage(getString(R.string.new_account_creating));
        progressDialog.show();
    }

    private String getText(EditText editText){
        return editText.getText().toString().trim();
    }

    private void showFailureMessage(String message){
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
    }

    private void signInToAccount() {
        firebaseAuth.signInWithEmailAndPassword(getText(email), getText(password1)).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            Intent intent = new Intent(SignUp.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            SignUp.this.finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        vibrator.vibrate(80);
                        showFailureMessage(e.getMessage());
                    }
                });
        progressDialog.setMessage(getString(R.string.new_account_sign_in));
        progressDialog.show();
    }

    private void promptGoToAccount(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.new_account_dialog_title));
        builder.setMessage(getString(R.string.new_account_dialog_message));
        builder.setNegativeButton(getString(R.string.not_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signInToAccount();
            }
        });
        builder.show();
    }

    private boolean isInternetAvailable(){
        return connectionDetector.isConnectingToInternet();
    }

    private void showMessage(String message){
        Snackbar.make(signUp, message, Snackbar.LENGTH_LONG).show();
        vibrator.vibrate(80);
    }
}
