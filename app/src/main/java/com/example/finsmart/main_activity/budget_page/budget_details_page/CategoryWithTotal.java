package com.example.finsmart.main_activity.budget_page.budget_details_page;

import com.example.finsmart.data.model.Category;

public class CategoryWithTotal {
    public final Category category;
    public final int totalAmount;

    public CategoryWithTotal(Category category, int totalAmount) {
        this.category = category;
        this.totalAmount = totalAmount;
    }
}