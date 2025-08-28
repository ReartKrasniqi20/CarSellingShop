package com.example.carsellingshop.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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

public class ProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);   // make sure this is the cleaned XML with topToolbar + drawer


        Toolbar toolbar = findViewById(R.id.topToolbar1); // id must match your XML
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Profile");

        // ----- Drawer -----
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        // hamburger opens the drawer
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
            } else if (id == R.id.nav_favorites) {
                // placeholder for future favorites screen
                // Toast.makeText(this, "Favorites (coming soon)", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_profile) {
                // already here
                drawerLayout.closeDrawers();
                return true;
            } else if (id == R.id.nav_aboutus) { // ensure this id matches your drawer_menu.xml
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
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        TextView tvAvatarInitial = findViewById(R.id.tvAvatarInitial);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            // name/email
            String display = (u.getDisplayName() != null && !u.getDisplayName().isEmpty())
                    ? u.getDisplayName()
                    : (u.getEmail() != null ? u.getEmail() : "User");
            tvName.setText(display);
            tvEmail.setText(u.getEmail() != null ? u.getEmail() : "—");

            // initial letter
            char initial = Character.toUpperCase(display.trim().isEmpty() ? 'U' : display.trim().charAt(0));
            tvAvatarInitial.setText(String.valueOf(initial));

            // stable color from uid/email
            String key = (u.getUid() != null) ? u.getUid() : display;
            int color = pickStableColor(key);

            // tint the circular background
            GradientDrawable bg = (GradientDrawable) findViewById(R.id.avatarContainer)
                    .getBackground().mutate();
            bg.setColor(color);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // auth guard: if signed out somehow, bounce to login
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

    // helper: generate a nice, stable color from a string
    private int pickStableColor(String s) {
        int hash = s.hashCode();
        float hue = (hash & 0xFFFFFF) % 360;  // 0..359
        float sat = 0.55f;
        float val = 0.85f;
        return Color.HSVToColor(new float[]{hue, sat, val});
    }
}
