package com.example.carsellingshop.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carsellingshop.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditCarActivity extends AppCompatActivity {

    private EditText etModel, etPrice, etDescription, etDiscount, etYear, etMileage, etFuelType, etTransmission, etImageUrl;
    private Button btnSave;

    private FirebaseFirestore db;
    private String carId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_car);

        db = FirebaseFirestore.getInstance();

        // Inputs
        etModel = findViewById(R.id.etModel);
        etPrice = findViewById(R.id.etPrice);
        etDescription = findViewById(R.id.etDescription);
        etDiscount = findViewById(R.id.etDiscount);
        etYear = findViewById(R.id.etYear);
        etMileage = findViewById(R.id.etMileage);
        etFuelType = findViewById(R.id.etFuelType);
        etTransmission = findViewById(R.id.etTransmission);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSave = findViewById(R.id.btnSave);

        // Get intent extras from CarAdapter when admin clicks edit
        carId = getIntent().getStringExtra("CAR_ID");
        etModel.setText(getIntent().getStringExtra("MODEL"));
        etPrice.setText(String.valueOf(getIntent().getDoubleExtra("PRICE", 0.0)));
        etDescription.setText(getIntent().getStringExtra("DESCRIPTION"));
        etDiscount.setText(String.valueOf(getIntent().getIntExtra("DISCOUNT", 0)));
        etYear.setText(String.valueOf(getIntent().getIntExtra("YEAR", 0)));
        etMileage.setText(getIntent().getStringExtra("MILEAGE"));
        etFuelType.setText(getIntent().getStringExtra("FUEL_TYPE"));
        etTransmission.setText(getIntent().getStringExtra("TRANSMISSION"));
        etImageUrl.setText(getIntent().getStringExtra("IMAGE_URL"));

        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void saveChanges() {
        String model = etModel.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String discountStr = etDiscount.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String mileage = etMileage.getText().toString().trim();
        String fuelType = etFuelType.getText().toString().trim();
        String transmission = etTransmission.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(model) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Model and price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int discount = TextUtils.isEmpty(discountStr) ? 0 : Integer.parseInt(discountStr);
        int year = TextUtils.isEmpty(yearStr) ? 0 : Integer.parseInt(yearStr);

        // Put updated fields into a map
        Map<String, Object> updates = new HashMap<>();
        updates.put("model", model);
        updates.put("price", price);
        updates.put("description", description);
        updates.put("discount", discount);
        updates.put("year", year);
        updates.put("mileageKm", mileage);
        updates.put("fuelType", fuelType);
        updates.put("transmission", transmission);
        updates.put("imageUrl", imageUrl);

        DocumentReference carRef = db.collection("cars").document(carId);
        carRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Car updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}