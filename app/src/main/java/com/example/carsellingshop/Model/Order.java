package com.example.carsellingshop.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

public class Order {
    // Document metadata
    private String id;            // Firestore doc id (optional to store in the doc)

    // User / car linkage
    private String userId;        // FirebaseAuth uid
    private String userEmail;     // convenience
    private String carId;         // Firestore car doc id
    private String carModel;      // optional snapshot for quick lists
    private String carImageUrl;   // optional snapshot
    private double price;         // optional snapshot

    // Buyer-provided form fields
    private String name;
    private String phone;
    private String address;

    // Workflow status: "pending" | "confirmed" | "cancelled"
    private String status;

    // Timestamps
    @ServerTimestamp
    private Timestamp createdAt;   // set by server on create
    private Timestamp cancelledAt; // optional, set when cancelling

    public Order() { }

    // ---- Getters / Setters ----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getCarId() { return carId; }
    public void setCarId(String carId) { this.carId = carId; }

    public String getCarModel() { return carModel; }
    public void setCarModel(String carModel) { this.carModel = carModel; }

    public String getCarImageUrl() { return carImageUrl; }
    public void setCarImageUrl(String carImageUrl) { this.carImageUrl = carImageUrl; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(Timestamp cancelledAt) { this.cancelledAt = cancelledAt; }
}