package com.example.carsellingshop.Model;

public class Car {
    private int id;
    private String model;
    private double price;
    private String imageUrl;
    private String description;
    private double discountPercentage; // e.g., 15 for 15% discount

    public Car(int id, String model, double price, String imageUrl, String description, double discountPercentage) {
        this.id = id;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discountPercentage = discountPercentage;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
}