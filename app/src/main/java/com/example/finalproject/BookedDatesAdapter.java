package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookedDatesAdapter extends RecyclerView.Adapter<BookedDatesAdapter.BookedDateViewHolder> {
    private List<String> bookedDatesList;

    public BookedDatesAdapter(List<String> bookedDatesList) {
        this.bookedDatesList = bookedDatesList;
    }

    @Override
    public BookedDateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new BookedDateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookedDateViewHolder holder, int position) {
        String bookedDate = bookedDatesList.get(position);
        holder.dateTextView.setText(bookedDate);
    }

    @Override
    public int getItemCount() {
        return bookedDatesList.size();
    }

    static class BookedDateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public BookedDateViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
