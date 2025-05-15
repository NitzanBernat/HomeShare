package com.example.homeshare;

import android.net.Uri;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class profile extends Fragment {

    private RadioGroup radioGroupSearchType;
    private RadioButton radioLookingForApartment, radioHaveApartment;
    private LinearLayout layoutLookingForApartment, layoutHaveApartment;
    private EditText spinnerAreaLooking, spinnerAreaHave;
    private EditText editTextMinPriceLooking, editTextMaxPriceLooking, editTextRoomsLooking;
    private EditText editTextSizeHave, editTextRoomsHave, editTextPriceHave;
    private EditText editTextHobbies, editTextDescription;
    private CheckBox checkBoxSmoking, checkBoxPets;
    private EditText Name;
    private Button btnSave;

    private Button btnCapture;
    private ImageView imageViewProfile;
    private Uri imageUri;
    private FirebaseStorage storage;
    private ImageView imageView;
    private Button pickImageButton;

    private ActivityResultLauncher<String> imagePickerLauncher;

    private FirebaseDatabase database;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    public profile() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        database = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            userRef = database.getReference("users").child(currentUser.getUid());
        }

        imageView = view.findViewById(R.id.imageView);
        pickImageButton = view.findViewById(R.id.pickImageButton);

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
        Name = view.findViewById(R.id.editTextName);

        radioGroupSearchType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioLookingForApartment) {
                layoutLookingForApartment.setVisibility(View.VISIBLE);
                layoutHaveApartment.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioHaveApartment) {
                layoutHaveApartment.setVisibility(View.VISIBLE);
                layoutLookingForApartment.setVisibility(View.GONE);
            }
        });

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        imageUri = uri;
                        imageView.setImageURI(uri);
                    }
                }
        );

        pickImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProfileData());

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
        userProfile.put("name", Name.getText().toString());

        if ("Looking".equals(searchType)) {
            userProfile.put("area", spinnerAreaLooking.getText().toString());
            userProfile.put("minPrice", parseIntOrZero(editTextMinPriceLooking.getText().toString()));
            userProfile.put("maxPrice", parseIntOrZero(editTextMaxPriceLooking.getText().toString()));
            userProfile.put("rooms", parseIntOrZero(editTextRoomsLooking.getText().toString()));
        } else {
            userProfile.put("area", spinnerAreaHave.getText().toString());
            userProfile.put("size", parseIntOrZero(editTextSizeHave.getText().toString()));
            userProfile.put("rooms", parseIntOrZero(editTextRoomsHave.getText().toString()));
            userProfile.put("price", parseIntOrZero(editTextPriceHave.getText().toString()));
        }

        userProfile.put("hobbies", editTextHobbies.getText().toString());
        userProfile.put("description", editTextDescription.getText().toString());
        userProfile.put("smokingAllowed", checkBoxSmoking.isChecked());
        userProfile.put("petsAllowed", checkBoxPets.isChecked());

        // Convert image to Base64 and save
        if (imageView.getDrawable() != null) {
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            byte[] imageBytes = baos.toByteArray();
            String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            userProfile.put("imageBase64", imageBase64);
        }

        DatabaseReference profileRef = userRef.child("profile");
        profileRef.setValue(userProfile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to save profile: " + task.getException(), Toast.LENGTH_LONG).show();
                Log.e("Firebase", "Error saving profile", task.getException());
            }
        });
    }

    private void loadUserProfile() {
        if (currentUser == null) return;
        DatabaseReference profileRef = userRef.child("profile");

        profileRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) return;

                String searchType = snapshot.child("searchType").getValue(String.class);
                if ("Looking".equals(searchType)) {
                    radioGroupSearchType.check(R.id.radioLookingForApartment);
                    layoutLookingForApartment.setVisibility(View.VISIBLE);
                    layoutHaveApartment.setVisibility(View.GONE);
                    spinnerAreaLooking.setText(snapshot.child("area").getValue(String.class));
                    editTextMinPriceLooking.setText(String.valueOf(snapshot.child("minPrice").getValue(Integer.class)));
                    editTextMaxPriceLooking.setText(String.valueOf(snapshot.child("maxPrice").getValue(Integer.class)));
                    editTextRoomsLooking.setText(String.valueOf(snapshot.child("rooms").getValue(Integer.class)));
                } else {
                    radioGroupSearchType.check(R.id.radioHaveApartment);
                    layoutLookingForApartment.setVisibility(View.GONE);
                    layoutHaveApartment.setVisibility(View.VISIBLE);
                    spinnerAreaHave.setText(snapshot.child("area").getValue(String.class));
                    editTextSizeHave.setText(String.valueOf(snapshot.child("size").getValue(Integer.class)));
                    editTextRoomsHave.setText(String.valueOf(snapshot.child("rooms").getValue(Integer.class)));
                    editTextPriceHave.setText(String.valueOf(snapshot.child("price").getValue(Integer.class)));
                }

                Name.setText(snapshot.child("name").getValue(String.class));
                editTextHobbies.setText(snapshot.child("hobbies").getValue(String.class));
                editTextDescription.setText(snapshot.child("description").getValue(String.class));
                checkBoxSmoking.setChecked(snapshot.child("smokingAllowed").getValue(Boolean.class) != null &&
                        snapshot.child("smokingAllowed").getValue(Boolean.class));
                checkBoxPets.setChecked(snapshot.child("petsAllowed").getValue(Boolean.class) != null &&
                        snapshot.child("petsAllowed").getValue(Boolean.class));

                // Load image from Base64
                String imageBase64 = snapshot.child("imageBase64").getValue(String.class);
                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    byte[] imageBytes = Base64.decode(imageBase64, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageView.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load data!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int parseIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
