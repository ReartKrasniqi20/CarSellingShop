package com.example.carsellingshop.Services;

import android.text.TextUtils;

import com.example.carsellingshop.Model.Order;
import com.example.carsellingshop.Repositories.OrderRepository;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Arrays;
import java.util.Locale;

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

        order.setCreatedAt(null);

        return repo.addOrder(order);
    }

    /** Real-time: ACTIVE (pending or confirmed/approved) orders for user. */
    public ListenerRegistration listenToActiveOrdersByUser(
            String userId,
            EventListener<QuerySnapshot> listener
    ) {
        if (TextUtils.isEmpty(userId)) throw new IllegalArgumentException("userId required");


        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereIn("status", Arrays.asList("pending", "confirmed", "approved"))
                .addSnapshotListener(listener);
    }

    /** One-shot: ACTIVE orders for user. */
    public Task<QuerySnapshot> getActiveOrdersByUser(String userId) {
        if (TextUtils.isEmpty(userId)) throw new IllegalArgumentException("userId required");
        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereIn("status", Arrays.asList("pending", "confirmed", "approved"))
                .get();
    }

    /** Check if user has an ACTIVE order for a car. */

    public Task<QuerySnapshot> hasUserOrderedCar(String userId, String carId) {
        if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(carId)) {
            throw new IllegalArgumentException("userId & carId required");
        }
        return db.collection("orders")
                .whereEqualTo("userId", userId)
                .whereEqualTo("carId", carId)
                .whereIn("status", Arrays.asList("pending", "confirmed", "approved"))
                .get();
    }

    /** Cancel ONLY a pending order for this user+car. */
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

  
    /** Utility to normalize any admin-provided status string to canonical value. */
    public static String normalizeStatus(String statusRaw) {
        if (statusRaw == null) return "pending";
        String s = statusRaw.trim().toLowerCase(Locale.US);
        switch (s) {
            case "approved":
            case "approve":
            case "confirm":
            case "confirmed":
                return "confirmed";
            case "cancel":
            case "canceled":
            case "cancelled":
                return "cancelled";
            case "pending":
            default:
                return "pending";
        }

    }

    /** Backward-compat: route old callers to pending-only cancel. */
    public Task<Void> cancelActiveOrder(String uid, String carId) {
        return cancelPendingOrder(uid, carId);
    }

    /** Admin confirm an order by id. */
    public Task<Void> confirmOrderById(String orderId) {
        if (TextUtils.isEmpty(orderId)) throw new IllegalArgumentException("orderId required");
        return db.collection("orders").document(orderId)
                .update("status", "confirmed");
    }
}