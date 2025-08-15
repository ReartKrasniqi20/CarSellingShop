package com.example.carsellingshop.Activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
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
    private final List<Car> carList = new ArrayList<>();
    private final CarService carService = new CarService();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.topToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CAR SELLING SHOP IDA");
        }

        // Use the field, not a new local variable
        carRecyclerView = findViewById(R.id.carRecyclerView);
        carRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        carAdapter = new CarAdapter(carList);
        carRecyclerView.setAdapter(carAdapter);

        loadCars();
    }

    private void loadCars() {
        carService.fetchAllCars(querySnapshot -> {
            carList.clear();
            for (QueryDocumentSnapshot doc : querySnapshot) {
                carList.add(doc.toObject(Car.class));
            }
            carAdapter.notifyDataSetChanged();
        }, e -> Toast.makeText(this, "Failed to load cars: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}
