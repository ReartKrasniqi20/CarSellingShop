package com.example.carsellingshop.Repositories;

import com.example.carsellingshop.Model.Order;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class OrderRepository {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<String> addOrder(Order order) {
        DocumentReference ref = db.collection("orders").document(); // auto id
        order.setId(ref.getId());
        return ref.set(order).continueWith(task -> ref.getId());
    }
}