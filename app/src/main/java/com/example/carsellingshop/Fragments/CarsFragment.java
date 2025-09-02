package com.example.carsellingshop.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carsellingshop.Activities.AddCarActivity;
import com.example.carsellingshop.Adapters.CarAdapter;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CarsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarAdapter adapter;
    private final List<Car> carsList = new ArrayList<>();
    private ListenerRegistration registration;
    private FloatingActionButton fabAddCar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cars, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewCars);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CarAdapter(carsList, true);
        recyclerView.setAdapter(adapter);

        fabAddCar = view.findViewById(R.id.fabAddCar);
        fabAddCar.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddCarActivity.class)));

        adapter.setOnDeleteClickListener(car -> {
            FirebaseFirestore.getInstance()
                    .collection("cars")
                    .document(car.getId())
                    .delete()
                    .addOnSuccessListener(unused ->
                            Toast.makeText(getContext(), "Car deleted", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });

        loadCars();

        return view;
    }

    private void loadCars() {
        registration = FirebaseFirestore.getInstance()
                .collection("cars")
                .addSnapshotListener((snap, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    carsList.clear();
                    if (snap != null) {
                        for (DocumentSnapshot doc : snap.getDocuments()) {
                            Car car = doc.toObject(Car.class);
                            if (car != null) {
                                car.setId(doc.getId());
                                carsList.add(car);
                            }
                        }
                    }
                    adapter.replaceData(carsList);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (registration != null) registration.remove();
    }
}