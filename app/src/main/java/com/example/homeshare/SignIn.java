package com.example.homeshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;




public class SignIn extends AppCompatActivity {

    private static String TAG = "FirebaseExample";

    //FirebaseDatabase database;
    //DatabaseReference database_reference;
    FirebaseAuth firebaseAuth;

    EditText userName;
    EditText password;

    Button send;

    void initComponent() {
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);

        send = findViewById(R.id.send);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        initComponent();

        firebaseAuth = FirebaseAuth.getInstance();

        setListener();
    }

    private void setListener() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize Firebase Authentication
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                // Example user details
                String email = userName.getText().toString();
                String password2 = password.getText().toString();


                // 1. Create User in Firebase Authentication
                mAuth.createUserWithEmailAndPassword(email, password2)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();


                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    System.err.println("Authentication failed: " + task.getException().getMessage());
                                }
                                Toast.makeText(SignIn.this, TAG, Toast.LENGTH_LONG).show();
                            }
                        });
                Toast.makeText(SignIn.this, TAG, Toast.LENGTH_LONG).show();

            }
        });
    }
}