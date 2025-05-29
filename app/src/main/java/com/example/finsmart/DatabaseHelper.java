package com.example.finsmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_PASSWORD_HASH = "password_hash";

    // SQL для создания таблицы
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_PHONE + " TEXT PRIMARY KEY NOT NULL, " +
                    COLUMN_PASSWORD_HASH + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public boolean registerUser(String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String hashedPassword = HashUtils.sha256(password);

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phone});

        if (cursor.getCount() > 0) {
            // Пользователь уже существует
            cursor.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_PHONE, phone);
        values.put(COLUMN_PASSWORD_HASH, hashedPassword);

        long result = db.insert(TABLE_USERS, null, values);
        cursor.close();
        return result != -1;
    }

    public boolean checkUser(String phone, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = HashUtils.sha256(password);

        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " +
                COLUMN_PHONE + " = ? AND " +
                COLUMN_PASSWORD_HASH + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{phone, hashedPassword});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    public boolean isPhoneExists(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_PHONE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{phone});

        // Проверяем, есть ли строки в результатах запроса
        boolean exists = cursor.getCount() > 0;

        cursor.close();
        return exists;
    }

    public boolean updateUserPassword(String phone, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        String newHashedPassword = HashUtils.sha256(newPassword);

        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD_HASH, newHashedPassword);

        // Обновляем запись где phone равен указанному номеру
        int rowsAffected = db.update(
                TABLE_USERS,
                values,
                COLUMN_PHONE + " = ?",
                new String[]{phone}
        );

        return rowsAffected > 0;
    }
}