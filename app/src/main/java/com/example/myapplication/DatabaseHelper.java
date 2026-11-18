package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "appointments.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Tao bang users
        String createUsers = "CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL)";

        db.execSQL(createUsers);

        // Tao bang appointments
        String createAppointments = "CREATE TABLE appointments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "date TEXT NOT NULL, " +
                "time TEXT NOT NULL, " +
                "reminder INTEGER NOT NULL)";

        db.execSQL(createAppointments);

        // Them tai khoan mau
        ContentValues user = new ContentValues();
        user.put("username", "admin");
        user.put("password", "123456");
        db.insert("users", null, user);

        // Them lich hen mau
        insertSample(db, "Họp nhóm đồ án", "Phòng B203",
                "2026-10-25", "09:00", 1);

        insertSample(db, "Khám răng", "Nha khoa Minh Tâm",
                "2026-10-26", "14:30", 1);

        insertSample(db, "Đi cà phê với bạn", "Highland Coffee",
                "2026-10-27", "19:00", 0);
    }

    private void insertSample(SQLiteDatabase db, String title,
                              String location, String date,
                              String time, int reminder) {

        ContentValues values = new ContentValues();

        values.put("title", title);
        values.put("location", location);
        values.put("date", date);
        values.put("time", time);
        values.put("reminder", reminder);

        db.insert("appointments", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS appointments");
        onCreate(db);
    }

    // Kiem tra dang nhap
    public boolean checkLogin(String username, String password) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "users",
                new String[]{"id"},
                "username = ? AND password = ?",
                new String[]{username, password},
                null, null, null
        );

        boolean result = cursor.moveToFirst();

        cursor.close();

        return result;
    }

    // Them tai khoan
    public long insertUser(String username, String password) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);

        return db.insert("users", null, values);
    }

    // Them lich hen
    public long insertAppointment(Appointment a) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("title", a.getTitle());
        values.put("location", a.getLocation());
        values.put("date", a.getDate());
        values.put("time", a.getTime());
        values.put("reminder", a.isReminder() ? 1 : 0);

        return db.insert("appointments", null, values);
    }

    // Lay tat ca lich hen
    public ArrayList<Appointment> getAllAppointments() {

        ArrayList<Appointment> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM appointments ORDER BY date ASC, time ASC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                Appointment a = new Appointment(
                        cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        cursor.getString(cursor.getColumnIndexOrThrow("location")),
                        cursor.getString(cursor.getColumnIndexOrThrow("date")),
                        cursor.getString(cursor.getColumnIndexOrThrow("time")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("reminder")) == 1
                );

                list.add(a);

            } while (cursor.moveToNext());
        }

        cursor.close();

        return list;
    }

    // Lay lich hen theo ID
    public Appointment getAppointmentById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "appointments",
                null,
                "id = ?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        Appointment a = null;

        if (cursor.moveToFirst()) {
            a = new Appointment(
                    cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("location")),
                    cursor.getString(cursor.getColumnIndexOrThrow("date")),
                    cursor.getString(cursor.getColumnIndexOrThrow("time")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("reminder")) == 1
            );
        }

        cursor.close();

        return a;
    }

    // Cap nhat trang thai nhac nho
    public void updateReminder(int id, boolean on) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("reminder", on ? 1 : 0);

        db.update(
                "appointments",
                values,
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Xoa lich hen
    public void deleteAppointment(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(
                "appointments",
                "id = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // Dem tong lich hen
    public int countAll() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM appointments",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }

    // Dem lich hen dang bat nhac nho
    public int countReminders() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM appointments WHERE reminder = 1",
                null
        );

        int count = 0;

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }

        cursor.close();

        return count;
    }
}
