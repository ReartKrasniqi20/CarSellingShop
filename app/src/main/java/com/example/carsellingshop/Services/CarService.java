package com.example.carsellingshop.Services;

import com.example.carsellingshop.Repositories.CarRepository;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.QuerySnapshot;

public class CarService {

    private final CarRepository carRepository;

    public CarService() {
        carRepository = new CarRepository();
    }

    public void fetchAllCars(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        carRepository.getAllCars(onSuccess, onFailure);
    }
}
