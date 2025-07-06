package com.example.carsellingshop.Model;

public class Car {

    private String id;
    private String model;
    private double price;
    private String imageUrl;
    private String description;
    private double discount;

    // Required empty constructor for Firestore
    public Car() { }

    public Car(String id, String model, double price, String imageUrl, String description, double discount) {
        this.id = id;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discount = discount;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
}
