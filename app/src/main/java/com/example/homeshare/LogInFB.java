package com.example.homeshare;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

public class LogInFB {

    private static final String TAG = "FirebaseExample";

    // Data model for user data in the Realtime Database
    @IgnoreExtraProperties
    public static class UserData {
        public String name;
        public String phone;

        public UserData() {
            // Default constructor required for calls to DataSnapshot.getValue(UserData.class)
        }

        public UserData(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    public static void main(String[] args) {
        // Initialize Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Example user details
        String email = "testuser@example.com";
        String password = "securePassword123";
        String name = "Test User";
        String phone = "555-123-4567";

        // 1. Create User in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            // 2. Store Additional User Data in Realtime Database
                            if (user != null) {
                                storeUserDataInRealtimeDatabase(user.getUid(), name, phone);
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            System.err.println("Authentication failed: " + task.getException().getMessage());
                        }
                    }
                });
    }

    // Helper method to store user data in the Realtime Database
    private static void storeUserDataInRealtimeDatabase(String userId, String name, String phone) {
        // Get a reference to the Firebase Realtime Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users"); // "users" is the root node

        // Create a UserData object
        UserData userData = new UserData(name, phone);

        // Store the data under the user's UID
        usersRef.child(userId).setValue(userData)
                .addOnSuccessListener(aVoid -> {
                    // Data saved successfully!
                    System.out.println("User data saved successfully for user ID: " + userId);
                })
                .addOnFailureListener(e -> {
                    // Uh-oh, an error occurred!
                    System.err.println("Error saving user data: " + e.getMessage());
                });
    }
}