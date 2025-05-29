package com.example.finsmart;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class CryptoDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "crypto_database";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_CRYPTO = "crypto_table";
    private static final String COLUMN_SYMBOL = "symbol";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_BUY_IN_PRICE = "buy_in_price";
    private static final String COLUMN_CURRENT_PRICE = "current_price";
    private static final String COLUMN_PURCHASE_DATE = "purchase_date";

    // SQL для создания таблицы
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_CRYPTO + " (" +
                    COLUMN_SYMBOL + " TEXT PRIMARY KEY, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_QUANTITY + " REAL, " +
                    COLUMN_BUY_IN_PRICE + " REAL, " +
                    COLUMN_CURRENT_PRICE + " REAL, " +
                    COLUMN_PURCHASE_DATE + " TEXT);";

    public CryptoDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CRYPTO);
        onCreate(db);
    }

    // Добавить крипту
    public void addCrypto(Crypto crypto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SYMBOL, crypto.getSymbol());
        values.put(COLUMN_NAME, crypto.getName());
        values.put(COLUMN_QUANTITY, crypto.getQuantity());
        values.put(COLUMN_BUY_IN_PRICE, crypto.getBuyInPrice());
        values.put(COLUMN_CURRENT_PRICE, crypto.getCurrentPrice());
        values.put(COLUMN_PURCHASE_DATE, crypto.getPurchaseDate());

        db.insertWithOnConflict(TABLE_CRYPTO, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    // Обновить цену крипты
    public void updateCryptoPrice(String symbol, double currentPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_PRICE, currentPrice);
        db.update(TABLE_CRYPTO, values, COLUMN_SYMBOL + " = ?", new String[]{symbol});
    }

    // Обновить количество крипты
    public void updateCryptoQuantity(String symbol, double quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QUANTITY, quantity);
        db.update(TABLE_CRYPTO, values, COLUMN_SYMBOL + " = ?", new String[]{symbol});
    }


    // Обновить цену крипты
    public void updateCryptoCurrentPrice(String symbol, double currentPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CURRENT_PRICE, currentPrice);
        db.update(TABLE_CRYPTO, values, COLUMN_SYMBOL + " = ?", new String[]{symbol});
    }

    // Обновить цену покупки крипты
    public void updateCryptoBuyPrice(String symbol, double buyInPrice) {
        if (buyInPrice < 0) {
            throw new IllegalArgumentException("Buy-in price must be >= 0");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_BUY_IN_PRICE, buyInPrice);

        db.update(
                TABLE_CRYPTO,
                values,
                COLUMN_SYMBOL + " = ?",
                new String[]{symbol}
        );
    }

    // Обновить количество крипты
    public void updateCryptoPurchaseDate(String symbol, String purchaseDate) {
        if (purchaseDate == null || purchaseDate.isEmpty()) {
            throw new IllegalArgumentException("Purchase date cannot be null or empty");
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PURCHASE_DATE, purchaseDate);

        db.update(
                TABLE_CRYPTO,
                values,
                COLUMN_SYMBOL + " = ?",
                new String[]{symbol}
        );
    }

    // Получить все крипты
    public List<Crypto> getAllCryptos() {
        List<Crypto> cryptos = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_CRYPTO;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String symbol = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL));
                double quantity = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double buyInPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUY_IN_PRICE));
                double currentPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PRICE));
                String purchaseDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE));

                cryptos.add(new Crypto(name, symbol, quantity, buyInPrice, currentPrice, purchaseDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return cryptos;
    }

    // Получить крипту по символу
    public Crypto getCryptoByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CRYPTO,
                new String[]{
                        COLUMN_SYMBOL,
                        COLUMN_NAME,
                        COLUMN_QUANTITY,
                        COLUMN_BUY_IN_PRICE,
                        COLUMN_CURRENT_PRICE,
                        COLUMN_PURCHASE_DATE
                },
                COLUMN_SYMBOL + "=?",
                new String[]{name},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Crypto crypto = new Crypto(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUY_IN_PRICE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE))
            );
            cursor.close();
            return crypto;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // Получить крипту по названию
    public Crypto getCryptoBySymbol(String symbol) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CRYPTO,
                new String[]{
                        COLUMN_SYMBOL,
                        COLUMN_NAME,
                        COLUMN_QUANTITY,
                        COLUMN_BUY_IN_PRICE,
                        COLUMN_CURRENT_PRICE,
                        COLUMN_PURCHASE_DATE
                },
                COLUMN_SYMBOL + "=?",
                new String[]{symbol},
                null, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            Crypto crypto = new Crypto(
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SYMBOL)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BUY_IN_PRICE)),
                    cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_CURRENT_PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PURCHASE_DATE))
            );
            cursor.close();
            return crypto;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    public boolean cryptoExists(String symbol) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CRYPTO, new String[]{COLUMN_SYMBOL},
                COLUMN_SYMBOL + " = ?", new String[]{symbol}, null, null, null);
        boolean exists = cursor.moveToFirst();
        cursor.close();
        return exists;
    }

    public void populateInitialData() {
        List<Crypto> sampleCryptos = getSampleCryptos();

        for (Crypto crypto : sampleCryptos) {
            // Проверяем, существует ли запись по символу
            Crypto existing = getCryptoBySymbol(crypto.getSymbol());

            if (existing == null) {
                // Если не существует — добавляем как новую
                addCrypto(crypto);
            } else {
                // Или обновляем существующую (по желанию)
                updateCryptoData(crypto);
            }
        }
    }

    // Вспомогательный метод: создаём тестовые данные
    private List<Crypto> getSampleCryptos() {
        ArrayList<Crypto> cryptos = new ArrayList<>();

        cryptos.add(new Crypto("Bitcoin", "BTC", 0.01123112, 2_500_000, 2_700_000, "15.11.2024"));
        cryptos.add(new Crypto("Ethereum", "ETH", 0.25548942, 160_000, 180_000, "10.01.2025"));
        cryptos.add(new Crypto("Solana", "SOL", 3.15125436, 8_000, 9_000, "05.02.2025"));
        cryptos.add(new Crypto("Toncoin", "TON", 950.35239124, 150, 110, "19.12.2024"));

        return cryptos;
    }

    // Опционально: метод обновления данных
    private void updateCryptoData(Crypto crypto) {
        updateCryptoQuantity(crypto.getSymbol(), crypto.getQuantity());
        updateCryptoBuyPrice(crypto.getSymbol(), crypto.getBuyInPrice());
        updateCryptoCurrentPrice(crypto.getSymbol(), crypto.getCurrentPrice());
        updateCryptoPurchaseDate(crypto.getSymbol(), crypto.getPurchaseDate());
    }
}