package com.example.homeshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.HashMap;
import java.util.Map;

public class profile extends Fragment {

    private RadioGroup radioGroupSearchType;
    private RadioButton radioLookingForApartment, radioHaveApartment;
    private LinearLayout layoutLookingForApartment, layoutHaveApartment;
    private Spinner spinnerAreaLooking, spinnerAreaHave;
    private EditText editTextMinPriceLooking, editTextMaxPriceLooking, editTextRoomsLooking;
    private EditText editTextSizeHave, editTextRoomsHave, editTextPriceHave;
    private EditText editTextHobbies, editTextDescription;
    private CheckBox checkBoxSmoking, checkBoxPets;
    private Button btnSave;

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    public profile() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = database.getReference("users").child(currentUser.getUid());
        }

        // Connect to XML components
        radioGroupSearchType = view.findViewById(R.id.radioGroupSearchType);
        radioLookingForApartment = view.findViewById(R.id.radioLookingForApartment);
        radioHaveApartment = view.findViewById(R.id.radioHaveApartment);
        layoutLookingForApartment = view.findViewById(R.id.layoutLookingForApartment);
        layoutHaveApartment = view.findViewById(R.id.layoutHaveApartment);
        spinnerAreaLooking = view.findViewById(R.id.spinnerAreaLooking);
        editTextMinPriceLooking = view.findViewById(R.id.editTextMinPriceLooking);
        editTextMaxPriceLooking = view.findViewById(R.id.editTextMaxPriceLooking);
        editTextRoomsLooking = view.findViewById(R.id.editTextRoomsLooking);
        spinnerAreaHave = view.findViewById(R.id.spinnerAreaHave);
        editTextSizeHave = view.findViewById(R.id.editTextSizeHave);
        editTextRoomsHave = view.findViewById(R.id.editTextRoomsHave);
        editTextPriceHave = view.findViewById(R.id.editTextPriceHave);
        editTextHobbies = view.findViewById(R.id.editTextHobbiesLooking);
        editTextDescription = view.findViewById(R.id.editTextDescriptionLooking);
        checkBoxSmoking = view.findViewById(R.id.checkBoxSmokingAllowedLooking);
        checkBoxPets = view.findViewById(R.id.checkBoxPetsAllowedLooking);
        btnSave = view.findViewById(R.id.btnSaveProfile);

        // Change visibility based on user selection
        radioGroupSearchType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLookingForApartment) {
                layoutLookingForApartment.setVisibility(View.VISIBLE);
                layoutHaveApartment.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioHaveApartment) {
                layoutHaveApartment.setVisibility(View.VISIBLE);
                layoutLookingForApartment.setVisibility(View.GONE);
            }
        });

        btnSave.setOnClickListener(v -> saveProfileData());

        // Load existing data from the database if it exists
        loadUserProfile();

        return view;
    }

    private void saveProfileData() {
        if (currentUser == null) {
            Toast.makeText(getContext(), "User not logged in!", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedOption = radioGroupSearchType.getCheckedRadioButtonId();
        String searchType = selectedOption == R.id.radioLookingForApartment ? "Looking" : "Have";

        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("searchType", searchType);

        if ("Looking".equals(searchType)) {
            userProfile.put("area", spinnerAreaLooking.getSelectedItem().toString());
            userProfile.put("minPrice", parseIntOrZero(editTextMinPriceLooking.getText().toString()));
            userProfile.put("maxPrice", parseIntOrZero(editTextMaxPriceLooking.getText().toString()));
            userProfile.put("rooms", parseIntOrZero(editTextRoomsLooking.getText().toString()));
        } else {
            userProfile.put("area", spinnerAreaHave.getSelectedItem().toString());
            userProfile.put("size", parseIntOrZero(editTextSizeHave.getText().toString()));
            userProfile.put("rooms", parseIntOrZero(editTextRoomsHave.getText().toString()));
            userProfile.put("price", parseIntOrZero(editTextPriceHave.getText().toString()));
        }

        userProfile.put("hobbies", editTextHobbies.getText().toString());
        userProfile.put("description", editTextDescription.getText().toString());
        userProfile.put("smokingAllowed", checkBoxSmoking.isChecked());
        userProfile.put("petsAllowed", checkBoxPets.isChecked());

        userRef.setValue(userProfile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to save profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadUserProfile() {
        if (currentUser == null) return;

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String searchType = snapshot.child("searchType").getValue(String.class);
                    if ("Looking".equals(searchType)) {
                        radioGroupSearchType.check(R.id.radioLookingForApartment);
                        layoutLookingForApartment.setVisibility(View.VISIBLE);
                        layoutHaveApartment.setVisibility(View.GONE);
                        setSpinnerSelection(spinnerAreaLooking, snapshot.child("area").getValue(String.class));
                        editTextMinPriceLooking.setText(String.valueOf(snapshot.child("minPrice").getValue(Integer.class)));
                        editTextMaxPriceLooking.setText(String.valueOf(snapshot.child("maxPrice").getValue(Integer.class)));
                        editTextRoomsLooking.setText(String.valueOf(snapshot.child("rooms").getValue(Integer.class)));
                    } else {
                        radioGroupSearchType.check(R.id.radioHaveApartment);
                        layoutLookingForApartment.setVisibility(View.GONE);
                        layoutHaveApartment.setVisibility(View.VISIBLE);
                        setSpinnerSelection(spinnerAreaHave, snapshot.child("area").getValue(String.class));
                        editTextSizeHave.setText(String.valueOf(snapshot.child("size").getValue(Integer.class)));
                        editTextRoomsHave.setText(String.valueOf(snapshot.child("rooms").getValue(Integer.class)));
                        editTextPriceHave.setText(String.valueOf(snapshot.child("price").getValue(Integer.class)));
                    }

                    editTextHobbies.setText(snapshot.child("hobbies").getValue(String.class));
                    editTextDescription.setText(snapshot.child("description").getValue(String.class));
                    checkBoxSmoking.setChecked(snapshot.child("smokingAllowed").getValue(Boolean.class) != null &&
                            snapshot.child("smokingAllowed").getValue(Boolean.class));
                    checkBoxPets.setChecked(snapshot.child("petsAllowed").getValue(Boolean.class) != null &&
                            snapshot.child("petsAllowed").getValue(Boolean.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSpinnerSelection(Spinner spinner, String value) {
        if (value == null) return;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(value)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
    private int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}