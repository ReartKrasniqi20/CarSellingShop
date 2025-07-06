package com.example.carsellingshop.Services;

import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.Repositories.CarRepository;

import java.util.ArrayList;
import java.util.List;

public class CarService {
    private CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public long addCar(Car car) {
        if (car.getModel() == null || car.getModel().trim().isEmpty() || car.getPrice() < 0) {
            throw new IllegalArgumentException("Model is required and price must be non-negative");
        }
        return carRepository.insertCar(car);
    }

    public List<Car> getAllCars() {
        return carRepository.getAllCars();
    }

    public Car getCarById(int id) {
        Car car = carRepository.getCarById(id);
        if (car == null) {
            throw new IllegalArgumentException("Car not found");
        }
        return car;
    }

    public void updateCar(Car car) {
        if (car.getId() <= 0) {
            throw new IllegalArgumentException("Invalid car ID");
        }
        carRepository.updateCar(car);
    }

    public void deleteCar(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Invalid car ID");
        }
        carRepository.deleteCar(id);
    }

    public List<Car> getCarsWithDiscount() {
        List<Car> cars = carRepository.getAllCars();
        List<Car> discountedCars = new ArrayList<>();
        for (Car car : cars) {
            if (car.getDiscountPercentage() > 0) {
                discountedCars.add(car);
            }
        }
        return discountedCars;
    }
}