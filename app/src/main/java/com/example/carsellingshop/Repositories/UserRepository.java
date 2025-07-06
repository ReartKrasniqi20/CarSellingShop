package com.example.carsellingshop.Repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.carsellingshop.Database.DatabaseHelper;
import com.example.carsellingshop.Model.User;

public class UserRepository {
    private DatabaseHelper dbHelper;

    public UserRepository(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long insertUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("email", user.getEmail());
        values.put("hashed_password", user.getHashedPassword());
        values.put("user_type", user.getUserType());
        long id = db.insert("users", null, values);
        db.close();
        return id;
    }

    public User getUserByUsername(String username) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        User user = null;
        Cursor cursor = db.query("users", new String[]{"id", "username", "email", "hashed_password", "user_type"},
                "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            user = new User(cursor.getInt(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4));
        }
        cursor.close();
        db.close();
        return user;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("email", user.getEmail());
        values.put("hashed_password", user.getHashedPassword());
        values.put("user_type", user.getUserType());
        db.update("users", values, "id=?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteUser(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("users", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}