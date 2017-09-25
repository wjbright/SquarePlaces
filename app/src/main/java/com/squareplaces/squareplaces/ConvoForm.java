package com.squareplaces.squareplaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by SQUARE PLACES on 9/19/2017.
 */

public class ConvoForm extends AppCompatActivity{

    private EditText editMessageView;
    private FloatingActionButton sendButton;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convo_form);

        firebaseDatabase = FirebaseDatabase.getInstance();
        sendMessageButton();
    }

    private void sendMessageButton() {
        sendButton = (FloatingActionButton) findViewById(R.id.sendMessagebutton);
        editMessageView = (EditText) findViewById(R.id.inputText);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ConvoForm.this, "Working on it", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
