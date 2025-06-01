package com.example.finsmart.main_activity.budget_page;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class BudgetDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "budget_db";
    private static final int DATABASE_VERSION = 1;

    // Бюджет
    public static final String TABLE_BUDGET = "budget_table";
    public static final String COLUMN_BUDGET_ID = "_id";
    public static final String COLUMN_MONTH = "month";

    // Доходы
    public static final String TABLE_INCOME_ENTRY = "income_entry_table";
    public static final String COLUMN_INCOME_ID = "_id";
    public static final String COLUMN_INCOME_BUDGET_ID = "budget_id";
    public static final String COLUMN_INCOME_NAME = "name";
    public static final String COLUMN_INCOME_AMOUNT = "amount";

    // Расходы
    public static final String TABLE_EXPENSE_ENTRY = "expense_entry_table";
    public static final String COLUMN_EXPENSE_ID = "_id";
    public static final String COLUMN_EXPENSE_BUDGET_ID = "budget_id";
    public static final String COLUMN_EXPENSE_NAME = "name";
    public static final String COLUMN_EXPENSE_AMOUNT = "amount";

    // SQL создания таблиц
    private static final String CREATE_TABLE_BUDGET =
            "CREATE TABLE " + TABLE_BUDGET + "(" +
                    COLUMN_BUDGET_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_MONTH + " TEXT NOT NULL UNIQUE" +
                    ")";

    private static final String CREATE_TABLE_INCOME_ENTRY =
            "CREATE TABLE " + TABLE_INCOME_ENTRY + "(" +
                    COLUMN_INCOME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_INCOME_BUDGET_ID + " INTEGER NOT NULL," +
                    COLUMN_INCOME_NAME + " TEXT NOT NULL," +
                    COLUMN_INCOME_AMOUNT + " REAL NOT NULL," +
                    "FOREIGN KEY(" + COLUMN_INCOME_BUDGET_ID + ") REFERENCES " +
                    TABLE_BUDGET + "(" + COLUMN_BUDGET_ID + ")" +
                    ")";

    private static final String CREATE_TABLE_EXPENSE_ENTRY =
            "CREATE TABLE " + TABLE_EXPENSE_ENTRY + "(" +
                    COLUMN_EXPENSE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COLUMN_EXPENSE_BUDGET_ID + " INTEGER NOT NULL," +
                    COLUMN_EXPENSE_NAME + " TEXT NOT NULL," +
                    COLUMN_EXPENSE_AMOUNT + " REAL NOT NULL," +
                    "FOREIGN KEY(" + COLUMN_EXPENSE_BUDGET_ID + ") REFERENCES " +
                    TABLE_BUDGET + "(" + COLUMN_BUDGET_ID + ")" +
                    ")";

    public BudgetDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_BUDGET);
        db.execSQL(CREATE_TABLE_INCOME_ENTRY);
        db.execSQL(CREATE_TABLE_EXPENSE_ENTRY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME_ENTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE_ENTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUDGET);
        onCreate(db);
    }

    public long addBudget(Budget budget) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Insert the budget
        values.put(COLUMN_MONTH, budget.getMonth());
        long budgetId = db.insert(TABLE_BUDGET, null, values);

        // If budget was inserted successfully, proceed with its entries
        if (budgetId != -1) {
            // Add all income entries
            for (IncomeEntry income : budget.getIncomeList()) {
                addIncomeEntry(income, (int) budgetId);
            }

            // Add all expense entries
            for (ExpenseEntry expense : budget.getExpenseList()) {
                addExpenseEntry(expense, (int) budgetId);
            }
        }

        return budgetId;
    }

    public Budget getBudgetByMonth(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_BUDGET,
                new String[]{COLUMN_BUDGET_ID, COLUMN_MONTH},
                COLUMN_MONTH + "=?",
                new String[]{month}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_ID));
            String monthFound = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH));
            cursor.close();

            Budget budget = new Budget(id, monthFound);
            budget.getIncomeList().addAll(getIncomesForBudget(id));
            budget.getExpenseList().addAll(getExpensesForBudget(id));

            return budget;
        }
        return null;
    }

    public long addIncomeEntry(IncomeEntry entry, int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME_BUDGET_ID, budgetId);
        values.put(COLUMN_INCOME_NAME, entry.getName());
        values.put(COLUMN_INCOME_AMOUNT, entry.getAmount());
        return db.insert(TABLE_INCOME_ENTRY, null, values);
    }

    public long addExpenseEntry(ExpenseEntry entry, int budgetId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EXPENSE_BUDGET_ID, budgetId);
        values.put(COLUMN_EXPENSE_NAME, entry.getName());
        values.put(COLUMN_EXPENSE_AMOUNT, entry.getAmount());
        return db.insert(TABLE_EXPENSE_ENTRY, null, values);
    }

    public List<IncomeEntry> getIncomesForBudget(int budgetId) {
        List<IncomeEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_INCOME_ENTRY,
                null,
                COLUMN_INCOME_BUDGET_ID + "=?",
                new String[]{String.valueOf(budgetId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_INCOME_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_INCOME_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_INCOME_AMOUNT));
                entries.add(new IncomeEntry(id, budgetId, name, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    public List<ExpenseEntry> getExpensesForBudget(int budgetId) {
        List<ExpenseEntry> entries = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXPENSE_ENTRY,
                null,
                COLUMN_EXPENSE_BUDGET_ID + "=?",
                new String[]{String.valueOf(budgetId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_NAME));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_EXPENSE_AMOUNT));
                entries.add(new ExpenseEntry(id, budgetId, name, amount));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return entries;
    }

    public void populateInitialData() {
        // Проверяем, пустая ли таблица бюджетов
        if (isBudgetTableEmpty()) {
            // Добавляем тестовые бюджеты
            Budget budgetMarch = new Budget("03/2025");
            Budget budgetApril = new Budget("04/2025");
            Budget budgetMay = new Budget("05/2025"); // Новый месяц

            long idMarch = addBudget(budgetMarch);
            long idApril = addBudget(budgetApril);
            long idMay = addBudget(budgetMay); // Добавляем в БД

            // Добавляем статьи доходов и расходов для марта
            if (idMarch != -1) {
                addIncomeEntry(new IncomeEntry("Зарплата", 75000), (int) idMarch);
                addIncomeEntry(new IncomeEntry("Фриланс", 10000), (int) idMarch);
                addExpenseEntry(new ExpenseEntry("Аренда", 25000), (int) idMarch);
                addExpenseEntry(new ExpenseEntry("Питание", 10000), (int) idMarch);
            }

            // Добавляем статьи доходов и расходов для апреля
            if (idApril != -1) {
                addIncomeEntry(new IncomeEntry("Зарплата", 80000), (int) idApril);
                addIncomeEntry(new IncomeEntry("Дивиденды", 3000), (int) idApril);
                addExpenseEntry(new ExpenseEntry("Аренда", 25000), (int) idApril);
                addExpenseEntry(new ExpenseEntry("Питание", 12000), (int) idApril);
            }

            // Добавляем статьи доходов и расходов для мая
            if (idMay != -1) {
                addIncomeEntry(new IncomeEntry("Зарплата", 82000), (int) idMay);
                addIncomeEntry(new IncomeEntry("Проценты по вкладам", 15000), (int) idMay);
                addExpenseEntry(new ExpenseEntry("Аренда", 25000), (int) idMay);
                addExpenseEntry(new ExpenseEntry("Питание", 11000), (int) idMay);
                addExpenseEntry(new ExpenseEntry("Развлечения", 5000), (int) idMay);
            }

            Log.d("tmp", "populateInitialData() отработал — добавлены март, апрель и май");
        }
    }

    private boolean isBudgetTableEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_BUDGET;
        Cursor cursor = db.rawQuery(query, null);
        boolean isEmpty = true;

        if (cursor.moveToFirst()) {
            int count = (int) cursor.getLong(0);
            isEmpty = count == 0;
        }

        cursor.close();
        return isEmpty;
    }

    public void printAllData() {
        List<Budget> budgets = getAllBudgets();

        if (budgets.isEmpty()) {
            Log.d("tmp", "База данных пуста.");
            return;
        }

        for (Budget budget : budgets) {
            printBudget(budget);
        }

        Log.d("tmp", "==============================");
    }

    public void printBudget(Budget budget) {
        Log.d("tmp", "==============================");
        Log.d("tmp", "Месяц: " + budget.getMonth());
        Log.d("tmp", "ID бюджета: " + budget.getId());

        // Доходы
        List<IncomeEntry> incomes = budget.getIncomeList();
        Log.d("tmp", "-- Доходы:");
        if (incomes.isEmpty()) {
            Log.d("tmp", "   Нет записей");
        } else {
            for (IncomeEntry income : incomes) {
                Log.d("tmp", "   - " + income.getName() + ": " + income.getAmount() + " ₽");
            }
        }

        // Расходы
        List<ExpenseEntry> expenses = budget.getExpenseList();
        Log.d("tmp", "-- Расходы:");
        if (expenses.isEmpty()) {
            Log.d("tmp", "   Нет записей");
        } else {
            for (ExpenseEntry expense : expenses) {
                Log.d("tmp", "   - " + expense.getName() + ": " + expense.getAmount() + " ₽");
            }
        }

        double totalIncome = incomes.stream().mapToDouble(IncomeEntry::getAmount).sum();
        double totalExpense = expenses.stream().mapToDouble(ExpenseEntry::getAmount).sum();
        double net = totalIncome - totalExpense;

        Log.d("tmp", "-- Итого:");
        Log.d("tmp", "   Доходы: " + totalIncome + " ₽");
        Log.d("tmp", "   Расходы: " + totalExpense + " ₽");
        Log.d("tmp", "   Чистый бюджет: " + net + " ₽");
    }

    public List<Budget> getAllBudgets() {
        List<Budget> budgets = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BUDGET;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BUDGET_ID));
                String month = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MONTH));

                budgets.add(new Budget(id, month));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return budgets;
    }

    public void clearAllData() {
        SQLiteDatabase db = this.getWritableDatabase();

        // Отключаем проверку внешних ключей на время удаления
        db.execSQL("PRAGMA foreign_keys=OFF");

        // Удаляем все данные
        db.delete(TABLE_INCOME_ENTRY, null, null);
        db.delete(TABLE_EXPENSE_ENTRY, null, null);
        db.delete(TABLE_BUDGET, null, null);

        // Сбрасываем счётчики автоинкремента
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLE_BUDGET + "'");
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLE_INCOME_ENTRY + "'");
        db.execSQL("DELETE FROM sqlite_sequence WHERE name='" + TABLE_EXPENSE_ENTRY + "'");

        db.execSQL("PRAGMA foreign_keys=ON");

        Log.d("BudgetDB", "Все данные и счётчики ID успешно сброшены.");
    }
}