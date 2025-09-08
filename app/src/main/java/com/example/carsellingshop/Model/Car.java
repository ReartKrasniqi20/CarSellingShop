package com.example.carsellingshop.Model;

import com.google.firebase.firestore.Exclude;

public class Car {

    @Exclude
    private String id;

    private String model;
    private double price;
    private String imageUrl;
    private String description;
    private int discount;
    private int year;
    private String mileageKm;
    private String fuelType;
    private String transmission;

    // Required empty constructor for Firestore
    public Car() { }

    public Car(String id, String model, double price, String imageUrl, String description,
               int discount, int year, String mileageKm, String fuelType, String transmission) {

        this.id = id;
        this.model = model;
        this.price = price;
        this.imageUrl = imageUrl;
        this.description = description;
        this.discount = discount;
        this.fuelType = fuelType;
        this.mileageKm = mileageKm;
        this.transmission = transmission;
        this.year = year;
    }


    @Exclude
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

    public int getDiscount() { return discount; }
    public void setDiscount(int discount) { this.discount = discount; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }


    public String getMileageKm() { return mileageKm; }
    public void setMileageKm(String mileageKm) { this.mileageKm = mileageKm; }

    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }

    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
}

