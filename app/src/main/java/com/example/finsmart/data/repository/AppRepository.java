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
import com.example.finsmart.data.model.common.FlowType;
import com.example.finsmart.data.provider.CategoryProvider;
import com.example.finsmart.main_activity.budget_page.BudgetUtils;
import com.example.finsmart.main_activity.budget_page.budget_details_page.CategoryWithTotal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Flow;

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
//        return categoryDao.getCategoriesByBudgetAndType(budgetId, FlowType.INCOME);
//    }

    public LiveData<List<Category>> getIncomeCategoriesByBudget(int budgetId) {
        if (incomeCategoriesCache.getValue() != null && !incomeCategoriesCache.getValue().isEmpty()) {
            Log.d("tmp", "Доходные категории взяты из кэша");
            return incomeCategoriesCache;
        }

        Log.d("tmp", "Доходные категории запрошены из БД");

        LiveData<List<Category>> liveDataFromDb = categoryDao.getCategoriesByBudgetAndType(budgetId, FlowType.INCOME);

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
//        return categoryDao.getCategoriesByBudgetAndType(budgetId, FlowType.EXPENSE);
//    }

    public LiveData<List<Category>> getExpenseCategoriesByBudget(int budgetId) {
        if (expenseCategoriesCache.getValue() != null && !expenseCategoriesCache.getValue().isEmpty()) {
            Log.d("tmp", "Расходные категории взяты из кэша");
            return expenseCategoriesCache;
        }

        Log.d("tmp", "Расходные категории запрошены из БД");

        LiveData<List<Category>> liveDataFromDb = categoryDao.getCategoriesByBudgetAndType(budgetId, FlowType.EXPENSE);

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

    public LiveData<List<CategoryWithTotal>> getCategoriesWithTotalByType(int budgetId, FlowType type) {
        MutableLiveData<List<CategoryWithTotal>> result = new MutableLiveData<>();

        LiveData<List<Category>> categoriesByType;
        if (type == FlowType.EXPENSE) {
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

    public LiveData<List<Operation>> getOperationsByBudget(int budgetId) {
        return operationDao.getOperationsByBudget(budgetId);
    }

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


//    public LiveData<List<OperationWithDate>> getGroupedOperationsByDay(int budgetId) {
//        MutableLiveData<List<OperationWithDate>> result = new MutableLiveData<>();
//
//        operationDao.getAllOperations().observeForever(operations -> {
//            Map<String, List<Operation>> grouped = new LinkedHashMap<>();
//
//            if (operations != null) {
//                for (Operation op : operations) {
//                    grouped.putIfAbsent(op.getDate(), new ArrayList<>());
//                    grouped.get(op.getDate()).add(op);
//                }
//
//                List<OperationWithDate> list = new ArrayList<>();
//                for (Map.Entry<String, List<Operation>> entry : grouped.entrySet()) {
//                    list.add(new OperationWithDate(entry.getKey(), entry.getValue()));
//                }
//
//                result.postValue(list);
//            }
//        });
//
//        return result;
//    }


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

                // Получаем категории
                CategoryProvider.DefaultCategoryProvider provider = new CategoryProvider.DefaultCategoryProvider();
                List<Category> allCategories = new ArrayList<>();
                List<Category> expenseCategories = provider.getExpenseCategories((int) budgetId);
                List<Category> incomeCategories = provider.getIncomeCategories((int) budgetId);

                allCategories.addAll(expenseCategories);
                allCategories.addAll(incomeCategories);

                // Вставляем все категории
                for (Category category : allCategories) {
                    long categoryId = categoryDao.insert(category);
                    category.setCategoryId((int) categoryId);
                }

                // === Добавляем операции вручную для каждого дня ===
                addOperationsForDay("2025-06-01", (int) budgetId, categoryDao, operationDao);
                addOperationsForDay("2025-06-02", (int) budgetId, categoryDao, operationDao);
                addOperationsForDay("2025-06-03", (int) budgetId, categoryDao, operationDao);
                addOperationsForDay("2025-06-04", (int) budgetId, categoryDao, operationDao);

                newBudgetId.postValue(budgetId);



            } catch (Exception e) {
                Log.e("TEST_DB", "Ошибка при создании бюджета", e);
            }
        }).start();

        return newBudgetId;
    }

    private void addOperationsForDay(String date, int budgetId,
                                     CategoryDao categoryDao,
                                     OperationDao operationDao) {

        // Получаем все категории для этого бюджета
        List<Category> categories = categoryDao.getCategoriesByBudget(budgetId);

        if (categories == null || categories.isEmpty()) return;

        // Выбираем конкретные категории
        Category rent = getCategoryByName(categories, "Аренда");
        Category food = getCategoryByName(categories, "Питание");
        Category transport = getCategoryByName(categories, "Транспорт");
        Category salary = getCategoryByName(categories, "Зарплата");
        Category freelance = getCategoryByName(categories, "Фриланс");

        if ("2025-06-01".equals(date)) {
            operationDao.insert(new Operation(6000, rent.getCategoryId(), rent.getName(), date, "Арендодатель", FlowType.EXPENSE));
            operationDao.insert(new Operation(800, food.getCategoryId(), food.getName(), date, "Пятерочка", FlowType.EXPENSE));
            operationDao.insert(new Operation(150, transport.getCategoryId(), transport.getName(), date, "Лукойл", FlowType.EXPENSE));
            operationDao.insert(new Operation(7000, salary.getCategoryId(), salary.getName(), date, "Завод", FlowType.INCOME));
        }

        else if ("2025-06-02".equals(date)) {
            operationDao.insert(new Operation(900, food.getCategoryId(), food.getName(), date, "Ресторан \"Хз вообще\"", FlowType.EXPENSE));
            operationDao.insert(new Operation(200, transport.getCategoryId(), transport.getName(), date, "МосТранспорт", FlowType.EXPENSE));
            operationDao.insert(new Operation(5000, freelance.getCategoryId(), freelance.getName(), date, "FL.RU", FlowType.INCOME));
        }

        else if ("2025-06-03".equals(date)) {
            operationDao.insert(new Operation(1000, food.getCategoryId(), food.getName(), date, "Вкусно и точка", FlowType.EXPENSE));
            operationDao.insert(new Operation(300, transport.getCategoryId(), transport.getName(), date, "МосГосПарковка", FlowType.EXPENSE));
            operationDao.insert(new Operation(2000, salary.getCategoryId(), salary.getName(), date, "Завод", FlowType.INCOME));
        }

        else if ("2025-06-04".equals(date)) {
            operationDao.insert(new Operation(1200, food.getCategoryId(), food.getName(), date, "ВкусВилл", FlowType.EXPENSE));
            operationDao.insert(new Operation(100, transport.getCategoryId(), transport.getName(), date, "МосТранспорт", FlowType.EXPENSE));
            operationDao.insert(new Operation(4000, freelance.getCategoryId(), freelance.getName(), date, "Фриланс", FlowType.INCOME));
        }
    }

    private Category getCategoryByName(List<Category> categories, String name) {
        for (Category c : categories) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
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
                        String typeLabel = (category.getType() == FlowType.INCOME) ? "Доходная" : "Расходная";

                        Log.d("DB_DUMP", "   ➝ " + typeLabel + " категория: ID=" + category.getCategoryId()
                                + ", Название='" + category.getName()
                                + "', Лимит=" + category.getPlannedLimit()
                                + ", Эмодзи=" + category.getEmoji());
                        List<Operation> operations = operationDao.getOperationsByCategoryNonLineData(category.getCategoryId());
                        for (Operation op : operations) {
                            Log.d("DB_DUMP", "     ➠ Операция: ID=" + op.getOperationId()
                                    + ", Сумма=" + op.getSum() +  ", Дата =" + op.getDate());
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