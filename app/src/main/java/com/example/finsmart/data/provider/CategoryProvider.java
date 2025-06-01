package com.example.finsmart.data.provider;

import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.common.CategoryType;

import java.util.ArrayList;
import java.util.List;

public class CategoryProvider {

    // Внутренний статический класс, чтобы можно было использовать без экземпляра CategoryProvider
    public static class DefaultCategoryProvider {

        public List<Category> getExpenseCategories(int budgetId) {
            List<Category> expenseCategories = new ArrayList<>();

            expenseCategories.add(new Category(budgetId, "Аренда", CategoryType.EXPENSE, 65000, "\uD83C\uDFE0"));
            expenseCategories.add(new Category(budgetId, "Питание", CategoryType.EXPENSE, 10000, "\uD83E\uDD69"));
            expenseCategories.add(new Category(budgetId, "Развлечения", CategoryType.EXPENSE, 9350, "\uD83C\uDF89"));
            expenseCategories.add(new Category(budgetId, "Одежда", CategoryType.EXPENSE, 8000, "\uD83D\uDC55"));
            expenseCategories.add(new Category(budgetId, "Транспорт", CategoryType.EXPENSE, 7500, "\uD83D\uDE97"));
            expenseCategories.add(new Category(budgetId, "Связь", CategoryType.EXPENSE, 2000, "\uD83D\uDCDE"));

            return expenseCategories;
        }

        public List<Category> getIncomeCategories(int budgetId) {
            List<Category> incomeCategories = new ArrayList<>();

            incomeCategories.add(new Category(budgetId, "Зарплата", CategoryType.INCOME, 65000, "\uD83D\uDCBC"));
            incomeCategories.add(new Category(budgetId, "Фриланс", CategoryType.INCOME, 10000, "\uD83D\uDDA5\uFE0F"));
            incomeCategories.add(new Category(budgetId, "Вклады и облигации", CategoryType.INCOME, 9350, "\uD83D\uDCC8"));
            incomeCategories.add(new Category(budgetId, "Другое", CategoryType.INCOME, 5000, "\uD83E\uDDE9"));

            return incomeCategories;
        }
    }
}