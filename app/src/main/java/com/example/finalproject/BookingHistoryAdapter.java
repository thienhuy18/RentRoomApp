package com.example.finalproject;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingHistoryViewHolder> {
    private List<String> bookingDatesList;

    public BookingHistoryAdapter(List<String> bookingDatesList) {
        this.bookingDatesList = bookingDatesList;
    }

    @NonNull
    @Override
    public BookingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking_history, parent, false);
        return new BookingHistoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingHistoryViewHolder holder, int position) {
        String bookingInfo = bookingDatesList.get(position);
        holder.tvBookingInfo.setText(bookingInfo);
    }

    @Override
    public int getItemCount() {
        return bookingDatesList.size();
    }

    public static class BookingHistoryViewHolder extends RecyclerView.ViewHolder {
        public TextView tvBookingInfo;

        public BookingHistoryViewHolder(View itemView) {
            super(itemView);
            tvBookingInfo = itemView.findViewById(R.id.tvBookingInfo);
        }
    }
}
