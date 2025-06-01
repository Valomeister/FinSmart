package com.example.finsmart.main_activity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class CurrencyUtils {

    // Форматтер для вывода числа в виде "1 234 567,89 ₽"
    private static final DecimalFormat formatter;

    // Инициализация формата
    static {
        // Устанавливаем русский локаль (для запятой как разделителя дробной части)
        formatter = (DecimalFormat) DecimalFormat.getCurrencyInstance(new Locale("ru", "RU"));
        formatter.setPositivePrefix("");  // Убираем символ валюты при форматировании
        formatter.setNegativePrefix("-"); // Для отрицательных чисел
        formatter.setCurrency(java.util.Currency.getInstance("RUB")); // Можно указать другую валюту
    }

    /**
     * Преобразует строку вида "2 134 124,12 ₽" в double.
     */
    public static double parseStringToDouble(String input) throws ParseException {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Входная строка не может быть пустой.");
        }

        // Убираем все пробеллы, символ валюты и нормализуем десятичный разделитель
        String cleaned = input.replaceAll("[^\\d,]", "").replace(',', '.');

        return Double.parseDouble(cleaned);
    }

    /**
     * Преобразует double в строку вида "1 234 567,89 ₽".
     */
    // Обновлённый метод:
    public static String formatDoubleToString(double value, int decimalPlaces) {
        // Создаём форматтер с нужным количеством знаков после запятой
        String pattern = "#,##0";
        if (decimalPlaces > 0) {
            pattern += ".";
            for (int i = 0; i < decimalPlaces; i++) {
                pattern += "0";
            }
        }
        pattern += ""; // Завершаем паттерн

        DecimalFormat formatter2 = new DecimalFormat(pattern);

        // Форматируем значение
        String formatted = formatter2.format(value);

        // Убираем возможные обозначения валюты и добавляем "₽"
        String amount = formatted.replaceAll("[^\\d.,]+", "").trim()
                .replace(',', ' ');

        return amount + " ₽";
    }

    // Тестирование
    public static void main(String[] args) {
        try {
            String input = "2 134 124,12 ₽";

            // Конвертация строки в double
            double number = CurrencyUtils.parseStringToDouble(input);
            System.out.println("Число: " + number); // 2134124.12

            // Обратная конвертация
            String output = CurrencyUtils.formatDoubleToString(number, 0);
            System.out.println("Форматированное значение: " + output); // 2 134 124,12 ₽

        } catch (ParseException e) {
            System.err.println("Ошибка парсинга: " + e.getMessage());
        }
    }
}