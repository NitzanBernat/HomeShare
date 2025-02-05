package com.example.homeshare;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignIn extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private RadioGroup userTypeGroup;
    private RadioButton hasApartmentButton;
    private Spinner locationSpinner;
    private LinearLayout hasApartmentLayout;
    private EditText apartmentDescriptionEditText, roommatesCountEditText, budgetEditText, hobbiesEditText, freeTextEditText;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


        userTypeGroup = findViewById(R.id.userTypeGroup);
        hasApartmentButton = findViewById(R.id.hasApartmentButton);
        locationSpinner = findViewById(R.id.locationSpinner);
        hasApartmentLayout = findViewById(R.id.hasApartmentLayout);
        apartmentDescriptionEditText = findViewById(R.id.apartmentDescription);
        roommatesCountEditText = findViewById(R.id.roommatesCount);
        budgetEditText = findViewById(R.id.budgetEditText);
        hobbiesEditText = findViewById(R.id.hobbiesEditText);
        freeTextEditText = findViewById(R.id.freeTextEditText);
        registerButton = findViewById(R.id.registerButton);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.location_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(adapter);


        userTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.hasApartmentButton) {
                hasApartmentLayout.setVisibility(View.VISIBLE);
            } else {
                hasApartmentLayout.setVisibility(View.GONE);
            }
        });


        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String userId = auth.getCurrentUser().getUid();
        boolean hasApartment = hasApartmentButton.isChecked();
        String selectedLocation = locationSpinner.getSelectedItem().toString();
        String apartmentDescription = hasApartment ? apartmentDescriptionEditText.getText().toString() : null;
        int roommatesCount = hasApartment ? Integer.parseInt(roommatesCountEditText.getText().toString()) : 0;
        int budget = budgetEditText.getText().toString().isEmpty() ? 0 : Integer.parseInt(budgetEditText.getText().toString());
        String hobbies = hobbiesEditText.getText().toString();
        String freeText = freeTextEditText.getText().toString();

        // שמירת הנתונים ב-Firebase
        Map<String, Object> userData = new HashMap<>();
        userData.put("hasApartment", hasApartment);
        userData.put("apartmentLocation", selectedLocation);
        userData.put("apartmentDescription", apartmentDescription);
        userData.put("roommatesCount", roommatesCount);
        userData.put("budget", budget);
        userData.put("hobbies", Arrays.asList(hobbies.split(",")));
        userData.put("freeText", freeText);

        db.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> Toast.makeText(this, "הרשמה בוצעה בהצלחה!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "שגיאה בהרשמה", Toast.LENGTH_SHORT).show());
    }
}