package com.example.homeshare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogIn extends AppCompatActivity {
    private static final String TAG = "FirebaseExample";

    FirebaseAuth firebaseAuth;

    EditText userName;
    EditText password;
    Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        initComponent();
        setListener();
    }

    void initComponent() {
        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);
        send = findViewById(R.id.send);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void setListener() {
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInUser();
            }
        });
    }

    private void signInUser() {
        // Get user input
        String email = userName.getText().toString().trim();
        String password2 = password.getText().toString().trim();

        if (email.isEmpty() || password2.isEmpty()) {
            Toast.makeText(LogIn.this, "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign In User with Firebase Authentication
        firebaseAuth.signInWithEmailAndPassword(email, password2)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Toast.makeText(LogIn.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();

                            // Move to the next screen after successful login
                            Intent intent = new Intent(LogIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogIn.this, "התחברות נכשלה: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}