package com.example.finsmart.main_activity.home_page.fund_page;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class FundDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fund_database";
    private static final int DATABASE_VERSION = 2; // Увеличиваем версию БД
    private static final String TABLE_FUND = "fund_table";

    // Названия столбцов
    private static final String COLUMN_FUND_NAME = "fund_name";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_INVESTED_SUM = "invested_sum";
    private static final String COLUMN_DYNAMICS = "dynamics";
    private static final String COLUMN_START_DATE = "start_date";

    // SQL для создания таблицы
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_FUND + " (" +
                    COLUMN_FUND_NAME + " TEXT PRIMARY KEY, " +
                    COLUMN_AMOUNT + " REAL, " +
                    COLUMN_INVESTED_SUM + " REAL, " +
                    COLUMN_DYNAMICS + " REAL, " +
                    COLUMN_START_DATE + " TEXT);";

    public FundDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Добавляем новые столбцы при апгрейде
            db.execSQL("ALTER TABLE " + TABLE_FUND + " ADD COLUMN " + COLUMN_INVESTED_SUM + " REAL;");
        }
    }

    // Добавить фонд
    public void addFund(Fund fund) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FUND_NAME, fund.getFundName());
        values.put(COLUMN_AMOUNT, fund.getAmount());
        values.put(COLUMN_INVESTED_SUM, fund.getInvestedSum());
        values.put(COLUMN_DYNAMICS, fund.getDynamics());
        values.put(COLUMN_START_DATE, fund.getStartDate());

        db.insertWithOnConflict(TABLE_FUND, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Обновить данные о фонде
    public void updateFund(Fund fund) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, fund.getAmount());
        values.put(COLUMN_INVESTED_SUM, fund.getInvestedSum());
        values.put(COLUMN_DYNAMICS, fund.getDynamics());
        values.put(COLUMN_START_DATE, fund.getStartDate());

        db.update(TABLE_FUND, values,
                COLUMN_FUND_NAME + " = ?",
                new String[]{fund.getFundName()});
    }

    // Удалить фонд по имени
    public void deleteFund(Fund fund) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FUND,
                COLUMN_FUND_NAME + " = ?",
                new String[]{fund.getFundName()});
    }

    // Получить все фонды
    public List<Fund> getAllFunds() {
        List<Fund> funds = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_FUND;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String fundName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FUND_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
                double investedSum = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INVESTED_SUM));
                String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));

                funds.add(new Fund(fundName, amount, investedSum, startDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return funds;
    }

    // Получить фонд по имени
    public Fund getFundByName(String fundName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_FUND,
                new String[]{
                        COLUMN_FUND_NAME,
                        COLUMN_AMOUNT,
                        COLUMN_INVESTED_SUM,
                        COLUMN_DYNAMICS,
                        COLUMN_START_DATE
                },
                COLUMN_FUND_NAME + "=?",
                new String[]{fundName},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            Fund fund = new Fund(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FUND_NAME)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INVESTED_SUM)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE))
            );
            cursor.close();
            return fund;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    // Проверить, существует ли фонд с таким именем
    public boolean fundExists(String fundName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_FUND,
                new String[]{COLUMN_FUND_NAME},
                COLUMN_FUND_NAME + " = ?",
                new String[]{fundName},
                null, null, null
        );

        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    // Заполнить тестовыми данными
    public void populateInitialData() {
        List<Fund> sampleFunds = getSampleFunds();
        for (Fund fund : sampleFunds) {
            if (!fundExists(fund.getFundName())) {
                addFund(fund);
            }
        }
    }

    // Вспомогательный метод: создаём тестовые данные
    private List<Fund> getSampleFunds() {
        List<Fund> funds = new ArrayList<>();
        funds.add(new Fund("ПИФ Сбербанк", 290000.0, 268900.0, "19.12.2024"));
        funds.add(new Fund("Альфа Капитал", 150000.0, 145000.0,"01.01.2024"));
        funds.add(new Fund("Открытие Рост", 500000.0, 480000.0, "15.03.2024"));
        return funds;
    }
}