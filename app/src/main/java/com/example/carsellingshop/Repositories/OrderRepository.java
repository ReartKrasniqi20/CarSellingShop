package com.example.carsellingshop.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.carsellingshop.Database.DatabaseHelper;
import com.example.carsellingshop.Model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private DatabaseHelper dbHelper;

    public OrderRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("car_id", order.getCarId());
        values.put("order_date", order.getOrderDate());
        values.put("status", order.getStatus());
        long id = db.insert("orders", null, values);
        db.close();
        return id;
    }

    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("orders", new String[]{"id", "user_id", "car_id", "order_date", "status"},
                "user_id=?", new String[]{String.valueOf(userId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                orders.add(new Order(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),
                        cursor.getString(3), cursor.getString(4)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return orders;
    }

    public Order getOrderById(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Order order = null;
        Cursor cursor = db.query("orders", new String[]{"id", "user_id", "car_id", "order_date", "status"},
                "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToFirst()) {
            order = new Order(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2),
                    cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        db.close();
        return order;
    }

    public void updateOrder(Order order) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("user_id", order.getUserId());
        values.put("car_id", order.getCarId());
        values.put("order_date", order.getOrderDate());
        values.put("status", order.getStatus());
        db.update("orders", values, "id=?", new String[]{String.valueOf(order.getId())});
        db.close();
    }

    public void deleteOrder(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("orders", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}