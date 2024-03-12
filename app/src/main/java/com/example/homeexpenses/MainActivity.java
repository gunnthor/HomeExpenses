package com.example.homeexpenses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    //TODO: (General todo's): Research Android Lint and if that can help with programming.

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Firebase for the App.
        FirebaseApp.initializeApp(this);

        // Firebase authorization to display what user is signed in.
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        String displayName = user.getDisplayName();

        // Get a reference to the Firebase Realtime Database
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        // Get a reference to the "expenses" node in the database
        DatabaseReference expensesRef = databaseRef.child("expenses");

        // Find the elements
        Button submitButton = findViewById(R.id.button);
        EditText amountEditText = findViewById(R.id.expense_amount);
        RadioButton gunnthorRadioButton = findViewById(R.id.radioButtonGunnthor);
        RadioButton irisRadioButton = findViewById(R.id.radioButtonIris);
        RadioGroup radioGroup = findViewById(R.id.radioButtonGroup);
        EditText descriptionEditText = findViewById(R.id.editTextDescription);

        if(user != null){
            String userEmail = user.getEmail();

            if(userEmail.equals("gunnthor0405@gmail.com")) {
                gunnthorRadioButton.setChecked(true);
            } else if (userEmail.equals("iris.magnusd@gmail.com")){
                irisRadioButton.setChecked(true);
            }
        }

        // Add an onClick listener to the button.
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the expense details from the UI (e.g. from EditText views)
                Person person = gunnthorRadioButton.isChecked() ? Person.Gunnthor : Person.Iris;
                String amountText = amountEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                if(!amountText.isEmpty()) {
                    try {
                        final Long amount = Long.valueOf(amountText);

                        Runnable submitExpense = new Runnable() {
                            @Override
                            public void run() {
                                //Create new expense.
                                Expense expense = new Expense(person, amount, displayName, description);

                                //Push the expense to the database. And retrieve the generated reference.
                                DatabaseReference newExpenseRef = expensesRef.push();

                                String expenseId = newExpenseRef.getKey();

                                // Set the generated ID to the expense object
                                expense.setId(expenseId);

                                // Save the expense to the database
                                newExpenseRef.setValue(expense)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Expense saved successfully
                                                Toast.makeText(MainActivity.this, "Expense added", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to save the expense
                                                Toast.makeText(MainActivity.this, "Failed to add expense", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                //TODO: Clear the amount field after
                                amountEditText.setText("");
                                descriptionEditText.setText("");
                                Toast.makeText(MainActivity.this, "Expense vistað", Toast.LENGTH_LONG).show();
                            }
                        };

                        if(amount > 5000) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setTitle("Staðfesta expense");
                            builder.setMessage("Upphæð hærri en 50.000. Ertu viss?");
                            builder.setPositiveButton("Staðfesta", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    submitExpense.run();
                                }
                            });
                            builder.setNegativeButton("Hætta við", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(MainActivity.this, "Hætt við expense", Toast.LENGTH_SHORT).show();
                                }
                            });
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } else {
                            // Amt less than 50000
                            submitExpense.run();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, "Upphæð ekki gild", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this,"Vantar upphæð", Toast.LENGTH_LONG).show();
                }

                    amountEditText.requestFocus();

                    // Close the keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            }
        });

        // Listen of changes on the amount.
        expensesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called when the data in the database changes
                reCalculateBalanceFromDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here.

            }
        });


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                reCalculateBalanceFromDatabase();
            }
        });



    }

    private void reCalculateBalanceFromDatabase() {
        DatabaseReference expensesRef = FirebaseDatabase.getInstance().getReference("expenses");
        expensesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // process the data snapshot here
                Long totalAmount = Long.valueOf(0);
                Long gunnthorAmount = Long.valueOf(0);
                Long irisAmount = Long.valueOf(0);
                String finalString = "";

                //Loop through all the expenses in the database
                for(DataSnapshot expenseSnapshot : dataSnapshot.getChildren()) {
                    //Get the Expense object for the current snapshot
                    Expense expense = expenseSnapshot.getValue(Expense.class);

                    // Add the amounts
                    if(expense.getPerson() == Person.Gunnthor) {
                        gunnthorAmount += expense.getAmount();
                    } else {
                        irisAmount += expense.getAmount();
                    }
                }

                // Update the UI with the total amount
                TextView balanceTextView = findViewById(R.id.textViewActualBalance);
                RadioButton gunnthorRadioButton = findViewById(R.id.radioButtonGunnthor);
                Person person = gunnthorRadioButton.isChecked() ? Person.Gunnthor : Person.Iris;

                if(person == Person.Gunnthor) {
                    totalAmount = gunnthorAmount - irisAmount;
                    if(totalAmount  > 0) {
                        finalString = "Gunnþór hefur lagt út auka: " + String.format("%d", totalAmount);
                    } else if (totalAmount < 0) {
                        finalString = "Gunnþór skuldar Írisi: " + String.format("%d", totalAmount);
                    } else {
                        finalString = "Við erum kvitt";
                    }
                } else {
                    totalAmount = irisAmount - gunnthorAmount;
                    if(totalAmount > 0) {
                        finalString = "Íris hefur lagt út auka: " + String.format("%d", totalAmount);
                    } else if (totalAmount < 0) {
                        finalString = "Íris skuldar Gunnþóri: " + String.format("%d", totalAmount);
                    } else {
                        finalString = "Við erum kvitt";
                    }
                }

                balanceTextView.setText(finalString);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle read error here
            }
        });


    }

    // onClick listener. this is referenced from the activity_main.xml layout.
    public void openExpenseListActivity(View view) {
        Intent intent = new Intent(this, ExpenseListActivity.class);
        startActivity(intent);
    }

    public void openNotesActivity(View view) {
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }
}