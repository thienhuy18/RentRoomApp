package com.example.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {
    private List<Contact> contactList;
    private OnContactClickListener listener;

    public ContactAdapter(List<Contact> contactList, OnContactClickListener listener) {
        this.contactList = contactList;
        this.listener = listener;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvContactName.setText(contact.getName());
        holder.itemView.setOnClickListener(v -> listener.onContactClick(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvContactName;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvContactName = itemView.findViewById(R.id.tvContactName);
        }
    }

    public interface OnContactClickListener {
        void onContactClick(Contact contact);
    }
}
