package com.example.finalproject;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.io.File;
import java.util.List;

public class RoomDetailsActivity extends AppCompatActivity {
    private ViewPager2 viewPagerImages;
    private TextView tvImageCounter, tvRoomAddress, tvRoomDescription, tvRoomPrice;
    private ImageButton btnPrevious, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_details);

        viewPagerImages = findViewById(R.id.viewPagerImages);
        tvImageCounter = findViewById(R.id.tvImageCounter);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);

        // Additional Room Information
        tvRoomAddress = findViewById(R.id.tvRoomAddress);
        tvRoomDescription = findViewById(R.id.tvRoomDescription);
        tvRoomPrice = findViewById(R.id.tvRoomPrice);

        // Retrieve the room and imageUrls
        Room room = (Room) getIntent().getSerializableExtra("room");
        List<String> imageUrls = room != null ? room.getImageUrls() : null;

        // Populate Room Information
        if (room != null) {
            tvRoomAddress.setText("Address: " + room.getAddress());
            tvRoomDescription.setText(room.getDescription());
            tvRoomPrice.setText("Price: $" + room.getPrice());
        }

        if (imageUrls != null && !imageUrls.isEmpty()) {
            // Set up the ViewPager2
            ImagePagerAdapter adapter = new ImagePagerAdapter(imageUrls, this);
            viewPagerImages.setAdapter(adapter);

            // Update image counter
            tvImageCounter.setText("1/" + imageUrls.size());

            // ViewPager2 Page Change Listener
            viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
                @Override
                public void onPageSelected(int position) {
                    super.onPageSelected(position);
                    tvImageCounter.setText((position + 1) + "/" + imageUrls.size());
                }
            });

            // Previous Button
            btnPrevious.setOnClickListener(v -> {
                int currentItem = viewPagerImages.getCurrentItem();
                if (currentItem > 0) {
                    viewPagerImages.setCurrentItem(currentItem - 1);
                }
            });

            // Next Button
            btnNext.setOnClickListener(v -> {
                int currentItem = viewPagerImages.getCurrentItem();
                if (currentItem < imageUrls.size() - 1) {
                    viewPagerImages.setCurrentItem(currentItem + 1);
                }
            });
        } else {
            // No images
            tvImageCounter.setText("0/0");
        }
    }
}