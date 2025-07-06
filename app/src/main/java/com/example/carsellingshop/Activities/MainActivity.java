package com.example.carsellingshop.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;  // ✅ Correct Toolbar import
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsellingshop.Adapters.CarAdapter;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;
import com.example.carsellingshop.Services.CarService;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView carRecyclerView;
    private CarAdapter carAdapter;
    private List<Car> carList = new ArrayList<>();
    private CarService carService = new CarService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CAR SELLING SHOP IDA");
        }

        // Set up RecyclerView
        carRecyclerView = findViewById(R.id.carRecyclerView);
        carRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        carAdapter = new CarAdapter(carList);
        carRecyclerView.setAdapter(carAdapter);

        // Load data
        loadCars();
    }

    private void loadCars() {
        carService.fetchAllCars(querySnapshot -> {
            carList.clear();
            for (QueryDocumentSnapshot document : querySnapshot) {
                Car car = document.toObject(Car.class);
                carList.add(car);
            }
            carAdapter.notifyDataSetChanged();
        }, e -> Toast.makeText(this, "Failed to load cars: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
