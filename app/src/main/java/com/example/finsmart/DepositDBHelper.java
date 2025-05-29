package com.example.finsmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DepositDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "deposit_database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_DEPOSIT = "deposit_table";

    // Названия столбцов
    private static final String COLUMN_BANK_NAME = "bank_name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_INTEREST_RATE = "interest_rate";
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_PROLONGATION = "has_prolongation";
    private static final String COLUMN_CAPITALIZATION = "has_capitalization";

    // SQL для создания таблицы
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_DEPOSIT + " (" +
                    COLUMN_BANK_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_INTEREST_RATE + " REAL, " +
                    COLUMN_START_DATE + " TEXT, " +
                    COLUMN_DURATION + " TEXT, " +
                    COLUMN_PROLONGATION + " INTEGER, " + // 0 = false, 1 = true
                    COLUMN_CAPITALIZATION + " INTEGER);"; // 0 = false, 1 = true

    public DepositDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPOSIT);
        onCreate(db);
    }

    // Добавить депозит
    public void addDeposit(Deposit deposit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BANK_NAME, deposit.getBankName());
        values.put(COLUMN_AMOUNT, deposit.getAmount());
        values.put(COLUMN_INTEREST_RATE, deposit.getInterestRate());
        values.put(COLUMN_START_DATE, deposit.getStartDate());
        values.put(COLUMN_DURATION, deposit.getDuration());
        values.put(COLUMN_PROLONGATION, deposit.isHasProlongation() ? 1 : 0);
        values.put(COLUMN_CAPITALIZATION, deposit.isHasCapitalization() ? 1 : 0);

        db.insertWithOnConflict(TABLE_DEPOSIT, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Обновить депозит
    public void updateDeposit(Deposit deposit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, deposit.getAmount());
        values.put(COLUMN_INTEREST_RATE, deposit.getInterestRate());
        values.put(COLUMN_START_DATE, deposit.getStartDate());
        values.put(COLUMN_DURATION, deposit.getDuration());
        values.put(COLUMN_PROLONGATION, deposit.isHasProlongation() ? 1 : 0);
        values.put(COLUMN_CAPITALIZATION, deposit.isHasCapitalization() ? 1 : 0);

        db.update(TABLE_DEPOSIT, values,
                COLUMN_BANK_NAME + " = ?",
                new String[]{deposit.getBankName()});
    }

    // Удалить депозит
    public void deleteDeposit(Deposit deposit) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DEPOSIT,
                COLUMN_BANK_NAME + " = ?",
                new String[]{deposit.getBankName()});
    }

    // Получить все депозиты
    public List<Deposit> getAllDeposits() {
        List<Deposit> deposits = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_DEPOSIT;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String bankName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANK_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                double interestRate = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INTEREST_RATE));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                String duration = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION));
                boolean hasProlongation = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROLONGATION)) == 1;
                boolean hasCapitalization = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPITALIZATION)) == 1;

                deposits.add(new Deposit(
                        bankName,
                        amount,
                        interestRate,
                        startDate,
                        duration,
                        hasProlongation,
                        hasCapitalization
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return deposits;
    }

    // Получить депозит по названию банка
    public Deposit getDepositByBankName(String bankName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_DEPOSIT,
                new String[]{
                        COLUMN_BANK_NAME,
                        COLUMN_AMOUNT,
                        COLUMN_INTEREST_RATE,
                        COLUMN_START_DATE,
                        COLUMN_DURATION,
                        COLUMN_PROLONGATION,
                        COLUMN_CAPITALIZATION
                },
                COLUMN_BANK_NAME + "=?",
                new String[]{bankName},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Deposit deposit = new Deposit(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BANK_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INTEREST_RATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DURATION)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROLONGATION)) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAPITALIZATION)) == 1
            );
            cursor.close();
            return deposit;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    // Проверить, существует ли депозит
    public boolean depositExists(String bankName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_DEPOSIT,
                new String[]{COLUMN_BANK_NAME},
                COLUMN_BANK_NAME + " = ?",
                new String[]{bankName},
                null, null, null
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Заполнить тестовыми данными
    public void populateInitialData() {
        List<Deposit> sampleDeposits = getSampleDeposits();
        for (Deposit deposit : sampleDeposits) {
            if (!depositExists(deposit.getBankName())) {
                addDeposit(deposit);
            }
        }
    }

    // Вспомогательный метод: создаём тестовые данные
    private List<Deposit> getSampleDeposits() {
        List<Deposit> deposits = new ArrayList<>();
        // Добавляем депозиты вручную
        deposits.add(new Deposit("Сбербанк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Т-Банк", 500000, 18.5, "01.01.2025", "12 месяцев", false, true));
        deposits.add(new Deposit("ВТБ", 150000, 16.0, "15.11.2024", "3 месяца", true, false));
        deposits.add(new Deposit("Альфа-Банк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Газпромбанк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Райффайзен Банк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        deposits.add(new Deposit("Райффайзен Бунк", 290000, 17.0, "19.12.2024", "6 месяцев", true, true));
        return deposits;
    }
}