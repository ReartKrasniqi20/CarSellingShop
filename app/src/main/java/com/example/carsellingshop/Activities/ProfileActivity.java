package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private TextView tvName, tvEmail, tvAvatarInitial;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.topToolbar1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Profile");

        // ----- Drawer -----
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_favorites) {
                // placeholder
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

        // ----- Profile UI -----
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvAvatarInitial = findViewById(R.id.tvAvatarInitial);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            tvEmail.setText(u.getEmail() != null ? u.getEmail() : "—");

            // load Firestore profile
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(u.getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String username = snapshot.getString("username");
                            if (username != null && !username.isEmpty()) {
                                tvName.setText(username);

                                char initial = Character.toUpperCase(username.charAt(0));
                                tvAvatarInitial.setText(String.valueOf(initial));
                            } else {
                                tvName.setText("User");
                                tvAvatarInitial.setText("U");
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("ProfileActivity", "Error loading user profile", e);
                        tvName.setText("User");
                        tvAvatarInitial.setText("U");
                    });

            // stable color for avatar background
            String key = (u.getUid() != null) ? u.getUid() : "U";
            int color = pickStableColor(key);

            GradientDrawable bg = (GradientDrawable) findViewById(R.id.avatarContainer)
                    .getBackground().mutate();
            bg.setColor(color);
        }
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
        float sat = 0.55f;
        float val = 0.85f;
        return Color.HSVToColor(new float[]{hue, sat, val});
    }
}