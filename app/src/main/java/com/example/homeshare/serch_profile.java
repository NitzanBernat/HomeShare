package com.example.homeshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class serch_profile extends BottomSheetDialogFragment {

    private static final String ARG_USER_ID = "userId";

    private TextView nameTextView, searchTypeTextView, areaTextView, priceRangeTextView, roomsTextView;
    private TextView sizeTextView, priceHaveTextView, hobbiesTextView, descriptionTextView, smokingTextView, petsTextView;
    private Button closeButton;

    public static serch_profile newInstance(String userId) {
        serch_profile fragment = new serch_profile();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_serch_profile, container, false);

        // Initialize views
        nameTextView = view.findViewById(R.id.profileName);
        searchTypeTextView = view.findViewById(R.id.profileSearchType);
        areaTextView = view.findViewById(R.id.profileArea);
        priceRangeTextView = view.findViewById(R.id.profilePriceRange);
        roomsTextView = view.findViewById(R.id.profileRooms);
        sizeTextView = view.findViewById(R.id.profileSize);
        priceHaveTextView = view.findViewById(R.id.profilePriceHave);
        hobbiesTextView = view.findViewById(R.id.profileHobbies);
        descriptionTextView = view.findViewById(R.id.profileDescription);
        smokingTextView = view.findViewById(R.id.profileSmoking);
        petsTextView = view.findViewById(R.id.profilePets);
        closeButton = view.findViewById(R.id.closeButton);

        // Get user ID from arguments
        if (getArguments() != null) {
            String userId = getArguments().getString(ARG_USER_ID);
            if (userId != null) {
                loadUserProfile(userId);
            }
        }

        // Close button listener
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    private void loadUserProfile(String userId) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userId).child("profile");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Load data from the snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    String searchType = snapshot.child("searchType").getValue(String.class);
                    String area = snapshot.child("area").getValue(String.class);
                    Integer minPrice = snapshot.child("minPrice").getValue(Integer.class);
                    Integer maxPrice = snapshot.child("maxPrice").getValue(Integer.class);
                    Integer rooms = snapshot.child("rooms").getValue(Integer.class);
                    Integer size = snapshot.child("size").getValue(Integer.class);
                    Integer price = snapshot.child("price").getValue(Integer.class);
                    String hobbies = snapshot.child("hobbies").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    Boolean smokingAllowed = snapshot.child("smokingAllowed").getValue(Boolean.class);
                    Boolean petsAllowed = snapshot.child("petsAllowed").getValue(Boolean.class);

                    // Set the text views
                    nameTextView.setText(name);
                    searchTypeTextView.setText(searchType);
                    areaTextView.setText(area);

                    // Handle null values for price range
                    if (minPrice != null && maxPrice != null) {
                        priceRangeTextView.setText("₪" + minPrice + " - ₪" + maxPrice);
                    } else {
                        priceRangeTextView.setText("N/A");
                    }

                    // Handle null values for rooms, size, and price
                    roomsTextView.setText(rooms != null ? rooms + " rooms" : "N/A");
                    sizeTextView.setText(size != null ? size + " sqm" : "N/A");
                    priceHaveTextView.setText(price != null ? "₪" + price : "N/A");

                    hobbiesTextView.setText(hobbies);
                    descriptionTextView.setText(description);

                    // Handle null values for smoking and pets
                    smokingTextView.setText(smokingAllowed != null && smokingAllowed ? "Allows Smoking" : "No Smoking");
                    petsTextView.setText(petsAllowed != null && petsAllowed ? "Allows Pets" : "No Pets");

                } else {
                    Toast.makeText(getContext(), "User profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("serch_profile", "Failed to load profile", error.toException());
                Toast.makeText(getContext(), "Failed to load profile: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}