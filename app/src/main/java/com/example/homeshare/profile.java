package com.example.homeshare;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

public class profile extends Fragment {

    private RadioGroup radioGroupSearchType;
    private RadioButton radioLookingForApartment;
    private RadioButton radioHaveApartment;
    private LinearLayout layoutLookingForApartment;
    private LinearLayout layoutHaveApartment;
    private Spinner spinnerAreaLooking;
    private EditText editTextMinPriceLooking;
    private EditText editTextMaxPriceLooking;
    private EditText editTextRoomsLooking;
    private Spinner spinnerAreaHave;
    private EditText editTextSizeHave;
    private EditText editTextRoomsHave;
    private EditText editTextPriceHave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
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

        // Set up the RadioGroup listener
        radioGroupSearchType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioLookingForApartment) {
                    // Show "Looking for Apartment" layout, hide "Have Apartment" layout
                    layoutLookingForApartment.setVisibility(View.VISIBLE);
                    layoutHaveApartment.setVisibility(View.GONE);
                } else if (checkedId == R.id.radioHaveApartment) {
                    // Show "Have Apartment" layout, hide "Looking for Apartment" layout
                    layoutHaveApartment.setVisibility(View.VISIBLE);
                    layoutLookingForApartment.setVisibility(View.GONE);
                }
            }
        });

        return view;
    }
}