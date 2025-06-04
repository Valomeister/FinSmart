package com.example.finsmart.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

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
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.budget_details_page.CategoryWithTotal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AppRepository {

    private final UserDao userDao;
    private final BudgetDao budgetDao;
    private final CategoryDao categoryDao;
    private final OperationDao operationDao;
    private final AppDatabase database;
    private MutableLiveData<Long> newBudgetId = new MutableLiveData<>();

    // cache for current month
    String cachedMonth = BudgetUtils.getCurrentMonth();
    private MutableLiveData<Budget> budgetCache = new MutableLiveData<>();
    private MutableLiveData<List<Category>> incomeCategoriesCache = new MutableLiveData<>();
    private MutableLiveData<List<Category>> expenseCategoriesCache = new MutableLiveData<>();
    private final Map<Integer, MutableLiveData<List<Operation>>> operationsByCategoryCache = new HashMap<>();

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

    public void insertBudgetWithCategories(Budget budget, List<Category> categories) {
        // Очистка БД
        database.clearAllTables();
        database.getOpenHelper().getWritableDatabase()
                .execSQL("DELETE FROM sqlite_sequence");

        // Добавляем пользователя
        User user = new User("Valery");
        userDao.insert(user);

        // Сначала бюджет
        long budgetId = budgetDao.insert(budget);

        // Обновляем категории с новым budgetId
        for (Category category : categories) {
            category.setBudgetId((int) budgetId);
        }

        categoryDao.insertAll(categories);
    }

    public List<Budget> getAllBudgetsForUser(int userId) {
        return budgetDao.getAllBudgetsForUser(userId);
    }

    public LiveData<Budget> getBudgetByMonth(String month) {
        if (month.equals(cachedMonth) && budgetCache.getValue() != null) {
            Log.d("tmp", "Бюджет взят из кэша для месяца: " + month);

            return budgetCache;
        }

        Log.d("tmp", "Бюджет запрошен из БД для месяца: " + month);

        LiveData<Budget> liveDataFromDb = budgetDao.getBudgetByMonthForUser(1, month);

        liveDataFromDb.observeForever(budget -> {
            if (!month.equals(cachedMonth)) return;

            Budget previous = budgetCache.getValue();
            if (previous != null && previous.equals(budget)) {
                Log.d("tmp", "Данные не изменились — обновление кэша пропущено");
                return;
            }

            Log.d("tmp", "Обновляем кэш с новыми данными");
            budgetCache.postValue(budget);
            cachedMonth = month;
        });

        return budgetCache;
    }


    // === Работа с категориями ===
    public void insertCategory(Category category) {
        categoryDao.insert(category);
    }

//    public LiveData<List<Category>> getIncomeCategoriesByBudget(int budgetId) {
//        return categoryDao.getCategoriesByBudgetAndType(budgetId, CategoryType.INCOME);
//    }

    public LiveData<List<Category>> getIncomeCategoriesByBudget(int budgetId) {
        if (incomeCategoriesCache.getValue() != null && !incomeCategoriesCache.getValue().isEmpty()) {
            Log.d("tmp", "Доходные категории взяты из кэша");
            return incomeCategoriesCache;
        }

        Log.d("tmp", "Доходные категории запрошены из БД");

        LiveData<List<Category>> liveDataFromDb = categoryDao.getCategoriesByBudgetAndType(budgetId, CategoryType.INCOME);

        Observer<List<Category>> dbObserver = categories -> {
            if (categories == null || categories.isEmpty()) return;

            List<Category> previous = incomeCategoriesCache.getValue();
            if (previous != null && previous.equals(categories)) {
                Log.d("tmp", "Доходные категории не изменились — обновление пропущено");
                return;
            }

            Log.d("tmp", "Обновляем кэш доходных категорий");
            incomeCategoriesCache.postValue(categories);
        };

        liveDataFromDb.observeForever(dbObserver);

        return incomeCategoriesCache;
    }

//    public LiveData<List<Category>> getExpenseCategoriesByBudget(int budgetId) {
//        return categoryDao.getCategoriesByBudgetAndType(budgetId, CategoryType.EXPENSE);
//    }

    public LiveData<List<Category>> getExpenseCategoriesByBudget(int budgetId) {
        if (expenseCategoriesCache.getValue() != null && !expenseCategoriesCache.getValue().isEmpty()) {
            Log.d("tmp", "Расходные категории взяты из кэша");
            return expenseCategoriesCache;
        }

        Log.d("tmp", "Расходные категории запрошены из БД");

        LiveData<List<Category>> liveDataFromDb = categoryDao.getCategoriesByBudgetAndType(budgetId, CategoryType.EXPENSE);

        Observer<List<Category>> dbObserver = categories -> {
            if (categories == null || categories.isEmpty()) return;

            List<Category> previous = expenseCategoriesCache.getValue();
            if (previous != null && previous.equals(categories)) {
                Log.d("tmp", "Расходные категории не изменились — обновление пропущено");
                return;
            }

            Log.d("tmp", "Обновляем кэш расходных категорий");
            expenseCategoriesCache.postValue(categories);
        };

        liveDataFromDb.observeForever(dbObserver);

        return expenseCategoriesCache;
    }

    public LiveData<List<CategoryWithTotal>> getCategoriesWithTotalByType(int budgetId, CategoryType type) {
        MutableLiveData<List<CategoryWithTotal>> result = new MutableLiveData<>();

        LiveData<List<Category>> categoriesByType;
        if (type == CategoryType.EXPENSE) {
            categoriesByType = getExpenseCategoriesByBudget(budgetId);
        } else {
            categoriesByType = getIncomeCategoriesByBudget(budgetId);
        }

        categoriesByType.observeForever(categories -> {
            List<CategoryWithTotal> list = new ArrayList<>();

            if (categories == null || categories.isEmpty()) {
                result.postValue(list);
                return;
            }

            // Для каждой категории получаем список операций
            for (Category category : categories) {
                LiveData<List<Operation>> operationsLiveData = getOperationsByCategory(category.getCategoryId());

                // Подписываемся на операции этой категории
                operationsLiveData.observeForever(operations -> {
                    int total = (int) calculateTotalAmount(operations);

                    // Ищем или создаём новую запись
                    CategoryWithTotal existing = findInList(result.getValue(), category.getCategoryId());
                    CategoryWithTotal newItem = new CategoryWithTotal(category, total);

                    if (existing == null) {
                        list.add(newItem);
                    } else {
                        list.remove(existing);
                        list.add(newItem);
                    }

                    result.postValue(list);
                });
            }
        });

        return result;
    }

    private double calculateTotalAmount(List<Operation> operations) {
        double total = 0;
        if (operations != null) {
            for (Operation op : operations) {
                total += op.getSum();
            }
        }
        return total;
    }

    private CategoryWithTotal findInList(List<CategoryWithTotal> list, int categoryId) {
        if (list == null) return null;
        for (CategoryWithTotal item : list) {
            if (item.category.getCategoryId() == categoryId) {
                return item;
            }
        }
        return null;
    }


    // === Работа с операциями ===
    public void insertOperation(Operation operation) {
        operationDao.insert(operation);
    }

//    public LiveData<List<Operation>> getOperationsByCategory(int categoryId) {
//        return operationDao.getOperationsByCategory(categoryId);
//    }

    public LiveData<List<Operation>> getOperationsByCategory(int categoryId) {
        // Сначала проверяем, есть ли данные в списке-кэше
        MutableLiveData<List<Operation>> liveData = operationsByCategoryCache.get(categoryId);

        if (liveData != null && liveData.getValue() != null && !liveData.getValue().isEmpty()) {
            Log.d("tmp", "Операции взяты из кэша (списка) для категории: " + categoryId);

            return liveData;
        }

        Log.d("tmp", "Операции запрошены из БД для категории: " + categoryId);

        if (liveData == null) {
            liveData = new MutableLiveData<>();
            operationsByCategoryCache.put(categoryId, liveData);
        }
        LiveData<List<Operation>> liveDataFromDb = operationDao.getOperationsByCategory(categoryId);

        Observer<List<Operation>> dbObserver = operations -> {
            if (operations == null || operations.isEmpty()) return;

            List<Operation> previous = operationsByCategoryCache.get(categoryId).getValue();
            if (previous != null && previous.equals(operations)) {
                Log.d("tmp", "Расходные категории не изменились — обновление пропущено");
                return;
            }

            Log.d("tmp", "Обновляем кэш расходных категорий");
            operationsByCategoryCache.get(categoryId).postValue(operations);
        };

        liveDataFromDb.observeForever(dbObserver);

        return operationsByCategoryCache.get(categoryId);


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
                        operationDao.insert(new Operation(5000, category.getCategoryId(), "2025-06-00"));
                    } else if (category.getType() == CategoryType.INCOME) {
                        operationDao.insert(new Operation(50000, category.getCategoryId(), "2025-06-00"));
                    }
                }


            } catch (Exception e) {
                Log.e("TEST_DB", "Ошибка при заполнении тестовыми данными", e);
            }
        }).start();
    }

    public LiveData<Long> createDefaultBudget(String month) {
        new Thread(() -> {
            try {
                // Очистка БД
                database.clearAllTables();
                database.getOpenHelper().getWritableDatabase()
                        .execSQL("DELETE FROM sqlite_sequence");

                User user = new User("Valery");
                long userId = userDao.insert(user);

                // Добавляем бюджет
                Budget newBudget = new Budget((int) userId, month);
                long budgetId = budgetDao.insert(newBudget);
                newBudget.setBudgetId((int)budgetId);

                // Создаём провайдер
                CategoryProvider.DefaultCategoryProvider provider = new CategoryProvider.DefaultCategoryProvider();

                // Получаем дефолтные категории
                List<Category> expenseCategories = provider.getExpenseCategories((int)budgetId);
                List<Category> incomeCategories = provider.getIncomeCategories((int)budgetId);

                // Объединяем в один список для удобства
                List<Category> allCategories = new ArrayList<>();
                allCategories.addAll(expenseCategories);
                allCategories.addAll(incomeCategories);

                // Вставляем категории в БД
                for (Category category : allCategories) {
                    long categoryId = categoryDao.insert(category);
                    category.setCategoryId((int) categoryId);
                }

                // === Добавляем операции по дням ===
                Random random = new Random();
                String[] dates = {"2025-06-01", "2025-06-02", "2025-06-03", "2025-06-04"};
                for (String date : dates) {
                    int operationCount = random.nextInt(3) + 2; // 2-4 операции на день

                    for (int i = 0; i < operationCount; i++) {
                        // Выбираем случайную категорию
                        Category randomCategory = allCategories.get(random.nextInt(allCategories.size()));
                        int categoryId = randomCategory.getCategoryId();
                        int amount;

                        if (randomCategory.getType() == CategoryType.EXPENSE) {
                            amount = random.nextInt(5000) + 500; // расход: 500–5500
                        } else {
                            amount = random.nextInt(10000) + 5000; // доход: 5000–15000
                        }

                        Operation operation = new Operation(amount, categoryId, date);
                        operationDao.insert(operation);
                    }
                }

                newBudgetId.postValue(budgetId);

            } catch (Exception e) {
                Log.e("TEST_DB", "Ошибка при создании бюджета", e);
            }
        }).start();

        return newBudgetId;
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
                        List<Operation> operations = operationDao.getOperationsByCategoryNonLineData(category.getCategoryId());
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

    public void removeDataFromDatabase() {
        new Thread(() -> {
            // Очистка БД
            database.clearAllTables();
            database.getOpenHelper().getWritableDatabase()
                    .execSQL("DELETE FROM sqlite_sequence");
        }).start();

    }
}