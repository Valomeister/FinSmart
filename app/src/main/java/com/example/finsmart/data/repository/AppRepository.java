package com.example.finsmart.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.finsmart.data.dao.BudgetDao;
import com.example.finsmart.data.dao.CategoryDao;
import com.example.finsmart.data.dao.OperationDao;
import com.example.finsmart.data.dao.UserDao;
import com.example.finsmart.data.database.AppDatabase;
import com.example.finsmart.data.model.Budget;
import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.Operation;
import com.example.finsmart.data.model.User;
import com.example.finsmart.data.model.common.CategoryType;
import com.example.finsmart.data.provider.CategoryProvider;

import java.util.ArrayList;
import java.util.List;

public class AppRepository {

    private final UserDao userDao;
    private final BudgetDao budgetDao;
    private final CategoryDao categoryDao;
    private final OperationDao operationDao;
    private final AppDatabase database;

    // Конструктор
    public AppRepository(UserDao userDao, BudgetDao budgetDao, CategoryDao categoryDao,
                         OperationDao operationDao, AppDatabase database) {
        this.userDao = userDao;
        this.budgetDao = budgetDao;
        this.categoryDao = categoryDao;
        this.operationDao = operationDao;
        this.database = database;
    }

    // === Работа с пользователями ===
    public long insertUser(User user) {
        return userDao.insert(user);
    }

    public List<User> getAllUsersSync() {
        return userDao.getAllUsers(); // просто возвращаем список
    }

    // === Работа с бюджетами ===
    public long createBudget(int userId, String month) {
        Budget budget = new Budget(userId, month);
        return budgetDao.insert(budget);
    }

    public List<Budget> getAllBudgetsForUser(int userId) {
        return budgetDao.getAllBudgetsForUser(userId);
    }

    public Budget getBudgetByMonthForUser(int userId, String month) {
        return budgetDao.getBudgetByMonthForUser(userId, month);
    }

    // === Работа с категориями ===
    public void insertCategory(Category category) {
        categoryDao.insert(category);
    }

    public List<Category> getIncomeCategoriesByBudget(int budgetId) {
        return categoryDao.getCategoriesByBudgetAndType(budgetId, CategoryType.INCOME);
    }

    public List<Category> getExpenseCategoriesByBudget(int budgetId) {
        return categoryDao.getCategoriesByBudgetAndType(budgetId, CategoryType.EXPENSE);
    }

    // === Работа с операциями ===
    public void insertOperation(Operation operation) {
        operationDao.insert(operation);
    }

    public List<Operation> getOperationsByCategory(int categoryId) {
        return operationDao.getOperationsByCategory(categoryId);
    }

    // === Тестовые данные ===
    public void populateWithTestData() {
        new Thread(() -> {
            try {
                // Очистка БД
                database.clearAllTables();
                database.getOpenHelper().getWritableDatabase()
                        .execSQL("DELETE FROM sqlite_sequence");

                // Добавляем пользователя
                User user = new User("Valery");
                long userId = userDao.insert(user);

                // Добавляем бюджет за январь
                Budget janBudget = new Budget((int) userId, "06/25");
                int budgetId = (int) budgetDao.insert(janBudget);
                janBudget.setBudgetId(budgetId);

                // Создаём провайдер
                CategoryProvider.DefaultCategoryProvider provider = new CategoryProvider.DefaultCategoryProvider();

                // Получаем дефолтные категории
                List<Category> expenseCategories = provider.getExpenseCategories(budgetId);
                List<Category> incomeCategories = provider.getIncomeCategories(budgetId);

                // Объединяем в один список для удобства
                List<Category> allCategories = new ArrayList<>();
                allCategories.addAll(expenseCategories);
                allCategories.addAll(incomeCategories);

                // Вставляем категории в БД
                for (Category category : allCategories) {
                    long categoryId = categoryDao.insert(category);
                    category.setCategoryId((int) categoryId);
                }

                // === ОПЕРАЦИИ (по одной на категорию для теста) ===
                for (Category category : allCategories) {
                    if (category.getType() == CategoryType.EXPENSE) {
                        operationDao.insert(new Operation(5000, category.getCategoryId()));
                    } else if (category.getType() == CategoryType.INCOME) {
                        operationDao.insert(new Operation(50000, category.getCategoryId()));
                    }
                }

                printDatabaseContent();

            } catch (Exception e) {
                Log.e("TEST_DB", "Ошибка при заполнении тестовыми данными", e);
            }
        }).start();
    }

    public void printDatabaseContent() {
        new Thread(() -> {
            List<User> users = userDao.getAllUsers(); // должен быть синхронным

            if (users == null || users.isEmpty()) {
                Log.d("DB_DUMP", "Нет пользователей в базе");
                return;
            }

            for (User user : users) {
                Log.d("DB_DUMP", "===============================");
                Log.d("DB_DUMP", "Пользователь: ID=" + user.getUserId() + ", Имя='" + user.getName() + "'");

                List<Budget> budgets = budgetDao.getAllBudgetsForUser(user.getUserId());
                if (budgets == null || budgets.isEmpty()) {
                    Log.d("DB_DUMP", " ➤ Нет бюджетов");
                    continue;
                }

                for (Budget budget : budgets) {
                    Log.d("DB_DUMP", " ➤ Бюджет: ID=" + budget.getBudgetId() + ", Месяц='" + budget.getMonth() + "'");

                    List<Category> categories = categoryDao.getCategoriesByBudget(budget.getBudgetId());

                    if (categories.isEmpty()) {
                        Log.d("DB_DUMP", "   ➝ Нет категорий");
                        continue;
                    }

                    for (Category category : categories) {
                        String typeLabel = (category.getType() == CategoryType.INCOME) ? "Доходная" : "Расходная";

                        Log.d("DB_DUMP", "   ➝ " + typeLabel + " категория: ID=" + category.getCategoryId()
                                + ", Название='" + category.getName()
                                + "', Лимит=" + category.getPlannedLimit()
                                + ", Эмодзи=" + category.getEmoji());

                        List<Operation> operations = getOperationsByCategory(category.getCategoryId());
                        for (Operation op : operations) {
                            Log.d("DB_DUMP", "     ➠ Операция: ID=" + op.getOperationId()
                                    + ", Сумма=" + op.getSum());
                        }
                    }
                }
            }

            Log.d("DB_DUMP", "===============================");
        }).start();
    }
}