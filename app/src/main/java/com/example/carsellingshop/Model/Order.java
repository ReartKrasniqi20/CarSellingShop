package com.example.carsellingshop.Model;

public class Order {
    private int id;
    private int userId;
    private int carId;
    private String orderDate;
    private String status; // e.g., "pending", "completed", "canceled"

    public Order(int id, int userId, int carId, String orderDate, String status) {
        this.id = id;
        this.userId = userId;
        this.carId = carId;
        this.orderDate = orderDate;
        this.status = status;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}