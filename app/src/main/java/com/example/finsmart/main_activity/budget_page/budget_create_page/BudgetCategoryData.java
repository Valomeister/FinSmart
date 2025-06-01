package com.example.finsmart.main_activity.budget_page.budget_create_page;

public class BudgetCategoryData {
    private String emoji;
    private String title;
    private String amount;
    private boolean isLast;

    public BudgetCategoryData(String emoji, String title, String amount, boolean isLast) {
        this.emoji = emoji;
        this.title = title;
        this.amount = amount;
        this.isLast = isLast;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public Boolean isLast() {
        return isLast;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}