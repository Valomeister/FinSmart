package com.example.finsmart.main_activity.operations_page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateUtils {

    private static final Locale LOCALE = new Locale("ru"); // или "en" для английского
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Преобразует строковую дату в читаемый вид:
     * - Сегодня
     * - Вчера
     * - 1 июня
     */
    public static String getDisplayDate(String dateStr) {
        try {
            LocalDate inputDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            if (inputDate.isEqual(today)) {
                return "Сегодня";
            } else if (inputDate.isEqual(yesterday)) {
                return "Вчера";
            } else {
                // Формат: "1 июня", "15 июля"
                return inputDate.getDayOfMonth() + " " +
                        inputDate.getMonth().getDisplayName(TextStyle.FULL, LOCALE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dateStr; // Если не удалось распарсить — вернуть исходную дату
        }
    }

    /**
     * То же самое, но с годом: "1 июня 2025"
     */
    public static String getDisplayDateWithYear(String dateStr) {
        try {
            LocalDate inputDate = LocalDate.parse(dateStr, DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            LocalDate yesterday = today.minusDays(1);

            if (inputDate.isEqual(today)) {
                return "Сегодня";
            } else if (inputDate.isEqual(yesterday)) {
                return "Вчера";
            } else {
                return inputDate.getDayOfMonth() + " " +
                        inputDate.getMonth().getDisplayName(TextStyle.FULL, LOCALE) + " " +
                        inputDate.getYear();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return dateStr;
        }
    }
}