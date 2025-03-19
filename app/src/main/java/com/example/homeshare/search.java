package com.example.homeshare;

import android.os.Bundle;
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

    private DatabaseReference databaseReference;

    public search() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        searchButton = view.findViewById(R.id.searchButton);
        listView = view.findViewById(R.id.listView);

        userList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        searchButton.setOnClickListener(v -> searchUsers());

        return view;
    }

    private void searchUsers() {
        String queryText = searchInput.getText().toString().trim();
        if (queryText.isEmpty()) {
            Toast.makeText(getContext(), "נא להזין שם לחיפוש", Toast.LENGTH_SHORT).show();
            return;
        }

        Query query = databaseReference.orderByChild("name").equalTo(queryText);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String name = data.child("name").getValue(String.class);
                    if (name != null) {
                        userList.add(name);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "שגיאה בטעינת הנתונים", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
