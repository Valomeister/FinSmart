package com.example.finsmart.main_activity.budget_page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class BudgetUtils {

    // Приватный конструктор, чтобы нельзя было создать экземпляр
    private BudgetUtils() {
        throw new UnsupportedOperationException("Этот класс нельзя инстанцировать");
    }

    /**
     * Возвращает текущую дату в формате MM/yyyy (например, 04/2025)
     */
    public static String getCurrentMonth() {
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        return currentDate.format(formatter);
    }
}