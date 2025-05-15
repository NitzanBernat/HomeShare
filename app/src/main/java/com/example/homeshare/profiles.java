package com.example.homeshare;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class profiles extends Fragment {

    private LinearLayout profilesLayout;
    private DatabaseReference usersRef;
    private String currentUserId;

    private String area;
    private long rooms;
    private boolean smokingAllowed;
    private boolean petsAllowed;
    private String searchType;
    private long price;
    private long minPrice;
    private long maxPrice;

    public profiles() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profiles, container, false);
        profilesLayout = view.findViewById(R.id.profileLayout);
        usersRef = FirebaseDatabase.getInstance().getReference("users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        loadCurrentUserProfile();

        return view;
    }

    private void loadCurrentUserProfile() {
        usersRef.child(currentUserId).child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    area = snapshot.child("area").getValue(String.class);
                    rooms = snapshot.child("rooms").getValue(Long.class);
                    smokingAllowed = Boolean.TRUE.equals(snapshot.child("smokingAllowed").getValue(Boolean.class));
                    petsAllowed = Boolean.TRUE.equals(snapshot.child("petsAllowed").getValue(Boolean.class));
                    searchType = snapshot.child("searchType").getValue(String.class);

                    if ("Have".equals(searchType)) {
                        price = snapshot.child("price").getValue(Long.class);
                    } else if ("Looking".equals(searchType)) {
                        minPrice = snapshot.child("minPrice").getValue(Long.class);
                        maxPrice = snapshot.child("maxPrice").getValue(Long.class);
                    }

                    loadMatchingProfiles();
                } else {
                    Toast.makeText(getContext(), "Profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfilesFragment", "loadCurrentUserProfile: failed", error.toException());
            }
        });
    }

    private void loadMatchingProfiles() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profilesLayout.removeAllViews();

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String otherUserId = userSnapshot.getKey();
                    if (otherUserId == null || otherUserId.equals(currentUserId)) continue;

                    DataSnapshot profileSnapshot = userSnapshot.child("profile");
                    if (!profileSnapshot.exists()) continue;

                    String otherSearchType = profileSnapshot.child("searchType").getValue(String.class);
                    String otherArea = profileSnapshot.child("area").getValue(String.class);
                    Long otherRooms = profileSnapshot.child("rooms").getValue(Long.class);
                    Boolean otherSmoking = profileSnapshot.child("smokingAllowed").getValue(Boolean.class);
                    Boolean otherPets = profileSnapshot.child("petsAllowed").getValue(Boolean.class);
                    String otherName = profileSnapshot.child("name").getValue(String.class);

                    if (otherSearchType == null || otherArea == null || otherRooms == null ||
                            otherSmoking == null || otherPets == null || otherName == null) continue;

                    boolean priceMatches = false;
                    if ("Looking".equals(searchType) && "Have".equals(otherSearchType)) {
                        Long otherPrice = profileSnapshot.child("price").getValue(Long.class);
                        if (otherPrice != null) {
                            priceMatches = (otherPrice >= minPrice && otherPrice <= maxPrice);
                        }
                    } else if ("Have".equals(searchType) && "Looking".equals(otherSearchType)) {
                        Long otherMinPrice = profileSnapshot.child("minPrice").getValue(Long.class);
                        Long otherMaxPrice = profileSnapshot.child("maxPrice").getValue(Long.class);
                        if (otherMinPrice != null && otherMaxPrice != null) {
                            priceMatches = (price >= otherMinPrice && price <= otherMaxPrice);
                        }
                    }

                    if (priceMatches &&
                            area.equals(otherArea) &&
                            rooms == otherRooms &&
                            smokingAllowed == otherSmoking &&
                            petsAllowed == otherPets) {

                        TextView profileView = new TextView(getContext());
                        profileView.setText("שם: " + otherName + "\nאזור: " + otherArea + "\nחדרים: " + otherRooms);
                        profileView.setTextSize(18);
                        profileView.setPadding(20, 20, 20, 20);
                        profileView.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

                        LinearLayout container = new LinearLayout(getContext());
                        container.setOrientation(LinearLayout.VERTICAL);
                        container.setPadding(10, 10, 10, 10);

                        if ("Have".equals(otherSearchType)) {
                            String imageBase64 = profileSnapshot.child("imageBase64").getValue(String.class);
                            ImageView imageView = new ImageView(getContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(400, 400);
                            layoutParams.setMargins(0, 0, 0, 20);
                            imageView.setLayoutParams(layoutParams);

                            if (imageBase64 != null && !imageBase64.isEmpty()) {
                                try {
                                    byte[] decodedBytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                                    imageView.setImageBitmap(bitmap);
                                } catch (Exception e) {
                                    Log.e("ProfilesFragment", "Failed to decode Base64 image", e);
                                    imageView.setImageResource(R.drawable.default_picture);
                                }
                            } else {
                                imageView.setImageResource(R.drawable.default_picture);
                            }

                            container.addView(imageView);
                        }

                        container.addView(profileView);
                        profilesLayout.addView(container);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfilesFragment", "loadMatchingProfiles: failed", error.toException());
            }
        });
    }


    private void loadImageFromUrl(String imageUrl, ImageView imageView) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap originalBitmap = BitmapFactory.decodeStream(input);

                // שינוי גודל (scale) של התמונה
                int targetWidth = 100;
                int targetHeight = 100;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, targetWidth, targetHeight, true);

                // הצגת התמונה ב-UI
                getActivity().runOnUiThread(() -> imageView.setImageBitmap(scaledBitmap));
            } catch (Exception e) {
                Log.e("ProfilesFragment", "Error loading image", e);
            }
        }).start();
    }

}
