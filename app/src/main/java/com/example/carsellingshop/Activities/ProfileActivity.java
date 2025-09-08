package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.carsellingshop.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView tvName, tvEmail, tvAvatarInitial;
    private EditText etEditName;
    private Button btnEditName, btnSaveName;

    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.topToolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Profile");

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_profile) {
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_aboutus) {
                startActivity(new Intent(this, AboutActivity.class));
            } else if (id == R.id.nav_logout) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(this, LogInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                return true;
            }

            drawerLayout.closeDrawers();
            return true;
        });

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAvatarInitial = findViewById(R.id.tvAvatarInitial);

        etEditName = findViewById(R.id.etEditName);
        btnEditName = findViewById(R.id.btnEditName);
        btnSaveName = findViewById(R.id.btnSaveName);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser != null) {
            tvEmail.setText(currentUser.getEmail());

            db.collection("users").document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String username = snapshot.getString("username");
                            if (username != null && !username.isEmpty()) {
                                tvName.setText(username);
                                tvAvatarInitial.setText(username.substring(0, 1).toUpperCase());
                            } else {
                                tvName.setText("User");
                                tvAvatarInitial.setText("U");
                            }
                        }
                    });

            GradientDrawable bg = (GradientDrawable) findViewById(R.id.avatarContainer)
                    .getBackground().mutate();
            bg.setColor(pickStableColor(currentUser.getUid()));
        }

        // Edit button
        btnEditName.setOnClickListener(v -> {
            etEditName.setVisibility(View.VISIBLE);
            btnSaveName.setVisibility(View.VISIBLE);
            etEditName.setText(tvName.getText().toString());
        });

        // Save button
        btnSaveName.setOnClickListener(v -> {
            String newName = etEditName.getText().toString().trim();
            if (newName.isEmpty()) {
                etEditName.setError("Name cannot be empty");
                return;
            }

            db.collection("users").document(currentUser.getUid())
                    .update("username", newName)
                    .addOnSuccessListener(unused -> {
                        tvName.setText(newName);
                        tvAvatarInitial.setText(newName.substring(0, 1).toUpperCase());
                        etEditName.setVisibility(View.GONE);
                        btnSaveName.setVisibility(View.GONE);
                    })
                    .addOnFailureListener(e ->
                            Log.e("ProfileActivity", "Error updating name", e));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(this, LogInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        } else {
            super.onBackPressed();
        }
    }

    private int pickStableColor(String s) {
        int hash = s.hashCode();
        float hue = (hash & 0xFFFFFF) % 360;
        return Color.HSVToColor(new float[]{hue, 0.55f, 0.85f});
    }
}