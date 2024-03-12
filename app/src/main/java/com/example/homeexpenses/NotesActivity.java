package com.example.homeexpenses;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class NotesActivity extends AppCompatActivity {

    private EditText editTextNote;
    private DatabaseReference mDatabase;
    private String originalText = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        // Find all elements
        editTextNote = findViewById(R.id.editTextNote);
        Button buttonSave = findViewById(R.id.buttonSave);
        Button buttonCancel = findViewById(R.id.buttonCancel);

        // Initialize connection to firebase database and get the "note" in a one liner. im not sure exactly what's included in Note yet. but we need to load the actual values in next step.
        mDatabase = FirebaseDatabase.getInstance().getReference().child("note");

        // load existing value - ATTEMPT TO POPULATE THE EDIT TEXT FIELD WITH DATA FROM DB.
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String note = dataSnapshot.getValue(String.class);
                    originalText = note;
                    editTextNote.setText(note);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(NotesActivity.this, "Failed to load note from database.", Toast.LENGTH_LONG).show();
            }
        });

        // Listener for changes on editText
        editTextNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // do nothing
            }

            @Override
            public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
            {
                if (lengthAfter > lengthBefore) {
                    if (text.toString().length() == 1) {
                        text = "◎ " + text;
                        editTextNote.setText(text);
                        editTextNote.setSelection(editTextNote.getText().length());
                    }
                    if (text.toString().endsWith("\n")) {
                        text = text.toString().replace("\n", "\n◎ ");
                        text = text.toString().replace("◎ ◎", "◎");
                        editTextNote.setText(text);
                        editTextNote.setSelection(editTextNote.getText().length());
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable e) {

            }
        });

        // HANDLE THE SAVE BUTTON.
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String note = editTextNote.getText().toString();
                mDatabase.setValue(note);
                Toast.makeText(NotesActivity.this, "Note vistuð", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            }
        });

        // HANDLE THE CANCEL BUTTON
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the activity
            }
        });

    }

    @Override
    public void onBackPressed() {
        String currentText = editTextNote.getText().toString();
        if(!currentText.equals(originalText)) {
            new AlertDialog.Builder(this)
                    .setMessage("Viltu vista breytingar?")
                    .setPositiveButton("Vista", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            mDatabase.setValue(editTextNote.getText().toString());
                            finish();
                        }
                    })
                    .setNegativeButton("Nei", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .show();
        } else {
            finish();
        }
    }
}
