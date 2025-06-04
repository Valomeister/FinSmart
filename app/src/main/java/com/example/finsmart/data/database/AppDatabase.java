package com.example.finsmart.data.database;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import android.content.Context;

import com.example.finsmart.data.dao.BudgetDao;
import com.example.finsmart.data.dao.CategoryDao;
import com.example.finsmart.data.dao.OperationDao;
import com.example.finsmart.data.dao.UserDao;
import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.Operation;
import com.example.finsmart.data.model.User;

@Database(entities = {User.class, Budget.class, Category.class, Operation.class}, version = 7)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract BudgetDao budgetDao();
    public abstract OperationDao operationDao();
    public abstract CategoryDao categoryDao();


    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "finsmart_database")
//                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}