package com.example.carsellingshop.Database;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.carsellingshop.Model.User;
import com.example.carsellingshop.Model.Car;
import com.example.carsellingshop.Model.Order;

    public class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "CarShop.db";
        private static final int DATABASE_VERSION = 1;

        // Table names
        private static final String TABLE_USERS = "users";
        private static final String TABLE_CARS = "cars";
        private static final String TABLE_ORDERS = "orders";

        // Common column names
        private static final String COLUMN_ID = "id";

        // Users table columns
        private static final String COLUMN_USERNAME = "username";
        private static final String COLUMN_EMAIL = "email";
        private static final String COLUMN_HASHED_PASSWORD = "hashed_password";
        private static final String COLUMN_USER_TYPE = "user_type";

        // Cars table columns
        private static final String COLUMN_MODEL = "model";
        private static final String COLUMN_PRICE = "price";
        private static final String COLUMN_IMAGE_URL = "image_url";
        private static final String COLUMN_DESCRIPTION = "description";
        private static final String COLUMN_DISCOUNT_PERCENTAGE = "discount_percentage";

        // Orders table columns
        private static final String COLUMN_USER_ID = "user_id";
        private static final String COLUMN_CAR_ID = "car_id";
        private static final String COLUMN_ORDER_DATE = "order_date";
        private static final String COLUMN_STATUS = "status";

        // Create table statements
        private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL, " +
                COLUMN_EMAIL + " TEXT NOT NULL, " +
                COLUMN_HASHED_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USER_TYPE + " TEXT NOT NULL);";

        private static final String CREATE_TABLE_CARS = "CREATE TABLE " + TABLE_CARS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_MODEL + " TEXT NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_IMAGE_URL + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_DISCOUNT_PERCENTAGE + " REAL DEFAULT 0);";

        private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER NOT NULL, " +
                COLUMN_CAR_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_DATE + " TEXT NOT NULL, " +
                COLUMN_STATUS + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_CAR_ID + ") REFERENCES " + TABLE_CARS + "(" + COLUMN_ID + "));";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create tables
            db.execSQL(CREATE_TABLE_USERS);
            db.execSQL(CREATE_TABLE_CARS);
            db.execSQL(CREATE_TABLE_ORDERS);

            // Pre-populate some data
            // Add a default admin user (password will be hashed later)
            db.execSQL("INSERT INTO users (username, email, hashed_password, user_type) VALUES ('admin', 'admin@example.com', 'admin123', 'admin');");
            // Add some cars (like in your mockup)
            db.execSQL("INSERT INTO cars (model, price, image_url, description, discount_percentage) VALUES ('Audi S6', 72000, 'audi_s6_image_url', 'Such an impressive car with all required', 15);");
            db.execSQL("INSERT INTO cars (model, price, image_url, description, discount_percentage) VALUES ('BMW 3 Series', 45000, 'bmw_3series_image_url', 'Reliable and stylish', 0);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop tables and recreate if the database version changes
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CARS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

