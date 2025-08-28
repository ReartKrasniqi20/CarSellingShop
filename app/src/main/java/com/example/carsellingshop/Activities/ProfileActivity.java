package com.example.carsellingshop.Activities;

import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.carsellingshop.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);

        FirebaseUser u = FirebaseAuth.getInstance().getCurrentUser();
        if (u != null) {
            tvEmail.setText(u.getEmail());
            tvName.setText(u.getDisplayName() != null ? u.getDisplayName() : "User");
        }
    }
}
