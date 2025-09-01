package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsellingshop.Model.User;
import com.example.carsellingshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button signUpButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // 🔹 Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);

        mAuth = FirebaseAuth.getInstance();

        signUpButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // 🔹 Validations
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Password must be at least 8 characters long", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.matches(".*[0-9].*")) {
            Toast.makeText(this, "Password must contain at least 1 number", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.matches(".*[!@#$%^&()].*")) {
            Toast.makeText(this, "Password must contain at least 1 special character", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password.matches(".*[A-Za-z].*")) {
            Toast.makeText(this, "Password must contain at least 1 letter", Toast.LENGTH_LONG).show();
            return;
        }

        // 🔹 Let Firebase enforce password strength (min 6 chars)
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser fUser = mAuth.getCurrentUser();
                        if (fUser != null) {
                            fUser.sendEmailVerification();

                            String uid = fUser.getUid();

                            // 🔹 Save user to Firestore
                            User user = new User(
                                    uid,        // Firebase UID
                                    name,       // username
                                    email,
                                    "normal"    // default userType
                            );

                            FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(uid)
                                    .set(user)
                                    .addOnSuccessListener(unused ->
                                            Toast.makeText(this, "User saved in database", Toast.LENGTH_SHORT).show()
                                    )
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }

                        Toast.makeText(this, "Registration successful. Please verify your email.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignUpActivity.this, LogInActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}