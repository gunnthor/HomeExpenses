package com.example.homeexpenses;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ExpenseListActivity extends AppCompatActivity {
    private DatabaseReference expensesRef;
    private ExpenseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses");

        /*RecyclerView recyclerView = findViewById(R.id.expense_list_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);*/
        RecyclerView recyclerView = findViewById(R.id.expense_list_recycler_view);;
        LinearLayoutManager mLayoutManager;

        mLayoutManager = new LinearLayoutManager(ExpenseListActivity.this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        ExpenseAdapter.OnTrashButtonClickListener trashButtonClickListener = new ExpenseAdapter.OnTrashButtonClickListener() {
            @Override
            public void onTrashButtonClick(Expense expense) {
                // Show a confirmation dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseListActivity.this);
                builder.setTitle("Staðfesting")
                                .setMessage("Ertu viss um að þú viljir eyða Expense?")
                                .setPositiveButton("Eyða", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //User confirmed deletion, proceed with it.
                                        expensesRef.child(expense.getId()).removeValue()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                      public void onSuccess(Void aVoid) {
                                                          //Toast.makeText(ExpenseListActivity.this,"Færsla eydd", Toast.LENGTH_LONG).show();
                                                        showSnackbar("Eyðsla tókst");
                                                      }
                                                  }
                                                )
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        //Toast.makeText(ExpenseListActivity.this,"Mistókst að eyða",Toast.LENGTH_LONG).show();
                                                        showSnackbar("Mistóks að eyða færslu");
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("Hætta við",null)
                                .show();
            }
        };

        FirebaseRecyclerOptions<Expense> options =
                new FirebaseRecyclerOptions.Builder<Expense>()
                        .setQuery(expensesRef, Expense.class)
                        .build();

        adapter = new ExpenseAdapter(options, trashButtonClickListener);

        //recyclerView.setLayoutManager(layoutManager);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    // Override onStart and onStop to start/stop listening for data changes
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}

