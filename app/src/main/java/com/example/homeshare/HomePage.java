package com.example.homeshare;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

public class HomePage extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private Button logout;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Initialize Firebase Authentication
        firebaseAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        logout = findViewById(R.id.logout);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        frameLayout = findViewById(R.id.frameLayout);

        // Set the initial fragment (you can choose which one to start with)
        loadFragment(new profile(), false);

        // Set up the bottom navigation listener
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.mypro) {
                    loadFragment(new profile(), false);
                    return true;
                } else if (itemId == R.id.search) {
                    loadFragment(new search(), false);
                    return true;
                } else if (itemId == R.id.profiles) {
                    loadFragment(new profiles(), false);
                    return true;
                }
                return false;
            }
        });

        // Set up the logout button listener
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void loadFragment(Fragment fragment, boolean isAppInitialized) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (isAppInitialized) {
            fragmentTransaction.add(R.id.frameLayout, fragment);
        } else {
            fragmentTransaction.replace(R.id.frameLayout, fragment);
        }

        fragmentTransaction.commit();
    }

    private void signOut() {
        // Sign out from Firebase Authentication
        firebaseAuth.signOut();

        // Navigate back to the MainActivity (or your login screen)
        Intent intent = new Intent(HomePage.this, MainActivity.class);

        // Clear the activity stack so the user can't go back to HomePage
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        startActivity(intent);
        finish(); // Close the HomePage activity
    }
}