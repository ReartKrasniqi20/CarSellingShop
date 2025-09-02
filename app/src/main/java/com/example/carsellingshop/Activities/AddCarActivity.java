package com.example.carsellingshop.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddCarActivity extends AppCompatActivity {

    private EditText etModel, etPrice, etDiscount, etDescription, etImageUrl,
            etYear, etMileage, etFuelType, etTransmission;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        etModel = findViewById(R.id.etModel);
        etPrice = findViewById(R.id.etPrice);
        etDiscount = findViewById(R.id.etDiscount);
        etYear = findViewById(R.id.etYear);
        etMileage = findViewById(R.id.etMileage);
        etFuelType = findViewById(R.id.etFuelType);
        etTransmission = findViewById(R.id.etTransmission);
        etDescription = findViewById(R.id.etDescription);
        etImageUrl = findViewById(R.id.etImageUrl);
        btnSave = findViewById(R.id.btnSaveCar);

        btnSave.setOnClickListener(v -> saveCar());
    }

    private void saveCar() {
        String model = etModel.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String discountStr = etDiscount.getText().toString().trim();
        String yearStr = etYear.getText().toString().trim();
        String mileage = etMileage.getText().toString().trim();
        String fuelType = etFuelType.getText().toString().trim();
        String transmission = etTransmission.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String imageUrl = etImageUrl.getText().toString().trim();

        if (TextUtils.isEmpty(model) || TextUtils.isEmpty(priceStr)) {
            Toast.makeText(this, "Model and Price are required", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);
        int discount = TextUtils.isEmpty(discountStr) ? 0 : Integer.parseInt(discountStr);
        int year = TextUtils.isEmpty(yearStr) ? 0 : Integer.parseInt(yearStr);

        // 🚀 Don’t set ID here — Firestore generates it
        Car car = new Car(
                null, model, price, imageUrl, description,
                discount, year, mileage, fuelType, transmission
        );

        FirebaseFirestore.getInstance()
                .collection("cars")
                .add(car)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Car added", Toast.LENGTH_SHORT).show();
                    finish(); // close after saving
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}