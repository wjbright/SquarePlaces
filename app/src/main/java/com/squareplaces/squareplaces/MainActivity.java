package com.squareplaces.squareplaces;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private View.OnClickListener onClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
            }
        };

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setCancelable(false);
                pDialog.setMessage(getString(R.string.signing_out));
                pDialog.show();
                firebaseAuth.signOut();
            }
        };
        Button signOut = (Button) findViewById(R.id.signOut);
        signOut.setOnClickListener(onClickListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener!=null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
