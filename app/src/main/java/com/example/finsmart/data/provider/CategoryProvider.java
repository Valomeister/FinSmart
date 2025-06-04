package com.example.finsmart.data.provider;

import com.example.finsmart.data.model.Category;
import com.example.finsmart.data.model.common.FlowType;

import java.util.ArrayList;
import java.util.List;

public class CategoryProvider {

    // Внутренний статический класс, чтобы можно было использовать без экземпляра CategoryProvider
    public static class DefaultCategoryProvider {

        public List<Category> getExpenseCategories(int budgetId) {
            List<Category> expenseCategories = new ArrayList<>();

            expenseCategories.add(new Category(budgetId, "Аренда", FlowType.EXPENSE, 65000, "\uD83C\uDFE0"));
            expenseCategories.add(new Category(budgetId, "Питание", FlowType.EXPENSE, 10000, "\uD83E\uDD69"));
            expenseCategories.add(new Category(budgetId, "Развлечения", FlowType.EXPENSE, 9350, "\uD83C\uDF89"));
            expenseCategories.add(new Category(budgetId, "Одежда", FlowType.EXPENSE, 8000, "\uD83D\uDC55"));
            expenseCategories.add(new Category(budgetId, "Транспорт", FlowType.EXPENSE, 7500, "\uD83D\uDE97"));
            expenseCategories.add(new Category(budgetId, "Связь", FlowType.EXPENSE, 2000, "\uD83D\uDCDE"));

            return expenseCategories;
        }

        public List<Category> getIncomeCategories(int budgetId) {
            List<Category> incomeCategories = new ArrayList<>();

            incomeCategories.add(new Category(budgetId, "Зарплата", FlowType.INCOME, 65000, "\uD83D\uDCBC"));
            incomeCategories.add(new Category(budgetId, "Фриланс", FlowType.INCOME, 10000, "\uD83D\uDDA5\uFE0F"));
            incomeCategories.add(new Category(budgetId, "Вклады и облигации", FlowType.INCOME, 9350, "\uD83D\uDCC8"));
            incomeCategories.add(new Category(budgetId, "Другое", FlowType.INCOME, 5000, "\uD83E\uDDE9"));

            return incomeCategories;
        }

    }


}