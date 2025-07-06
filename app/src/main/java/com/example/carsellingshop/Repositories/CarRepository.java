package com.example.carsellingshop.Repositories;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class CarRepository {

    private final FirebaseFirestore db;

    public CarRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getAllCars(OnSuccessListener<QuerySnapshot> onSuccess, OnFailureListener onFailure) {
        db.collection("cars")
                .get()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(onFailure);
    }
}
