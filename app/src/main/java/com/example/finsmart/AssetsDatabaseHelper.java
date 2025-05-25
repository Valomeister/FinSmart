package com.example.finsmart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AssetsDatabaseHelper extends SQLiteOpenHelper {
    public AssetsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String DATABASE_NAME = "client_assets.db";
    private static final int DATABASE_VERSION = 1;

    // Общая таблица для всех активов
    private static final String TABLE_ASSETS = "assets";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type"; // deposit, fund, stock, currency, crypto
    private static final String COLUMN_CLIENT_ID = "client_id";
    private static final String COLUMN_AMOUNT = "amount";
    private static final String COLUMN_SUM = "sum";
    private static final String COLUMN_PURCHASE_DATE = "purchase_date";

    // Таблица для вкладов
    private static final String TABLE_DEPOSITS = "deposits";
    private static final String COLUMN_BANK = "bank";
    private static final String COLUMN_RATE = "rate";
    private static final String COLUMN_END_DATE = "end_date";

    // Таблица для фондов
    private static final String TABLE_FUNDS = "funds";
    private static final String COLUMN_FUND_NAME = "fund_name";
    private static final String COLUMN_YEAR_DYNAMIC = "year_dynamic";
    private static final String COLUMN_PURCHASE_DYNAMIC = "purchase_dynamic";

    // Таблица для акций
    private static final String TABLE_STOCKS = "stocks";
    private static final String COLUMN_COMPANY = "company";
    private static final String COLUMN_QUANTITY = "quantity";

    // Таблица для валют
    private static final String TABLE_CURRENCIES = "currencies";
    private static final String COLUMN_CURRENCY_NAME = "currency_name";
    private static final String COLUMN_EXCHANGE_RATE = "exchange_rate";

    // Таблица для криптовалют
    private static final String TABLE_CRYPTO = "crypto";
    private static final String COLUMN_CRYPTO_NAME = "crypto_name";
    private static final String COLUMN_CRYPTO_RATE = "crypto_rate";

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создаем общую таблицу активов
        String createAssetsTable = "CREATE TABLE " + TABLE_ASSETS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TYPE + " TEXT NOT NULL, " +
                COLUMN_CLIENT_ID + " INTEGER NOT NULL, " +
                COLUMN_AMOUNT + " REAL, " +
                COLUMN_SUM + " REAL, " +
                COLUMN_PURCHASE_DATE + " TEXT)";
        db.execSQL(createAssetsTable);

        // Создаем таблицу вкладов
        String createDepositsTable = "CREATE TABLE " + TABLE_DEPOSITS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_BANK + " TEXT NOT NULL, " +
                COLUMN_RATE + " REAL NOT NULL, " +
                COLUMN_END_DATE + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_ASSETS + "(" + COLUMN_ID + "))";
        db.execSQL(createDepositsTable);

        // Создаем таблицу фондов
        String createFundsTable = "CREATE TABLE " + TABLE_FUNDS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_FUND_NAME + " TEXT NOT NULL, " +
                COLUMN_YEAR_DYNAMIC + " REAL, " +
                COLUMN_PURCHASE_DYNAMIC + " REAL, " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_ASSETS + "(" + COLUMN_ID + "))";
        db.execSQL(createFundsTable);

        // Создаем таблицу акций
        String createStocksTable = "CREATE TABLE " + TABLE_STOCKS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_COMPANY + " TEXT NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_YEAR_DYNAMIC + " REAL, " +
                COLUMN_PURCHASE_DYNAMIC + " REAL, " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_ASSETS + "(" + COLUMN_ID + "))";
        db.execSQL(createStocksTable);

        // Создаем таблицу валют
        String createCurrenciesTable = "CREATE TABLE " + TABLE_CURRENCIES + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CURRENCY_NAME + " TEXT NOT NULL, " +
                COLUMN_EXCHANGE_RATE + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_ASSETS + "(" + COLUMN_ID + "))";
        db.execSQL(createCurrenciesTable);

        // Создаем таблицу криптовалют
        String createCryptoTable = "CREATE TABLE " + TABLE_CRYPTO + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_CRYPTO_NAME + " TEXT NOT NULL, " +
                COLUMN_CRYPTO_RATE + " REAL NOT NULL, " +
                COLUMN_YEAR_DYNAMIC + " REAL, " +
                COLUMN_PURCHASE_DYNAMIC + " REAL, " +
                "FOREIGN KEY(" + COLUMN_ID + ") REFERENCES " + TABLE_ASSETS + "(" + COLUMN_ID + "))";
        db.execSQL(createCryptoTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSETS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEPOSITS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FUNDS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CRYPTO);
        onCreate(db);
    }
}