package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private List<Room> roomList;

    private Context context;


    public List<Room> getRoomList() {
        return roomList;
    }

    public RoomAdapter(List<Room> roomList) {
        this.roomList = roomList;
    }
    public RoomAdapter(List<Room> roomList, Context context) {
        this.roomList = roomList;
        this.context = context;
    }

    public void setRoomList(List<Room> roomList) {
        this.roomList = roomList;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_room, parent, false);
        return new RoomViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.roomDescription.setText(room.getDescription());
        holder.price.setText(room.getPrice()+ "/Tháng");
        holder.address.setText(room.getAddress());
        holder.ownerName.setText(room.getOwnerName());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailsActivity.class);
            intent.putExtra("room", room);
            context.startActivity(intent);
        });
        holder.scheduleViewingButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DatePickerActivity.class);
            intent.putExtra("roomId", room.getIdRoom());
            context.startActivity(intent);
        });





        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("savedRooms")
                .document(room.getIdRoom())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().exists()) {
                        holder.saveRoomButton.setVisibility(View.GONE);
                    } else {
                        holder.saveRoomButton.setVisibility(View.VISIBLE);
                    }
                });

        holder.saveRoomButton.setOnClickListener(v -> {
            saveRoomToFavorites(room, holder.saveRoomButton);
        });

        String imageFileName = room.getImageFileName();
        File file = new File(context.getFilesDir(), "images/" + imageFileName);


        if (file.exists()) {

            try {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                if (bitmap != null) {
                    holder.roomImage.setImageBitmap(bitmap);
                    Log.d("RoomAdapter", "Image loaded successfully with BitmapFactory.");
                } else {
                    Log.e("RoomAdapter", "Failed to load image with BitmapFactory.");
                    holder.roomImage.setImageResource(R.drawable.placeholder_image);
                }
            } catch (Exception e) {
                Log.e("RoomAdapter", "Error loading image: " + e.getMessage(), e);
                holder.roomImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            Log.e("RoomAdapter", "Image file does not exist: " + file.getAbsolutePath());
            holder.roomImage.setImageResource(R.drawable.placeholder_image); 
        }



    }





    private void saveRoomToFavorites(Room room, ImageButton saveRoomButton) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("savedRooms")
                .document(room.getIdRoom())
                .set(room)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Phòng đã được lưu vào danh sách ưa thích!", Toast.LENGTH_SHORT).show();
                    saveRoomButton.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    Log.e("RoomAdapter", "Failed to save room", e);
                    Toast.makeText(context, "Đã xảy ra lỗi khi lưu phòng.", Toast.LENGTH_SHORT).show();
                });
    }






    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        public TextView roomDescription, price, address,ownerName;
        public ImageView roomImage;
        public Button  scheduleViewingButton;
        public ImageButton saveRoomButton;




        public RoomViewHolder(View itemView) {
            super(itemView);
            roomDescription = itemView.findViewById(R.id.tvRoomDescription);
            price = itemView.findViewById(R.id.tvPrice);
            address = itemView.findViewById(R.id.tvAddress);
            roomImage = itemView.findViewById(R.id.ivRoomImage);
            ownerName = itemView.findViewById(R.id.ownerName);
            saveRoomButton = itemView.findViewById(R.id.btnSaveRoom);
            scheduleViewingButton = itemView.findViewById(R.id.btnScheduleViewing);


        }


    }
}
