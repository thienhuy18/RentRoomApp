package com.example.finalproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.util.List;

public class SavedRoomsAdapter extends RecyclerView.Adapter<SavedRoomsAdapter.SavedRoomViewHolder> {

    private List<Room> savedRoomList;
    private Context context;

    public SavedRoomsAdapter(List<Room> savedRoomList, Context context) {
        this.savedRoomList = savedRoomList;
        this.context = context;
    }

    @NonNull
    @Override
    public SavedRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saved_room, parent, false); // New layout
        return new SavedRoomViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRoomViewHolder holder, int position) {
        Room room = savedRoomList.get(position);

        holder.roomDescription.setText(room.getDescription());
        holder.price.setText(room.getPrice()+"/Tháng");
        holder.address.setText(room.getAddress());

        loadImage(holder, room);
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, RoomDetailsActivity.class);
            intent.putExtra("room", room);
            context.startActivity(intent);
        });


        holder.unsaveRoomButton.setOnClickListener(v -> {
            unsaveRoom(room, position);
        });
    }

    private void loadImage(SavedRoomViewHolder holder, Room room) {
        String imageFileName = room.getImageFileName();
        File file = new File(context.getFilesDir(), "images/" + imageFileName);

        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                holder.roomImage.setImageBitmap(bitmap);
            } else {
                holder.roomImage.setImageResource(R.drawable.placeholder_image);
            }
        } else {
            holder.roomImage.setImageResource(R.drawable.placeholder_image);
        }
    }

    private void unsaveRoom(Room room, int position) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users")
                .document(userId)
                .collection("savedRooms")
                .document(room.getIdRoom())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    savedRoomList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, savedRoomList.size());
                    Toast.makeText(context, "Phòng đã được gỡ khỏi danh sách ưa thích!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Đã xảy ra lỗi khi gỡ phòng.", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public int getItemCount() {
        return savedRoomList.size();
    }

    public static class SavedRoomViewHolder extends RecyclerView.ViewHolder {
        public TextView roomDescription, price, address;
        public ImageView roomImage;
        public Button unsaveRoomButton;

        public SavedRoomViewHolder(View itemView) {
            super(itemView);
            roomDescription = itemView.findViewById(R.id.tvRoomDescription);
            price = itemView.findViewById(R.id.tvPrice);
            address = itemView.findViewById(R.id.tvAddress);
            roomImage = itemView.findViewById(R.id.ivRoomImage);
            unsaveRoomButton = itemView.findViewById(R.id.btnUnsaveRoom);
        }
    }
}
