package com.example.carsellingshop.Services;

import android.text.TextUtils;

import com.example.carsellingshop.Model.Order;
import com.example.carsellingshop.Repositories.OrderRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;

public class OrderService {
    private final OrderRepository repo;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public OrderService(OrderRepository repo) {
        this.repo = repo;
    }

    /** Create a NEW order as PENDING. */
    public Task<String> placeOrder(Order order) {
        if (order == null) throw new IllegalArgumentException("Order is null");
        if (TextUtils.isEmpty(order.getUserId())) throw new IllegalArgumentException("userId required");
        if (TextUtils.isEmpty(order.getCarId())) throw new IllegalArgumentException("carId required");

        // Force workflow state to pending on create
        order.setStatus("pending");
        // Let Firestore fill @ServerTimestamp
        order.setCreatedAt(null);

        return repo.addOrder(order);
    }

    /** (Legacy) Get all orders by user (any status). */
    public Task<QuerySnapshot> getOrdersByUser(String userId) {
        if (TextUtils.isEmpty(userId)) throw new IllegalArgumentException("userId required");
        return repo.getOrdersByUser(userId);
    }

    /** Get ACTIVE orders (pending or confirmed) for UI state rebuild. */
    public Task<QuerySnapshot> getActiveOrdersByUser(String userId) {
        if (TextUtils.isEmpty(userId)) throw new IllegalArgumentException("userId required");
        // Use Firestore directly to ensure status filter even if repo lacks it
        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereIn("status", Arrays.asList("pending", "confirmed"))
                .get();
    }

    /** Check if the user currently has an ACTIVE order for a car (pending/confirmed). */
    public Task<QuerySnapshot> hasUserOrderedCar(String userId, String carId) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(carId)) {
            throw new IllegalArgumentException("userId & carId required");
        }
        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", carId)
                .whereIn("status", Arrays.asList("pending", "confirmed"))
                .get();
    }

    /** Cancel ONLY a pending order for this user+car (no-op if none). */
    public Task<Void> cancelPendingOrder(String uid, String carId) {
        if (TextUtils.isEmpty(uid) || TextUtils.isEmpty(carId)) {
            throw new IllegalArgumentException("uid & carId required");
        }
        return db.collection("orders")
                .whereEqualTo("userId", uid)
                .whereEqualTo("carId", carId)
                .whereEqualTo("status", "pending")
                .limit(1)
                .get()
                .onSuccessTask(qs -> {
                    if (qs.isEmpty()) return Tasks.forResult(null);
                    DocumentReference ref = qs.getDocuments().get(0).getReference();
                    return ref.update("status", "cancelled",
                            "cancelledAt", FieldValue.serverTimestamp());
                });
    }

    /** Backward-compat: route old callers to pending-only cancel. */
    public Task<Void> cancelActiveOrder(String uid, String carId) {
        return cancelPendingOrder(uid, carId);
    }

    /** (Optional) Admin confirm an order by id. */
    public Task<Void> confirmOrderById(String orderId) {
        if (TextUtils.isEmpty(orderId)) throw new IllegalArgumentException("orderId required");
        return db.collection("orders").document(orderId)
                .update("status", "confirmed");
    }
}