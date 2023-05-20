package com.example.homeexpenses;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ExpenseViewHolder extends RecyclerView.ViewHolder {
    private TextView personTextView;
    private TextView amountTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;
    private ImageButton trashButton;

    public ExpenseViewHolder(@NonNull View itemView) {
        super(itemView);
        personTextView = itemView.findViewById(R.id.person_text_view);
        amountTextView = itemView.findViewById(R.id.amount_text_view);
        descriptionTextView = itemView.findViewById(R.id.description_text_view);
        dateTextView = itemView.findViewById(R.id.date_text_view);
        trashButton = itemView.findViewById(R.id.trash_button);
    }

    public void bind(Expense expense, ExpenseAdapter.OnTrashButtonClickListener listener) {
        // Bind data to the views

        personTextView.setText(expense.getPerson().toString());
        amountTextView.setText(String.valueOf(expense.getAmount()));
        descriptionTextView.setText(expense.getDescription());

        Date createdDate = expense.getCreatedDateTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy", Locale.getDefault());
        String formattedDate = dateFormat.format(createdDate);
        dateTextView.setText(formattedDate);

        // Set click listener for the trash button
        trashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTrashButtonClick(expense);
            }
        });
    }
}


