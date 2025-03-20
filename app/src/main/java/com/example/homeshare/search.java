package com.example.homeshare;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class search extends Fragment {

    private EditText searchInput;
    private Button searchButton;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> userList;
    private List<String> userIdsList; // רשימת userId-ים

    private DatabaseReference databaseReference;

    public search() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchButton = view.findViewById(R.id.searchButton);
        listView = view.findViewById(R.id.listView);

        userList = new ArrayList<>();
        userIdsList = new ArrayList<>(); // יישום הרשימה של ה- userId

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        searchButton.setOnClickListener(v -> searchUsers());

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedUserId = userIdsList.get(position); // שליפת ה-userId המתאים
            showUserProfile(selectedUserId);
        });

        return view;
    }

    private void searchUsers() {
        String queryText = searchInput.getText().toString().trim();
        if (queryText.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a name to search", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = databaseReference.orderByChild("profile/name").equalTo(queryText);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                userIdsList.clear(); // ננקה גם את ה-userId-ים

                if (snapshot.exists()) {
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String userId = userSnapshot.getKey(); // קבלת ה-userId
                        DataSnapshot profileSnapshot = userSnapshot.child("profile");
                        if (profileSnapshot.exists()) {
                            String name = profileSnapshot.child("name").getValue(String.class);
                            if (name != null) {
                                userList.add(name);
                                userIdsList.add(userId); // הוספת userId לרשימה
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "No users found with that name", Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("search", "Error searching users", error.toException());
                Toast.makeText(getContext(), "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showUserProfile(String userId) {
        serch_profile bottomSheetFragment = serch_profile.newInstance(userId);
        bottomSheetFragment.show(getParentFragmentManager(), bottomSheetFragment.getTag());
    }
}
