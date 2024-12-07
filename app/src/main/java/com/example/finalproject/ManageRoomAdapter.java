package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ManageRoomAdapter extends RecyclerView.Adapter<ManageRoomAdapter.ManageRoomViewHolder> {

    private List<Room> roomList;
    private Context context;
    private OnRoomActionListener onRoomActionListener;

    public interface OnRoomActionListener {
        void onEdit(Room room);
        void onDelete(Room room);
    }

    public ManageRoomAdapter(List<Room> roomList, Context context, OnRoomActionListener onRoomActionListener) {
        this.roomList = roomList;
        this.context = context;
        this.onRoomActionListener = onRoomActionListener;
    }

    @NonNull
    @Override
    public ManageRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_manage_room, parent, false);
        return new ManageRoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageRoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.roomName.setText(room.getRoomName());
        holder.price.setText(room.getPrice());
        holder.address.setText(room.getAddress());

        holder.btnEditRoom.setOnClickListener(v -> {
            if (onRoomActionListener != null) {
                onRoomActionListener.onEdit(room);
            }
        });

        holder.btnDeleteRoom.setOnClickListener(v -> {
            if (onRoomActionListener != null) {
                onRoomActionListener.onDelete(room);
            }
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ManageRoomViewHolder extends RecyclerView.ViewHolder {

        public TextView roomName, price, address;
        public Button btnEditRoom, btnDeleteRoom;

        public ManageRoomViewHolder(View itemView) {
            super(itemView);
            roomName = itemView.findViewById(R.id.tvRoomName);
            price = itemView.findViewById(R.id.tvPrice);
            address = itemView.findViewById(R.id.tvAddress);
            btnEditRoom = itemView.findViewById(R.id.btnEditRoom);
            btnDeleteRoom = itemView.findViewById(R.id.btnDeleteRoom);
        }
    }
}
