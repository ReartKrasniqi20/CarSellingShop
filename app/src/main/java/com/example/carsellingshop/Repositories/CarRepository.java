package com.example.carsellingshop.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.carsellingshop.Database.DatabaseHelper;
import com.example.carsellingshop.Model.Car;

import java.util.ArrayList;
import java.util.List;

public class CarRepository {
    private DatabaseHelper dbHelper;

    public CarRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertCar(Car car) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("model", car.getModel());
        values.put("price", car.getPrice());
        values.put("image_url", car.getImageUrl());
        values.put("description", car.getDescription());
        values.put("discount_percentage", car.getDiscountPercentage());
        long id = db.insert("cars", null, values);
        db.close();
        return id;
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("cars", new String[]{"id", "model", "price", "image_url", "description", "discount_percentage"},
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                cars.add(new Car(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),
                        cursor.getString(3), cursor.getString(4), cursor.getDouble(5)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return cars;
    }

    public Car getCarById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Car car = null;
        Cursor cursor = db.query("cars", new String[]{"id", "model", "price", "image_url", "description", "discount_percentage"},
                "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            car = new Car(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2),
                    cursor.getString(3), cursor.getString(4), cursor.getDouble(5));
        }
        cursor.close();
        db.close();
        return car;
    }

    public void updateCar(Car car) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("model", car.getModel());
        values.put("price", car.getPrice());
        values.put("image_url", car.getImageUrl());
        values.put("description", car.getDescription());
        values.put("discount_percentage", car.getDiscountPercentage());
        db.update("cars", values, "id=?", new String[]{String.valueOf(car.getId())});
        db.close();
    }

    public void deleteCar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("cars", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}