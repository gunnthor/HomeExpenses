package com.example.homeexpenses;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class ExpenseAdapter extends FirebaseRecyclerAdapter<Expense, ExpenseViewHolder> {
    private OnTrashButtonClickListener trashButtonClickListener;

    public ExpenseAdapter(@NonNull FirebaseRecyclerOptions<Expense> options, OnTrashButtonClickListener listener) {
        super(options);
        trashButtonClickListener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position, @NonNull Expense model) {
        //int reversedPosition = getItemCount() - position - 1;
        //Expense reversedModel = getItem(reversedPosition);
        holder.bind(model, trashButtonClickListener);
    }

    public interface OnTrashButtonClickListener {
        void onTrashButtonClick(Expense expense);
    }
}





