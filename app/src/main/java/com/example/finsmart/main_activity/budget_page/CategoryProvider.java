package com.example.finsmart.main_activity.budget_page;

import java.util.ArrayList;
import java.util.List;

public class CategoryProvider {

    // Списки категорий
    private final List<String> incomeCategories = new ArrayList<>();
    private final List<String> expenseCategories = new ArrayList<>();

    public CategoryProvider() {
        // Заполняем дефолтные категории
        loadDefaultCategories();
    }

    // --- Инициализация ---

    private void loadDefaultCategories() {
        // Доходы
        incomeCategories.addAll(List.of(
                "Зарплата",
                "Фриланс",
                "Дивиденды",
                "Проценты по вкладам",
                "Подработка"
        ));

        // Расходы
        expenseCategories.addAll(List.of(
                "Аренда",
                "Питание",
                "Транспорт",
                "Комунальные услуги",
                "Развлечения",
                "Здоровье",
                "Образование"
        ));
    }

    // --- Получение списков ---

    public List<String> getIncomeCategories() {
        return new ArrayList<>(incomeCategories); // возвращаем копию для безопасности
    }

    public List<String> getExpenseCategories() {
        return new ArrayList<>(expenseCategories);
    }

    // --- Добавление пользовательских категорий ---

    public boolean addIncomeCategory(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        if (incomeCategories.contains(name)) return false;

        incomeCategories.add(name);
        return true;
    }

    public boolean addExpenseCategory(String name) {
        if (name == null || name.trim().isEmpty()) return false;
        if (expenseCategories.contains(name)) return false;

        expenseCategories.add(name);
        return true;
    }

    // --- Удаление пользовательских категорий ---

    public boolean removeIncomeCategory(String name) {
        return incomeCategories.remove(name);
    }

    public boolean removeExpenseCategory(String name) {
        return expenseCategories.remove(name);
    }

    // --- Проверка наличия категории ---

    public boolean isDefaultIncomeCategory(String name) {
        return List.of(
                "Зарплата",
                "Фриланс",
                "Дивиденды",
                "Проценты по вкладам",
                "Подработка"
        ).contains(name);
    }

    public boolean isDefaultExpenseCategory(String name) {
        return List.of(
                "Аренда",
                "Питание",
                "Транспорт",
                "Комунальные услуги",
                "Развлечения",
                "Здоровье",
                "Образование"
        ).contains(name);
    }

    static int[] getDefaultColors() {
        // Пример цветов (R.color должно быть заменено на ваши ресурсы)
        int[] colors = new int[]{
                0xFF7D79FF,
                0xFFA8A5FF,
                0xFFAB8BFA,
                0xFFCE9EF9,
                0xFFE4C7FE,
                0xFFDCCEFF,
                0xFFF0EAFF,
        };

        return colors;
    }
}